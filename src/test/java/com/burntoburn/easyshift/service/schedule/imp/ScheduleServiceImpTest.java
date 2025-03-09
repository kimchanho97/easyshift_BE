package com.burntoburn.easyshift.service.schedule.imp;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.burntoburn.easyshift.dto.schedule.req.ScheduleUpload;
import com.burntoburn.easyshift.dto.schedule.req.ShiftDetail;
import com.burntoburn.easyshift.dto.schedule.res.ScheduleDetailDTO;
import com.burntoburn.easyshift.dto.schedule.res.ScheduleInfoResponse;
import com.burntoburn.easyshift.dto.schedule.res.WorkerScheduleResponse;
import com.burntoburn.easyshift.entity.schedule.Schedule;
import com.burntoburn.easyshift.entity.schedule.ScheduleStatus;
import com.burntoburn.easyshift.entity.schedule.Shift;
import com.burntoburn.easyshift.entity.store.Store;
import com.burntoburn.easyshift.entity.templates.ScheduleTemplate;

import com.burntoburn.easyshift.entity.templates.ShiftTemplate;
import com.burntoburn.easyshift.repository.schedule.ScheduleRepository;
import com.burntoburn.easyshift.repository.schedule.ScheduleTemplateRepository;
import com.burntoburn.easyshift.repository.schedule.ShiftRepository;
import com.burntoburn.easyshift.repository.store.StoreRepository;
import com.burntoburn.easyshift.repository.store.UserStoreRepository;
import com.burntoburn.easyshift.repository.user.UserRepository;
import com.burntoburn.easyshift.service.schedule.ScheduleFactory;


