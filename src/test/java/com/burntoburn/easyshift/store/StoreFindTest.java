package com.burntoburn.easyshift.store;

import com.burntoburn.easyshift.dto.schedule.res.ScheduleDetailDTO;
import com.burntoburn.easyshift.dto.store.res.StoreScheduleResponseDTO;
import com.burntoburn.easyshift.entity.schedule.Schedule;
import com.burntoburn.easyshift.entity.schedule.Shift;
import com.burntoburn.easyshift.entity.schedule.collection.Shifts;
import com.burntoburn.easyshift.entity.store.Store;
import com.burntoburn.easyshift.entity.user.Role;
import com.burntoburn.easyshift.entity.user.User;
import com.burntoburn.easyshift.repository.schedule.ScheduleRepository;
import com.burntoburn.easyshift.repository.store.StoreRepository;
import com.burntoburn.easyshift.repository.store.UserStoreRepository;
import com.burntoburn.easyshift.repository.user.UserRepository;
import com.burntoburn.easyshift.config.jwt.TokenProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.burntoburn.easyshift.service.StoreService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StoreFindTest {

    @Mock
    private StoreRepository storeRepository;
    @Mock
    private ScheduleRepository scheduleRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserStoreRepository userStoreRepository;
    @Mock
    private TokenProvider tokenProvider;

    @InjectMocks
    private StoreService storeService;

    /**
     * 성공 케이스: store는 존재하고 scheduleId가 제공되지 않으면
     * store의 첫 번째 schedule을 선택하여 DTO를 반환해야 합니다.
     */
    @Test
    void testGetStoreSchedule_success_default() {
        // 더미 User
        User user = User.builder()
                .id(1L)
                .email("user@example.com")
                .name("John Doe")
                .role(Role.WORKER)
                .build();

        // 더미 Shift: 동일한 그룹("2교대")의 서로 다른 날짜에 대한 근무 정보
        Shift shift1 = Shift.builder()
                .id(101L)
                .shiftName("2교대")
                .startTime(LocalTime.of(15, 0))
                .endTime(LocalTime.of(18, 0))
                .shiftDate(LocalDate.of(2024, 2, 10))
                .user(user)
                .build();

        Shift shift2 = Shift.builder()
                .id(102L)
                .shiftName("2교대")
                .startTime(LocalTime.of(15, 0))
                .endTime(LocalTime.of(18, 0))
                .shiftDate(LocalDate.of(2024, 2, 11))
                .user(user)
                .build();

        // 더미 Schedule
        Schedule schedule = Schedule.builder()
                .id(201L)
                .scheduleName("주간 근무")
                .shifts((Shifts) Arrays.asList(shift1, shift2))
                .build();

        // 더미 Store: schedules 필드에 schedule 추가
        Store store = Store.builder()
                .id(301L)
                .storeName("매장 A")
                .schedules(Arrays.asList(schedule))
                .build();

        when(storeRepository.findById(301L)).thenReturn(Optional.of(store));

        // scheduleId 미제공 → Optional.empty()
        Optional<Long> scheduleIdOptional = Optional.empty();

        StoreScheduleResponseDTO responseDTO = storeService.getStoreSchedule(301L, scheduleIdOptional);

        // 검증
        assertNotNull(responseDTO);
        assertEquals(301L, responseDTO.getStoreId().longValue());
        assertEquals("매장 A", responseDTO.getStoreName());
        assertEquals(1, responseDTO.getSchedules().size());

        ScheduleDetailDTO detailDTO = responseDTO.getSelectedSchedule();
        assertNotNull(detailDTO);
        assertEquals(201L, detailDTO.getScheduleId().longValue());
        assertEquals("주간 근무", detailDTO.getScheduleName());
        // shifts 그룹이 잘 구성되었는지 추가 검증 (예: 그룹 내 날짜 수)
        assertFalse(detailDTO.getShifts().isEmpty());
    }

    /**
     * 성공 케이스: store는 존재하고, 유효한 scheduleId가 제공되는 경우.
     * scheduleRepository.findById를 통해 해당 스케줄을 직접 가져와서 DTO에 매핑해야 합니다.
     */
    @Test
    void testGetStoreSchedule_success_withScheduleId() {
        // 더미 User
        User user = User.builder()
                .id(2L)
                .email("jane@example.com")
                .name("Jane Doe")
                .role(Role.WORKER)
                .build();

        // 더미 Shift: "3교대" 그룹의 근무 정보
        Shift shift1 = Shift.builder()
                .id(103L)
                .shiftName("3교대")
                .startTime(LocalTime.of(18, 0))
                .endTime(LocalTime.of(21, 0))
                .shiftDate(LocalDate.of(2024, 2, 10))
                .user(user)
                .build();

        Shift shift2 = Shift.builder()
                .id(104L)
                .shiftName("3교대")
                .startTime(LocalTime.of(18, 0))
                .endTime(LocalTime.of(21, 0))
                .shiftDate(LocalDate.of(2024, 2, 11))
                .user(user)
                .build();

        // 더미 Schedule (야간 근무)
        Schedule schedule = Schedule.builder()
                .id(202L)
                .scheduleName("야간 근무")
                .shifts((Shifts) Arrays.asList(shift1, shift2))
                .build();

        // 다른 Schedule도 포함한 Store
        Schedule otherSchedule = Schedule.builder()
                .id(203L)
                .scheduleName("추가 근무")
                .shifts((Shifts) Collections.emptyList())
                .build();

        Store store = Store.builder()
                .id(302L)
                .storeName("매장 B")
                .schedules(Arrays.asList(otherSchedule, schedule))
                .build();

        when(storeRepository.findById(302L)).thenReturn(Optional.of(store));
        when(scheduleRepository.findById(202L)).thenReturn(Optional.of(schedule));

        Optional<Long> scheduleIdOptional = Optional.of(202L);

        StoreScheduleResponseDTO responseDTO = storeService.getStoreSchedule(302L, scheduleIdOptional);

        // 검증
        assertNotNull(responseDTO);
        assertEquals(302L, responseDTO.getStoreId().longValue());
        assertEquals("매장 B", responseDTO.getStoreName());
        assertEquals(2, responseDTO.getSchedules().size());

        ScheduleDetailDTO detailDTO = responseDTO.getSelectedSchedule();
        assertNotNull(detailDTO);
        assertEquals(202L, detailDTO.getScheduleId().longValue());
        assertEquals("야간 근무", detailDTO.getScheduleName());
        assertFalse(detailDTO.getShifts().isEmpty());
    }

    /**
     * 실패 케이스: storeId에 해당하는 매장이 존재하지 않는 경우.
     */
    @Test
    void testGetStoreSchedule_failure_storeNotFound() {
        when(storeRepository.findById(999L)).thenReturn(Optional.empty());
        Optional<Long> scheduleIdOptional = Optional.empty();

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            storeService.getStoreSchedule(999L, scheduleIdOptional);
        });
        assertTrue(exception.getMessage().contains("매장을 찾을 수 없습니다."));
    }

    /**
     * 실패 케이스: scheduleId가 제공되었으나 해당 스케줄이 존재하지 않는 경우.
     */
    @Test
    void testGetStoreSchedule_failure_scheduleNotFound() {
        // 더미 Store (스케줄 목록에 단 하나의 스케줄만 존재)
        User user = User.builder()
                .id(3L)
                .email("bob@example.com")
                .name("Bob")
                .role(Role.WORKER)
                .build();

        Shift shift = Shift.builder()
                .id(105L)
                .shiftName("2교대")
                .startTime(LocalTime.of(15, 0))
                .endTime(LocalTime.of(18, 0))
                .shiftDate(LocalDate.of(2024, 2, 12))
                .user(user)
                .build();

        Schedule schedule = Schedule.builder()
                .id(204L)
                .scheduleName("주간 근무")
                .shifts((Shifts) Arrays.asList(shift))
                .build();

        Store store = Store.builder()
                .id(303L)
                .storeName("매장 C")
                .schedules(Arrays.asList(schedule))
                .build();

        when(storeRepository.findById(303L)).thenReturn(Optional.of(store));
        when(scheduleRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Long> scheduleIdOptional = Optional.of(999L);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            storeService.getStoreSchedule(303L, scheduleIdOptional);
        });
        assertTrue(exception.getMessage().contains("스케줄을 찾을 수 없습니다."));
    }
}
