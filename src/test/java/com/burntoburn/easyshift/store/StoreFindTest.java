package com.burntoburn.easyshift.store;

import com.burntoburn.easyshift.dto.schedule.res.ScheduleDetailDTO;
import com.burntoburn.easyshift.dto.shift.res.AssignedShiftDTO;
import com.burntoburn.easyshift.dto.shift.res.ShiftDateDTO;
import com.burntoburn.easyshift.dto.shift.res.ShiftGroupDTO;
import com.burntoburn.easyshift.dto.store.res.StoreScheduleResponseDTO;
import com.burntoburn.easyshift.entity.schedule.Schedule;
import com.burntoburn.easyshift.entity.schedule.ScheduleStatus;
import com.burntoburn.easyshift.entity.schedule.Shift;
import com.burntoburn.easyshift.entity.schedule.collection.Shifts;
import com.burntoburn.easyshift.entity.store.Store;
import com.burntoburn.easyshift.repository.schedule.ScheduleRepository;
import com.burntoburn.easyshift.repository.store.StoreRepository;
import com.burntoburn.easyshift.service.StoreService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StoreFindTest {

    @InjectMocks
    private StoreService storeService;

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private ScheduleRepository scheduleRepository;

    /**
     * 더미 Store 생성.
     * Store에는 두 개의 Schedule이 있으며, 각 Schedule은 Shifts 일급 컬렉션에 1개의 Shift를 포함합니다.
     */
    private Store createDummyStore() {
        // Store 생성
        Store store = Store.builder()
                .storeName("Test Store")
                .build();
        ReflectionTestUtils.setField(store, "id", 1L);

        // Schedule1 생성: "Schedule1", 2024-02, 상태 PENDING
        Schedule schedule1 = Schedule.builder()
                .scheduleName("Schedule1")
                .scheduleMonth(YearMonth.of(2024, 2))
                .scheduleStatus(ScheduleStatus.PENDING)
                .build();
        ReflectionTestUtils.setField(schedule1, "id", 101L);
        Shifts shifts1 = new Shifts();
        // Shift: "Morning Shift", 날짜 2024-02-10, 09:00~17:00
        Shift shift1 = Shift.builder()
                .shiftName("Morning Shift")
                .shiftDate(LocalDate.of(2024, 2, 10))
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(17, 0))
                .build();
        ReflectionTestUtils.setField(shift1, "id", 1001L);
        // 내부 필드 이름이 "shiftList"인 점을 반영하여 값 주입
        ReflectionTestUtils.setField(shifts1, "shiftList", List.of(shift1));
        ReflectionTestUtils.setField(schedule1, "shifts", shifts1);

        // Schedule2 생성: "Schedule2", 2024-02, 상태 PENDING
        Schedule schedule2 = Schedule.builder()
                .scheduleName("Schedule2")
                .scheduleMonth(YearMonth.of(2024, 2))
                .scheduleStatus(ScheduleStatus.PENDING)
                .build();
        ReflectionTestUtils.setField(schedule2, "id", 102L);
        Shifts shifts2 = new Shifts();
        // Shift: "Evening Shift", 날짜 2024-02-10, 18:00~22:00
        Shift shift2 = Shift.builder()
                .shiftName("Evening Shift")
                .shiftDate(LocalDate.of(2024, 2, 10))
                .startTime(LocalTime.of(18, 0))
                .endTime(LocalTime.of(22, 0))
                .build();
        ReflectionTestUtils.setField(shift2, "id", 1002L);
        ReflectionTestUtils.setField(shifts2, "shiftList", List.of(shift2));
        ReflectionTestUtils.setField(schedule2, "shifts", shifts2);

        // Store에 두 스케줄 주입
        ReflectionTestUtils.setField(store, "schedules", List.of(schedule1, schedule2));
        return store;
    }

    // --- 성공 케이스 ---

    @Test
    public void testGetStoreSchedule_WithScheduleId_Success() {
        // arrange: storeId=1, scheduleId=102 (Schedule2)
        Store dummyStore = createDummyStore();
        when(storeRepository.findById(1L)).thenReturn(Optional.of(dummyStore));

        // act: scheduleId 제공 시 해당 스케줄 반환
        StoreScheduleResponseDTO response = storeService.getStoreSchedule(1L, Optional.of(102L));

        // assert
        assertNotNull(response);
        assertEquals(1L, response.getStoreId());
        // 스케줄 목록은 2개여야 함
        assertEquals(2, response.getSchedules().size());

        ScheduleDetailDTO selectedSchedule = response.getSelectedSchedule();
        assertNotNull(selectedSchedule);
        assertEquals(102L, selectedSchedule.getScheduleId());
        assertEquals("Schedule2", selectedSchedule.getScheduleName());

        // Schedule2의 Shifts: 그룹화 로직에 따라 ShiftGroupDTO 하나로 묶임
        List<ShiftGroupDTO> shiftGroups = selectedSchedule.getShifts();
        assertNotNull(shiftGroups);
        assertEquals(1, shiftGroups.size());
        ShiftGroupDTO group = shiftGroups.get(0);
        assertEquals("Evening Shift", group.getShiftName());
        // 그룹 내 날짜별 그룹화: 2024-02-10
        List<ShiftDateDTO> shiftDates = group.getDates();
        assertNotNull(shiftDates);
        assertEquals(1, shiftDates.size());
        ShiftDateDTO dateDTO = shiftDates.get(0);
        assertEquals("2024-02-10", dateDTO.getDate());
        // AssignedShiftDTO 확인 (shift id, user 정보는 null)
        List<AssignedShiftDTO> assignedShifts = dateDTO.getAssignedShifts();
        assertNotNull(assignedShifts);
        assertEquals(1, assignedShifts.size());
        AssignedShiftDTO assigned = assignedShifts.get(0);
        assertEquals(1002L, assigned.getAssignedShiftId());
    }

    @Test
    public void testGetStoreSchedule_NoScheduleId_Success() {
        // arrange: scheduleId 미제공 시 첫 번째 스케줄(Schedule1) 반환
        Store dummyStore = createDummyStore();
        when(storeRepository.findById(1L)).thenReturn(Optional.of(dummyStore));

        // act
        StoreScheduleResponseDTO response = storeService.getStoreSchedule(1L, Optional.empty());

        // assert
        assertNotNull(response);
        ScheduleDetailDTO selectedSchedule = response.getSelectedSchedule();
        assertNotNull(selectedSchedule);
        assertEquals(101L, selectedSchedule.getScheduleId());
        assertEquals("Schedule1", selectedSchedule.getScheduleName());
        // Schedule1의 Shift 정보 확인: "Morning Shift", 2024-02-10
        List<ShiftGroupDTO> shiftGroups = selectedSchedule.getShifts();
        assertNotNull(shiftGroups);
        assertEquals(1, shiftGroups.size());
        ShiftGroupDTO group = shiftGroups.get(0);
        assertEquals("Morning Shift", group.getShiftName());
        List<ShiftDateDTO> shiftDates = group.getDates();
        assertNotNull(shiftDates);
        assertEquals(1, shiftDates.size());
        ShiftDateDTO dateDTO = shiftDates.get(0);
        assertEquals("2024-02-10", dateDTO.getDate());
        List<AssignedShiftDTO> assignedShifts = dateDTO.getAssignedShifts();
        assertNotNull(assignedShifts);
        assertEquals(1, assignedShifts.size());
        AssignedShiftDTO assigned = assignedShifts.get(0);
        assertEquals(1001L, assigned.getAssignedShiftId());
    }

    // --- 실패 케이스 ---

    @Test
    public void testGetStoreSchedule_StoreNotFound_Failure() {
        // arrange: 존재하지 않는 storeId 사용
        when(storeRepository.findById(999L)).thenReturn(Optional.empty());

        // act & assert
        Exception exception = assertThrows(RuntimeException.class, () ->
                storeService.getStoreSchedule(999L, Optional.empty())
        );
        assertTrue(exception.getMessage().contains("해당 매장을 찾을 수 없습니다"));
    }

    @Test
    public void testGetStoreSchedule_ScheduleNotFound_Failure() {
        // arrange: store는 존재하지만, 요청된 scheduleId가 목록에 없음
        Store dummyStore = createDummyStore();
        when(storeRepository.findById(1L)).thenReturn(Optional.of(dummyStore));

        // act & assert
        Exception exception = assertThrows(RuntimeException.class, () ->
                storeService.getStoreSchedule(1L, Optional.of(999L))
        );
        assertTrue(exception.getMessage().contains("선택한 스케줄을 찾을 수 없습니다"));
    }
}