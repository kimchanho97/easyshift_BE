package com.burntoburn.easyshift.service;

import com.burntoburn.easyshift.config.jwt.TokenProvider;
import com.burntoburn.easyshift.dto.schedule.res.ScheduleDetailDTO;
import com.burntoburn.easyshift.dto.schedule.res.ScheduleSummaryDTO;
import com.burntoburn.easyshift.dto.shift.res.AssignedShiftDTO;
import com.burntoburn.easyshift.dto.shift.res.ShiftDateDTO;
import com.burntoburn.easyshift.dto.shift.res.ShiftGroupDTO;
import com.burntoburn.easyshift.dto.shift.res.ShiftKey;
import com.burntoburn.easyshift.dto.store.req.StoreCreateRequest;
import com.burntoburn.easyshift.dto.store.res.StoreScheduleResponseDTO;
import com.burntoburn.easyshift.entity.schedule.Schedule;
import com.burntoburn.easyshift.entity.schedule.Shift;
import com.burntoburn.easyshift.entity.store.Store;
import com.burntoburn.easyshift.entity.store.UserStore;
import com.burntoburn.easyshift.entity.user.User;
import com.burntoburn.easyshift.repository.schedule.ScheduleRepository;
import com.burntoburn.easyshift.repository.store.StoreRepository;
import com.burntoburn.easyshift.repository.store.UserStoreRepository;
import com.burntoburn.easyshift.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;
    private final UserRepository userRepository;
    private final UserStoreRepository userStoreRepository;
    private final TokenProvider tokenProvider;
    // 추가: 스케줄 관련 조회를 위해 필요함
    private final ScheduleRepository scheduleRepository;
    public Store getStoreById(Long storeId) {
        return storeRepository.findById(storeId)
                .orElseThrow(() -> new RuntimeException("해당 매장을 찾을 수 없습니다. id: " + storeId));
    }

    public Store createStore(StoreCreateRequest request) {
        Store store = Store.builder()
                .storeName(request.getStoreName())
                .storeCode(UUID.randomUUID())
                .build();

        return storeRepository.save(store);
    }

    public void deleteStore(Long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new RuntimeException("해당 매장을 찾을 수 없습니다. id: " + storeId));

        storeRepository.delete(store);
    }

    public List<String> getStoreNamesByUserId(Long userId) {
        List<UserStore> userStores = userStoreRepository.findAllByUserId(userId);
        if (userStores.isEmpty()) {
            throw new RuntimeException("해당 사용자와 연결된 매장이 없습니다.");
        }

        return userStores.stream()
                .map(userStore -> userStore.getStore().getStoreName())
                .collect(Collectors.toList());
    }

    public List<String> linkStoreToUser(String token, Long storeId) {
        Long userId = tokenProvider.getUserIdFromToken(token);

        Store store = getStoreById(storeId);

        boolean exists = userStoreRepository.existsByUserIdAndStoreId(userId, storeId);
        if (!exists) {
            // 실제 DB 조회 없이 프록시 객체를 이용하여 연관관계 설정
            User user = userRepository.getReferenceById(userId);

            UserStore userStore = UserStore.builder()
                    .user(user)
                    .store(store)
                    .build();

            userStoreRepository.save(userStore);
        }

        return userStoreRepository.findAllByUserId(userId)
                .stream()
                .map(us -> us.getStore().getStoreName())
                .collect(Collectors.toList());
    }

    // -----------------------------------------
    // 매장 스케줄 조회 기능
    // storeId와 (선택적) scheduleId를 받아서
    // 매장 정보, 전체 스케줄 목록, 그리고 선택된 스케줄의 상세 정보를 반환합니다.
    // -----------------------------------------
    public StoreScheduleResponseDTO getStoreSchedule(Long storeId, Optional<Long> scheduleIdOptional) {
        // 1. 매장 조회
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new RuntimeException("매장을 찾을 수 없습니다. id: " + storeId));

        // 2. 전체 스케줄 목록 구성 (드롭다운에 사용)
        List<ScheduleSummaryDTO> scheduleSummaries = store.getSchedules().stream()
                .map(schedule -> ScheduleSummaryDTO.builder()
                        .scheduleId(schedule.getId())
                        .scheduleName(schedule.getScheduleName())
                        .build())
                .collect(Collectors.toList());

        // 3. 선택된 스케줄 결정: 요청에 scheduleId가 있으면 해당 스케줄, 없으면 첫 번째 스케줄 선택
        Schedule selectedSchedule;
        if (scheduleIdOptional.isPresent()) {
            selectedSchedule = scheduleRepository.findById(scheduleIdOptional.get())
                    .orElseThrow(() -> new RuntimeException("스케줄을 찾을 수 없습니다. id: " + scheduleIdOptional.get()));
        } else {
            selectedSchedule = store.getSchedules().get(0);
        }

        // 4. 선택된 스케줄 내 Shift 데이터 매핑
        List<Shift> shifts = selectedSchedule.getShifts().getList();

        // 그룹화 기준: 같은 shiftName, startTime, endTime 인 Shift들을 하나의 그룹으로 묶습니다.
        Map<ShiftKey, List<Shift>> groupedByShiftType = shifts.stream()
                .collect(Collectors.groupingBy(
                        shift -> new ShiftKey(shift.getShiftName(), shift.getStartTime(), shift.getEndTime())
                ));

        List<ShiftGroupDTO> shiftGroupDTOs = new ArrayList<>();
        for (Map.Entry<ShiftKey, List<Shift>> entry : groupedByShiftType.entrySet()) {
            ShiftKey key = entry.getKey();
            List<Shift> shiftGroup = entry.getValue();

            // 같은 shift 그룹 내에서 날짜별로 그룹화
            Map<LocalDate, List<Shift>> dateGrouped = shiftGroup.stream()
                    .collect(Collectors.groupingBy(Shift::getShiftDate));

            List<ShiftDateDTO> shiftDateDTOs = new ArrayList<>();
            for (Map.Entry<LocalDate, List<Shift>> dateEntry : dateGrouped.entrySet()) {
                LocalDate date = dateEntry.getKey();
                List<AssignedShiftDTO> assignedShifts = dateEntry.getValue().stream()
                        .map(shift -> AssignedShiftDTO.builder()
                                .shiftId(shift.getId())
                                .userId(shift.getUser().getId())
                                .userName(shift.getUser().getName())
                                .build())
                        .collect(Collectors.toList());

                ShiftDateDTO shiftDateDTO = ShiftDateDTO.builder()
                        .date(date.toString()) // 필요에 따라 포맷 변환 가능
                        .assignedShifts(assignedShifts)
                        .build();

                shiftDateDTOs.add(shiftDateDTO);
            }
            // 날짜별 데이터 정렬 (예: 오름차순)
            shiftDateDTOs.sort(Comparator.comparing(ShiftDateDTO::getDate));

            ShiftGroupDTO shiftGroupDTO = ShiftGroupDTO.builder()
                    .shiftName(key.getShiftName())
                    .startTime(key.getStartTime().toString())
                    .endTime(key.getEndTime().toString())
                    .dates(shiftDateDTOs)
                    .build();

            shiftGroupDTOs.add(shiftGroupDTO);
        }

        // 5. 선택된 스케줄 상세 DTO 구성
        ScheduleDetailDTO scheduleDetailDTO = ScheduleDetailDTO.builder()
                .scheduleId(selectedSchedule.getId())
                .scheduleName(selectedSchedule.getScheduleName())
                .shifts(shiftGroupDTOs)
                .build();

        // 6. 최종 응답 DTO 구성 (매장 정보, 전체 스케줄 목록, 선택된 스케줄 상세 정보)
        return StoreScheduleResponseDTO.builder()
                .storeId(store.getId())
                .storeName(store.getStoreName())
                .schedules(scheduleSummaries)
                .selectedSchedule(scheduleDetailDTO)
                .build();
    }
}
