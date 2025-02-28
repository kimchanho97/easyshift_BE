package com.burntoburn.easyshift.service.store;

import com.burntoburn.easyshift.dto.store.res.ScheduleTemplateDto;
import com.burntoburn.easyshift.dto.store.res.SelectedScheduleTemplateDto;
import com.burntoburn.easyshift.dto.store.res.ShiftTemplateDto;
import com.burntoburn.easyshift.dto.store.res.StoreInfoResponse;
import com.burntoburn.easyshift.entity.schedule.Shift;
import com.burntoburn.easyshift.entity.templates.ScheduleTemplate;
import com.burntoburn.easyshift.entity.templates.ShiftTemplate;
import com.burntoburn.easyshift.exception.store.StoreException;
import com.burntoburn.easyshift.repository.schedule.ScheduleTemplateRepository;
import com.burntoburn.easyshift.repository.schedule.ShiftRepository;
import com.burntoburn.easyshift.repository.store.UserStoreRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StoreServiceImplTest {

    @InjectMocks
    private StoreServiceImpl storeService;
    @Mock
    private UserStoreRepository userStoreRepository;
    @Mock
    private ScheduleTemplateRepository scheduleTemplateRepository;
    @Mock
    private ShiftRepository shiftRepository;

    @Test
    @DisplayName("스케줄 템플릿이 없는 경우 빈 응답 반환")
    void getStoreInfo_NoScheduleTemplate() {
        // Given
        Long userId = 1L;
        Long storeId = 1L;

        when(userStoreRepository.existsByUserIdAndStoreId(userId, storeId)).thenReturn(true);
        when(scheduleTemplateRepository.findAllWithShiftTemplatesByStoreId(storeId)).thenReturn(Collections.emptyList());

        // When
        StoreInfoResponse response = storeService.getStoreInfo(storeId, userId);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStoreId()).isEqualTo(storeId);
        assertThat(response.getScheduleTemplates()).isEmpty();
        assertThat(response.getSelectedScheduleTemplate()).isNull();
    }

    @Test
    @DisplayName("매장 접근 권한 없음 예외 발생")
    void getStoreInfo_AccessDenied() {
        // Given
        Long userId = 1L;
        Long storeId = 1L;

        when(userStoreRepository.existsByUserIdAndStoreId(userId, storeId)).thenReturn(false);

        assertThatThrownBy(() -> storeService.getStoreInfo(storeId, userId))
                .isInstanceOf(StoreException.class);
    }

    @Test
    @DisplayName("다양한 스케줄 데이터가 있을 때 응답이 올바르게 변환되는지 확인")
    void getStoreInfo_MultipleSchedules() {
        // Given
        Long userId = 1L;
        Long storeId = 1L;

        ScheduleTemplate st1 = new ScheduleTemplate(101L, "ScheduleTemplateName1", null);
        ScheduleTemplate st2 = new ScheduleTemplate(102L, "ScheduleTemplateName2", null);
        ScheduleTemplate st3 = new ScheduleTemplate(103L, "ScheduleTemplateName3", null);
        List<ScheduleTemplate> scheduleTemplates = List.of(st1, st2, st3);

        // ShiftTemplate
        st1.getShiftTemplates().add(new ShiftTemplate(201L, "오전 근무", LocalTime.of(12, 0), LocalTime.of(15, 0), st1));
        st1.getShiftTemplates().add(new ShiftTemplate(202L, "오후 근무", LocalTime.of(15, 0), LocalTime.of(18, 0), st1));
        st1.getShiftTemplates().add(new ShiftTemplate(203L, "야간 근무", LocalTime.of(18, 0), LocalTime.of(21, 0), st1));

        Shift shift1 = new Shift(201L, "오전 근무", LocalDate.of(2024, 3, 1), null, null, null, null, 201L);
        Shift shift2 = new Shift(202L, "오후 근무", LocalDate.of(2024, 3, 2), null, null, null, null, 202L);
        Shift shift3 = new Shift(203L, "야간 근무", LocalDate.of(2024, 3, 2), null, null, null, null, 203L);
        List<Shift> shifts = List.of(shift1, shift2, shift3);

        when(userStoreRepository.existsByUserIdAndStoreId(userId, storeId)).thenReturn(true);
        when(scheduleTemplateRepository.findAllWithShiftTemplatesByStoreId(storeId)).thenReturn(scheduleTemplates);
        when(shiftRepository.findAllByScheduleTemplateIdAndDateBetween(eq(storeId), any(), any(), any())).thenReturn(shifts);

        // When
        StoreInfoResponse response = storeService.getStoreInfo(storeId, userId);

        // Then
        assertThat(response)
                .isNotNull()
                .extracting(StoreInfoResponse::getStoreId)
                .isEqualTo(storeId);

        assertThat(response.getScheduleTemplates())
                .hasSize(3)
                .extracting(ScheduleTemplateDto::getScheduleTemplateId, ScheduleTemplateDto::getScheduleTemplateName)
                .containsExactlyInAnyOrder(
                        tuple(101L, "ScheduleTemplateName1"),
                        tuple(102L, "ScheduleTemplateName2"),
                        tuple(103L, "ScheduleTemplateName3")
                );

        assertThat(response.getSelectedScheduleTemplate())
                .isNotNull()
                .extracting(SelectedScheduleTemplateDto::getScheduleTemplateId, SelectedScheduleTemplateDto::getScheduleTemplateName)
                .containsExactly(101L, "ScheduleTemplateName1");

        assertThat(response.getSelectedScheduleTemplate().getShifts())
                .hasSize(3)
                .extracting(ShiftTemplateDto::getShiftTemplateId, ShiftTemplateDto::getShiftTemplateName, ShiftTemplateDto::getStartTime, ShiftTemplateDto::getEndTime)
                .containsExactlyInAnyOrder(
                        tuple(201L, "오전 근무", "12:00", "15:00"),
                        tuple(202L, "오후 근무", "15:00", "18:00"),
                        tuple(203L, "야간 근무", "18:00", "21:00")
                );
    }

}