import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ScheduleServiceImpTest {

    @InjectMocks
    ScheduleServiceImp scheduleService;

    @Mock
    private UserStoreRepository userStoreRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private StoreRepository storeRepository;
    @Mock
    private ScheduleTemplateRepository scheduleTemplateRepository;
    @Mock
    private ShiftRepository shiftRepository;
    @Mock
    private ScheduleRepository scheduleRepository;
    @Mock
    private ScheduleFactory scheduleFactory;


    @Test
    @DisplayName("스케줄 생성 성공")
    void createSchedule_success(){
        // given: 요청 객체 설정
        ScheduleUpload request = new ScheduleUpload();
        ReflectionTestUtils.setField(request, "scheduleName", "Test Schedule");
        ReflectionTestUtils.setField(request, "storeId", 1L);
        ReflectionTestUtils.setField(request, "scheduleMonth", YearMonth.of(2025, 3));
        ReflectionTestUtils.setField(request, "scheduleTemplateId", 2L);
        ReflectionTestUtils.setField(request, "description", "Test Description");

        // ShiftDetail 리스트 생성 (ShiftTemplateId와 expectedWorkers 정의)
        List<ShiftDetail> shiftDetails = List.of(
                new ShiftDetail(10L, 2), // ShiftTemplate 10번에 대해 2개의 Shift 생성 기대
                new ShiftDetail(20L, 3)  // ShiftTemplate 20번에 대해 3개의 Shift 생성 기대
        );
        ReflectionTestUtils.setField(request, "shiftDetails", shiftDetails);

        // Store 및 ScheduleTemplate Mock 설정
        Store store = Store.builder()
                .id(1L)
                .storeName("Test Store")
                .build();

        ScheduleTemplate scheduleTemplate = ScheduleTemplate.builder()
                .id(2L)
                .scheduleTemplateName("Test Template")
                .build();

        // ShiftTemplate Mock (Shift 생성 시 참조됨)
        ShiftTemplate shiftTemplate1 = ShiftTemplate.builder()
                .id(10L)
                .shiftTemplateName("Morning Shift")
                .startTime(LocalTime.parse("08:00"))
                .endTime(LocalTime.parse("12:00"))
                .build();

        ShiftTemplate shiftTemplate2 = ShiftTemplate.builder()
                .id(20L)
                .shiftTemplateName("Evening Shift")
                .startTime(LocalTime.parse("17:00"))
                .endTime(LocalTime.parse("21:00"))
                .build();

        // 빌더 패턴을 사용하여 Shift 리스트 생성
        List<Shift> generatedShifts = new ArrayList<>();
        for (int day = 1; day <= 31; day++) { // 31일짜리 달 (예: 3월) 기준으로 생성
            generatedShifts.add(Shift.builder()
                    .schedule(null)
                    .shiftDate(request.getScheduleMonth().atDay(day))
                    .shiftName(shiftTemplate1.getShiftTemplateName())
                    .shiftTemplateId(shiftTemplate1.getId())
                    .startTime(shiftTemplate1.getStartTime())
                    .endTime(shiftTemplate1.getEndTime())
                    .user(null)
                    .build());

            generatedShifts.add(Shift.builder()
                    .schedule(null)
                    .shiftDate(request.getScheduleMonth().atDay(day))
                    .shiftName(shiftTemplate1.getShiftTemplateName())
                    .shiftTemplateId(shiftTemplate1.getId())
                    .startTime(shiftTemplate1.getStartTime())
                    .endTime(shiftTemplate1.getEndTime())
                    .user(null)
                    .build());

            generatedShifts.add(Shift.builder()
                    .schedule(null)
                    .shiftDate(request.getScheduleMonth().atDay(day))
                    .shiftName(shiftTemplate2.getShiftTemplateName())
                    .shiftTemplateId(shiftTemplate2.getId())
                    .startTime(shiftTemplate2.getStartTime())
                    .endTime(shiftTemplate2.getEndTime())
                    .user(null)
                    .build());

            generatedShifts.add(Shift.builder()
                    .schedule(null)
                    .shiftDate(request.getScheduleMonth().atDay(day))
                    .shiftName(shiftTemplate2.getShiftTemplateName())
                    .shiftTemplateId(shiftTemplate2.getId())
                    .startTime(shiftTemplate2.getStartTime())
                    .endTime(shiftTemplate2.getEndTime())
                    .user(null)
                    .build());

            generatedShifts.add(Shift.builder()
                    .schedule(null)
                    .shiftDate(request.getScheduleMonth().atDay(day))
                    .shiftName(shiftTemplate2.getShiftTemplateName())
                    .shiftTemplateId(shiftTemplate2.getId())
                    .startTime(shiftTemplate2.getStartTime())
                    .endTime(shiftTemplate2.getEndTime())
                    .user(null)
                    .build());
        }

        // Schedule 객체 Mock 설정
        Schedule schedule = Schedule.builder()
                .id(100L)
                .scheduleName(request.getScheduleName())
                .store(store)
                .scheduleMonth(request.getScheduleMonth())
                .description(request.getDescription())
                .shifts(generatedShifts)
                .build();

        // Mock 설정
        when(storeRepository.findById(1L)).thenReturn(Optional.of(store));
        when(scheduleTemplateRepository.findById(2L)).thenReturn(Optional.of(scheduleTemplate));
        when(scheduleFactory.createSchedule(store, scheduleTemplate, request)).thenReturn(schedule);
        when(scheduleRepository.save(any(Schedule.class))).thenReturn(schedule);

        // when: 스케줄 생성 실행
        scheduleService.createSchedule(request);

        // then: 검증
        verify(storeRepository, times(1)).findById(1L);
        verify(scheduleTemplateRepository, times(1)).findById(2L);
        verify(scheduleFactory, times(1)).createSchedule(store, scheduleTemplate, request);
        verify(scheduleRepository, times(1)).save(schedule);

        // Shift가 예상대로 생성되었는지 검증
        assertNotNull(schedule.getShifts());
        assertEquals(31 * (2 + 3), schedule.getShifts().size()); // 31일 x (2+3)개의 Shift 생성 확인

        // 특정 날짜에 Shift가 올바르게 생성되었는지 확인
        Shift sampleShift = schedule.getShifts().get(0);
        assertNotNull(sampleShift.getShiftDate());
        assertNotNull(sampleShift.getShiftTemplateId());
        assertNotNull(sampleShift.getShiftName());
    }

    @Test
    @DisplayName("매장 스케줄 목록 조회 성공")
    void getSchedulesByStore_success(){
        // given
        Long storeId = 1L;
        Pageable pageable = PageRequest.of(0,10);

        Store store = Store.builder()
                .id(storeId)
                .storeCode(UUID.randomUUID())
                .storeName("test")
                .description("asda")
                .build();

        // Schedule 객체 Mock 설정
        Schedule schedule = Schedule.builder()
                .id(100L)
                .scheduleStatus(ScheduleStatus.PENDING)
                .scheduleName("test")
                .store(store)
                .scheduleMonth(YearMonth.now())
                .description("test")
                .build();

        List<Schedule> schedules = List.of(schedule);
        Page<Schedule> schedulePage = new PageImpl<>(schedules, pageable, schedules.size()); // Page 객체 생성

        // Mock 설정
        when(scheduleRepository.findByStoreIdOrderByCreatedAtDesc(1L,pageable)).thenReturn(schedulePage);

        // when
        ScheduleInfoResponse response = scheduleService.getSchedulesByStore(storeId, pageable);

        //then
        verify(scheduleRepository, times(1)).findByStoreIdOrderByCreatedAtDesc(storeId, pageable);

        assertThat(response).isNotNull();
        assertThat(response.getSchedules()).hasSize(1);
        assertThat(response.getPageInfoDTO().isLast()).isTrue();
    }



    @Test
    @DisplayName("스케줄 삭제 성공")
    void deleteSchedule_success(){
        // given
        Long scheduleId = 100L;
        Schedule schedule = Schedule.builder()
                .id(scheduleId)
                .scheduleName("Test Schedule")
                .build();

        when(scheduleRepository.findById(scheduleId)).thenReturn(Optional.of(schedule));

        // when
        scheduleService.deleteSchedule(scheduleId);

        // then
        verify(scheduleRepository, times(1)).findById(scheduleId);
        verify(scheduleRepository, times(1)).delete(schedule);
    }


    @Test
    @DisplayName("worker 의 스케줄 조회 성공")
    void getSchedulesByWorker_success(){
        // given
        Long storeId = 1L;
        Long userId = 10L;
        String date = "2025-03";
        YearMonth scheduleMonth = YearMonth.parse(date, DateTimeFormatter.ofPattern("yyyy-MM"));

        Store store = Store.builder().id(storeId).storeName("Test Store").build();
        Schedule schedule = Schedule.builder().id(100L).store(store).scheduleMonth(scheduleMonth).build();
        List<Schedule> schedules = List.of(schedule);

        when(scheduleRepository.findWorkerSchedules(storeId, scheduleMonth, userId)).thenReturn(schedules);

        // when
        WorkerScheduleResponse response = scheduleService.getSchedulesByWorker(storeId, userId, date);

        // then
        verify(scheduleRepository, times(1)).findWorkerSchedules(storeId, scheduleMonth, userId);
        assertThat(response).isNotNull();
        assertThat(response.getSchedules()).hasSize(1);
    }


    @Test
    @DisplayName("스케줄 조회(all) 성공")
    void getAllSchedules_success() {
        // given
        Long scheduleId = 100L;
        Store store = Store.builder()
                .id(1L)
                .storeName("Test Store")
                .build();

        Schedule schedule = Schedule.builder()
                .id(scheduleId)
                .scheduleName("Test Schedule")
                .store(store)
                .scheduleMonth(YearMonth.of(2025, 3))
                .description("Test Description")
                .build();

        ScheduleTemplate scheduleTemplate = ScheduleTemplate.builder()
                .id(200L)
                .scheduleTemplateName("Test Template")
                .build();

        List<ShiftTemplate> shiftTemplates = List.of(
                ShiftTemplate.builder()
                        .id(10L)
                        .shiftTemplateName("Morning Shift")
                        .startTime(LocalTime.parse("08:00"))
                        .endTime(LocalTime.parse("12:00"))
                        .build()
        );

        List<Shift> shifts = List.of(
                Shift.builder()
                        .id(300L)
                        .schedule(schedule)
                        .shiftDate(schedule.getScheduleMonth().atDay(1))
                        .shiftName("Morning Shift")
                        .shiftTemplateId(10L)
                        .startTime(LocalTime.parse("08:00"))
                        .endTime(LocalTime.parse("12:00"))
                        .user(null)
                        .build()
        );

        when(scheduleRepository.findScheduleWithShifts(scheduleId)).thenReturn(Optional.of(schedule));
        when(scheduleTemplateRepository.findScheduleTemplateWithShiftsById(schedule.getScheduleTemplateId())).thenReturn(Optional.of(scheduleTemplate));

        ReflectionTestUtils.setField(scheduleTemplate, "shiftTemplates", shiftTemplates);
        ReflectionTestUtils.setField(schedule, "shifts", shifts);

        // when
        ScheduleDetailDTO response = scheduleService.getAllSchedules(scheduleId);

        // then
        verify(scheduleRepository, times(1)).findScheduleWithShifts(scheduleId);
        verify(scheduleTemplateRepository, times(1)).findScheduleTemplateWithShiftsById(schedule.getScheduleTemplateId());

        assertThat(response).isNotNull();
        assertThat(response.getShifts()).hasSize(1);
    }

}
