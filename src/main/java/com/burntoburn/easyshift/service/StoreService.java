package com.burntoburn.easyshift.service;

import com.burntoburn.easyshift.config.jwt.TokenProvider;
import com.burntoburn.easyshift.dto.schedule.res.ScheduleDetailDTO;
import com.burntoburn.easyshift.dto.schedule.res.ScheduleSummaryDTO;
import com.burntoburn.easyshift.dto.shift.res.AssignedShiftDTO;
import com.burntoburn.easyshift.dto.shift.res.ShiftDateDTO;
import com.burntoburn.easyshift.dto.shift.res.ShiftGroupDTO;
import com.burntoburn.easyshift.dto.shift.res.ShiftKey;
import com.burntoburn.easyshift.dto.store.req.StoreCreateRequest;
import com.burntoburn.easyshift.dto.store.res.StoreDto;
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

    public Store updateStore(Long storeId, String newStoreName) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new RuntimeException("해당 매장을 찾을 수 없습니다. id: " + storeId));

        store.setStoreName(newStoreName);
        return storeRepository.save(store);
    }

    public List<StoreDto> getStoreNamesByUserId(Long userId) {
        List<UserStore> userStores = userStoreRepository.findAllByUserId(userId);
        if (userStores.isEmpty()) {
            throw new RuntimeException("해당 사용자와 연결된 매장이 없습니다.");
        }

        return userStores.stream()
                .map(userStore -> {
                    Store store = userStore.getStore();
                    return new StoreDto(store.getId(), store.getStoreName());
                })
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

    /**
     * 매장 조회 시 storeId와 선택된 scheduleId(Optional)를 받아 응답 DTO를 생성합니다.
     * scheduleId가 없으면 해당 매장의 첫 번째 스케줄을 선택합니다.
     */
    public StoreScheduleResponseDTO getStoreSchedule(Long storeId, Optional<Long> scheduleId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new RuntimeException("해당 매장을 찾을 수 없습니다. id: " + storeId));

        List<ScheduleSummaryDTO> scheduleSummaries = mapToScheduleSummaries(store);
        Schedule targetSchedule = selectSchedule(store, scheduleId);
        ScheduleDetailDTO scheduleDetailDTO = mapToScheduleDetailDTO(targetSchedule);

        return StoreScheduleResponseDTO.builder()
                .storeId(store.getId())
                .schedules(scheduleSummaries)
                .selectedSchedule(scheduleDetailDTO)
                .build();
    }

    private List<ScheduleSummaryDTO> mapToScheduleSummaries(Store store) {
        return store.getSchedules().stream()
                .map(s -> ScheduleSummaryDTO.builder()
                        .scheduleId(s.getId())
                        .scheduleName(s.getScheduleName())
                        .build())
                .collect(Collectors.toList());
    }

    private Schedule selectSchedule(Store store, Optional<Long> scheduleId) {
        return scheduleId
                .map(id -> store.getSchedules().stream()
                        .filter(s -> s.getId().equals(id))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("선택한 스케줄을 찾을 수 없습니다.")))
                .orElseGet(() -> store.getSchedules().stream()
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("매장에 스케줄이 존재하지 않습니다.")));
    }

    private ScheduleDetailDTO mapToScheduleDetailDTO(Schedule schedule) {
        // Shifts 일급 컬렉션 내부의 Shift 리스트는 getList()를 통해 가져옵니다.
        List<Shift> shifts = schedule.getShifts().getList();
        Map<ShiftKey, List<Shift>> groupedByKey = groupShiftsByKey(shifts);

        List<ShiftGroupDTO> shiftGroups = groupedByKey.entrySet().stream()
                .map(entry -> mapShiftGroupToDTO(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

        return ScheduleDetailDTO.builder()
                .scheduleId(schedule.getId())
                .scheduleName(schedule.getScheduleName())
                .shifts(shiftGroups)
                .build();
    }

    private Map<ShiftKey, List<Shift>> groupShiftsByKey(List<Shift> shifts) {
        return shifts.stream()
                .collect(Collectors.groupingBy(
                        shift -> new ShiftKey(shift.getShiftName(), shift.getStartTime(), shift.getEndTime())
                ));
    }

    private ShiftGroupDTO mapShiftGroupToDTO(ShiftKey key, List<Shift> shifts) {
        // 그룹의 식별자는 그룹 내 첫 번째 Shift의 id 사용
        Long groupShiftId = shifts.get(0).getId();
        Map<LocalDate, List<Shift>> groupedByDate = shifts.stream()
                .collect(Collectors.groupingBy(Shift::getShiftDate));

        List<ShiftDateDTO> shiftDateDTOList = groupedByDate.entrySet().stream()
                .map(entry -> mapDateGroupToDTO(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

        return ShiftGroupDTO.builder()
                .shiftId(groupShiftId)
                .shiftName(key.getShiftName())
                .startTime(key.getStartTime().toString())
                .endTime(key.getEndTime().toString())
                .dates(shiftDateDTOList)
                .build();
    }

    private ShiftDateDTO mapDateGroupToDTO(LocalDate date, List<Shift> shiftsOnDate) {
        List<AssignedShiftDTO> assignedShiftDTOList = shiftsOnDate.stream()
                .map(s -> AssignedShiftDTO.builder()
                        .assignedShiftId(s.getId())
                        .userId(s.getUser() != null ? s.getUser().getId() : null)
                        .userName(s.getUser() != null ? s.getUser().getName() : null)
                        .build())
                .collect(Collectors.toList());
        return ShiftDateDTO.builder()
                .date(date.toString())
                .assignedShifts(assignedShiftDTOList)
                .build();
    }
}
