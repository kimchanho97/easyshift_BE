package com.burntoburn.easyshift.service.schedule.imp;

import com.burntoburn.easyshift.dto.schedule.req.scheduleCreate.ScheduleRequest;
import com.burntoburn.easyshift.entity.schedule.Schedule;
import com.burntoburn.easyshift.entity.store.Store;
import com.burntoburn.easyshift.entity.templates.ScheduleTemplate;
import com.burntoburn.easyshift.repository.schedule.ScheduleRepository;
import com.burntoburn.easyshift.repository.schedule.ScheduleTemplateRepository;
import com.burntoburn.easyshift.repository.store.StoreRepository;
import com.burntoburn.easyshift.service.schedule.ScheduleFactory;
import com.burntoburn.easyshift.service.schedule.ScheduleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class  ScheduleTemplateServiceImplTest{

    @Autowired
    private ScheduleService scheduleService;

    @MockitoBean
    private ScheduleFactory scheduleFactory;

    @MockitoBean
    private ScheduleTemplateRepository scheduleTemplateRepository;

    @MockitoBean
    private ScheduleRepository scheduleRepository;

    @MockitoBean
    private StoreRepository storeRepository;

    private Store store;
    private ScheduleTemplate scheduleTemplate;
    private Schedule schedule;
    private ScheduleRequest scheduleRequest;

    @BeforeEach
    void setUp() {
        // 테스트용 Store 생성
        store = Store.builder().id(1L).build();

        // 테스트용 ScheduleTemplate 생성 (스케줄 템플릿은 보통 생성 시에 Store와 연관됨)
        scheduleTemplate = ScheduleTemplate.builder()
                .id(1L)
                .scheduleTemplateName("Old Schedule")
                .store(store)
                .build();

        // 테스트용 Schedule 생성
        schedule = Schedule.builder()
                .id(1L)
                .build();

        // ScheduleRequest 생성 (스케줄 생성/수정 요청 DTO)
        scheduleRequest = ScheduleRequest.builder()
                .scheduleTemplateId(1L)
                // 추가 필드들(예: scheduleName, shiftDetails 등)을 필요에 맞게 설정
                .build();
    }

    @Test
    @DisplayName("createSchedule: 스케줄 생성 테스트")
    void createScheduleTest() {
        // Given
        when(storeRepository.findById(1L)).thenReturn(Optional.of(store));
        when(scheduleTemplateRepository.findById(scheduleRequest.getScheduleTemplateId()))
                .thenReturn(Optional.of(scheduleTemplate));
        when(scheduleFactory.createSchedule(store, scheduleTemplate, scheduleRequest))
                .thenReturn(schedule);
        // scheduleRepository.save(schedule)가 호출되면 schedule을 반환하도록 설정
        when(scheduleRepository.save(any(Schedule.class))).thenReturn(schedule);

        // When
        Schedule createdSchedule = scheduleService.createSchedule(1L, scheduleRequest);

        // Then
        assertNotNull(createdSchedule);
        verify(storeRepository, times(1)).findById(1L);
        verify(scheduleTemplateRepository, times(1)).findById(scheduleRequest.getScheduleTemplateId());
        verify(scheduleRepository, times(1)).save(any(Schedule.class));
    }

    @Test
    @DisplayName("updateSchedule: 스케줄 수정 테스트")
    void updateScheduleTest() {
        // Given
        when(scheduleRepository.findByIdAndStoreId(1L, 1L))
                .thenReturn(Optional.of(schedule));
        when(scheduleFactory.updateSchedule(schedule, scheduleRequest))
                .thenReturn(schedule);
        // When: updateSchedule 메서드는 saveAndFlush를 호출하지 않으므로, 별도의 stubbing은 필요 없음
        Schedule updatedSchedule = scheduleService.updateSchedule(1L, 1L, scheduleRequest);
        // Then
        assertNotNull(updatedSchedule);
        verify(scheduleRepository, times(1)).findByIdAndStoreId(1L, 1L);
        // saveAndFlush 호출 검증은 제거합니다.
    }

    @Test
    @DisplayName("deleteSchedule: 스케줄 삭제 테스트")
    void deleteScheduleTest() {
        // Given
        when(scheduleRepository.findById(1L))
                .thenReturn(Optional.of(schedule));
        doNothing().when(scheduleRepository).delete(schedule);

        // When
        scheduleService.deleteSchedule(1L);

        // Then
        verify(scheduleRepository, times(1)).findById(1L);
        verify(scheduleRepository, times(1)).delete(schedule);
    }

    @Test
    @DisplayName("getScheduleWithShifts: 스케줄과 쉬프트 조회 테스트")
    void getScheduleWithShiftsTest() {
        // Given
        when(scheduleRepository.findByIdWithShifts(1L))
                .thenReturn(Optional.of(schedule));

        // When
        Schedule result = scheduleService.getScheduleWithShifts(1L);

        // Then
        assertNotNull(result);
        verify(scheduleRepository, times(1)).findByIdWithShifts(1L);
    }

    @Test
    @DisplayName("getSchedulesByStore: 매장의 모든 스케줄 조회 테스트")
    void getSchedulesByStoreTest() {
        // Given
        List<Schedule> schedules = List.of(schedule);
        when(scheduleRepository.findByStoreId(1L)).thenReturn(schedules);

        // When
        List<Schedule> result = scheduleService.getSchedulesByStore(1L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(scheduleRepository, times(1)).findByStoreId(1L);
    }
}
