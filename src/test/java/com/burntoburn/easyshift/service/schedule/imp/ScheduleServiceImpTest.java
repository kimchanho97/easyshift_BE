package com.burntoburn.easyshift.service.schedule.imp;

import com.burntoburn.easyshift.dto.schedule.req.ScheduleRequest;
import com.burntoburn.easyshift.entity.schedule.Schedule;
import com.burntoburn.easyshift.entity.schedule.ScheduleStatus;
import com.burntoburn.easyshift.entity.store.Store;
import com.burntoburn.easyshift.entity.templates.ScheduleTemplate;
import com.burntoburn.easyshift.entity.templates.ShiftTemplate;
import com.burntoburn.easyshift.repository.schedule.ScheduleRepository;
import com.burntoburn.easyshift.repository.schedule.ScheduleTemplateRepository;
import com.burntoburn.easyshift.repository.store.StoreRepository;
import com.burntoburn.easyshift.service.schedule.ScheduleService;
import java.time.LocalTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class ScheduleServiceImpTest {

    @Autowired
    private ScheduleService scheduleService; // ✅ 실제 서비스 객체 주입

    @MockitoBean
    private ScheduleTemplateRepository scheduleTemplateRepository;

    @MockitoBean
    private ScheduleRepository scheduleRepository;

    @MockitoBean
    private StoreRepository storeRepository;

    private Store store;
    private ScheduleTemplate scheduleTemplate;
    private Schedule existingSchedule;

    @BeforeEach
    void setUp() {
        // ✅ Store 생성
        store = Store.builder()
                .id(1L)
                .build();

        // ✅ ScheduleTemplate 생성
        scheduleTemplate = ScheduleTemplate.builder()
                .id(1L)
                .scheduleTemplateName("Morning Shift")
                .store(store)
                .build();

        // ✅ 기존 스케줄 생성
        existingSchedule = Schedule.builder()
                .id(1L)
                .scheduleName("March Schedule")
                .scheduleMonth("2024-03")
                .scheduleStatus(ScheduleStatus.PENDING)
                .store(store)
                .build();
    }

    @Test
    @DisplayName("스케줄 생성 테스트 - 빈 Shift 포함 검증")
    void createScheduleWithShifts() {
        // Given
        ScheduleRequest request = ScheduleRequest.builder()
                .scheduleName("March Schedule")
                .scheduleMonth("2024-03")
                .build();

        // ✅ ShiftTemplates를 포함한 ScheduleTemplate 생성
        ShiftTemplate shift1 = ShiftTemplate.builder()
                .shiftTemplateName("Morning Shift")
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(17, 0))
                .build();

        ShiftTemplate shift2 = ShiftTemplate.builder()
                .shiftTemplateName("Evening Shift")
                .startTime(LocalTime.of(14, 0))
                .endTime(LocalTime.of(22, 0))
                .build();

        scheduleTemplate.getShiftTemplates().addAll(List.of(shift1, shift2)); // ✅ ShiftTemplates에 추가

        when(storeRepository.findById(1L)).thenReturn(Optional.of(store));
        when(scheduleTemplateRepository.findById(1L)).thenReturn(Optional.of(scheduleTemplate));
        when(scheduleRepository.saveAndFlush(any(Schedule.class))).thenReturn(existingSchedule);

        // When
        Schedule createdSchedule = scheduleService.createSchedule(1L, 1L, request);

        // Then
        assertNotNull(createdSchedule, "생성된 Schedule이 null이면 안 됩니다.");
        assertEquals("March Schedule", createdSchedule.getScheduleName());
        assertEquals("2024-03", createdSchedule.getScheduleMonth());

        // ✅ 빈 Shift 생성 여부 검증
        assertNotNull(createdSchedule.getShifts(), "Shifts 객체가 null이면 안 됩니다.");
        assertFalse(createdSchedule.getShifts().getList().isEmpty(), "Shift 리스트가 비어있으면 안 됩니다.");
        assertEquals(scheduleTemplate.getShiftTemplates().getList().size(), createdSchedule.getShifts().getList().size(), "Shift 개수가 일치해야 합니다.");

        // ✅ 저장 메서드가 1번 호출되었는지 검증
        verify(scheduleRepository, times(1)).save(any(Schedule.class));
    }


    @Test
    @DisplayName("스케줄 생성 테스트 - 저장 검증")
    void createSchedule() {
        // Given
        ScheduleRequest request = ScheduleRequest.builder()
                .scheduleName("March Schedule")
                .scheduleMonth("2024-03")
                .build();

        when(storeRepository.findById(1L)).thenReturn(Optional.of(store));
        when(scheduleTemplateRepository.findById(1L)).thenReturn(Optional.of(scheduleTemplate));
        when(scheduleRepository.save(any(Schedule.class))).thenReturn(existingSchedule);

        // When
        Schedule createdSchedule = scheduleService.createSchedule(1L, 1L, request);

        // Then
        assertNotNull(createdSchedule);
        assertEquals("March Schedule", createdSchedule.getScheduleName());
        assertEquals("2024-03", createdSchedule.getScheduleMonth());

        // ✅ 저장 메서드가 호출되었는지 검증
        verify(scheduleRepository, times(1)).save(any(Schedule.class));
    }

    @Test
    @DisplayName("스케줄 삭제 테스트")
    void deleteSchedule() {
        // Given
        when(scheduleRepository.findById(1L)).thenReturn(Optional.of(existingSchedule));
        doNothing().when(scheduleRepository).delete(existingSchedule);

        // When
        scheduleService.deleteSchedule(1L);

        // Then
        verify(scheduleRepository, times(1)).delete(existingSchedule);
    }

    @Test
    @DisplayName("스케줄 수정 테스트")
    void updateSchedule() {
        // Given
        ScheduleRequest request = ScheduleRequest.builder()
                .scheduleName("Updated March Schedule")
                .scheduleMonth("2024-03")
                .build();

        Schedule updatedSchedule = Schedule.builder()
                .id(1L)
                .scheduleName("Updated March Schedule")
                .scheduleMonth("2024-03")
                .scheduleStatus(ScheduleStatus.PENDING)
                .store(store)
                .build();

        when(scheduleRepository.findById(1L)).thenReturn(Optional.of(existingSchedule));
        when(scheduleRepository.save(any(Schedule.class))).thenReturn(updatedSchedule);

        // When
        Schedule result = scheduleService.updateSchedule(1L, request);

        // Then
        assertNotNull(result);
        assertEquals("Updated March Schedule", result.getScheduleName());

        verify(scheduleRepository, times(1)).save(any(Schedule.class));
    }

    @Test
    @DisplayName("스케줄 조회 테스트")
    void getSchedulesByStore() {
        // Given
        when(scheduleRepository.findByStoreId(1L)).thenReturn(List.of(existingSchedule));

        // When
        List<Schedule> result = scheduleService.getSchedulesByStore(1L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("March Schedule", result.get(0).getScheduleName());

        verify(scheduleRepository, times(1)).findByStoreId(1L);
    }

    @Test
    @DisplayName("존재하지 않는 스케줄 삭제 시 예외 발생")
    void deleteSchedule_NotFound() {
        // Given
        when(scheduleRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NoSuchElementException.class, () -> scheduleService.deleteSchedule(1L));
    }

    @Test
    @DisplayName("존재하지 않는 스케줄 수정 시 예외 발생")
    void updateSchedule_NotFound() {
        // Given
        ScheduleRequest request = ScheduleRequest.builder()
                .scheduleName("Updated Schedule")
                .scheduleMonth("2024-03")
                .build();

        when(scheduleRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NoSuchElementException.class, () -> scheduleService.updateSchedule(1L, request));
    }
}
