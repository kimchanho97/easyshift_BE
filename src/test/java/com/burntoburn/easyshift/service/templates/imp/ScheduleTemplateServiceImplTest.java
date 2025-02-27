package com.burntoburn.easyshift.service.templates.imp;

import com.burntoburn.easyshift.dto.template.req.ScheduleTemplateRequest;
import com.burntoburn.easyshift.dto.template.req.ShiftTemplateRequest;
import com.burntoburn.easyshift.dto.template.res.AllScheduleTemplateResponse;
import com.burntoburn.easyshift.entity.templates.ScheduleTemplate;
import com.burntoburn.easyshift.entity.templates.ShiftTemplate;
import com.burntoburn.easyshift.entity.store.Store;
import com.burntoburn.easyshift.repository.schedule.ScheduleTemplateRepository;
import com.burntoburn.easyshift.repository.store.StoreRepository;
import com.burntoburn.easyshift.service.templates.ScheduleTemplateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class ScheduleTemplateServiceImplTest {

    @Autowired
    private ScheduleTemplateService scheduleTemplateService;

    @MockitoBean
    private ScheduleTemplateRepository scheduleTemplateRepository;

    @MockitoBean
    private StoreRepository storeRepository;

    private Store store;
    private ScheduleTemplate existingTemplate;

    @BeforeEach
    void setUp() {
        // 매장(Store) 객체 생성
        store = Store.builder().id(1L).build();

        // 기존 ShiftTemplate 2개 포함된 ScheduleTemplate 생성
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

        existingTemplate = ScheduleTemplate.builder()
                .id(1L)
                .scheduleTemplateName("Old Schedule")
                .store(store)
                .build();

        existingTemplate.getShiftTemplates().addAll(List.of(shift1, shift2)); // ✅ 일급 컬렉션 이용하여 추가
    }

    @Test
    @DisplayName("스케줄 템플릿 생성")
    void createScheduleTemplate() {
        // Given
        ScheduleTemplateRequest request = ScheduleTemplateRequest.builder()
                .scheduleTemplateName("New Schedule")
                .shiftTemplates(List.of(
                        new ShiftTemplateRequest("Morning Shift", LocalTime.of(8, 0), LocalTime.of(12, 0)),
                        new ShiftTemplateRequest("Evening Shift", LocalTime.of(16, 0), LocalTime.of(20, 0))
                ))
                .build();

        // 새로운 ScheduleTemplate 객체 생성
        ScheduleTemplate newTemplate = ScheduleTemplate.builder()
                .scheduleTemplateName("New Schedule")
                .store(store)
                .build();

        when(storeRepository.findById(1L)).thenReturn(Optional.of(store));
        when(scheduleTemplateRepository.save(any(ScheduleTemplate.class))).thenReturn(newTemplate);

        // When
        ScheduleTemplate createdTemplate = scheduleTemplateService.createScheduleTemplate(1L, request);

        // Then
        assertNotNull(createdTemplate);
        assertEquals("New Schedule", createdTemplate.getScheduleTemplateName());
        verify(scheduleTemplateRepository, times(1)).save(any(ScheduleTemplate.class));
    }

    @Test
    @DisplayName("스케줄 템플릿 단건 조회")
    void getScheduleTemplateOne() {
        // Given
        when(scheduleTemplateRepository.findById(1L)).thenReturn(Optional.of(existingTemplate));

        // When
        ScheduleTemplate foundTemplate = scheduleTemplateService.getScheduleTemplateOne(1L);

        // Then
        assertNotNull(foundTemplate);
        assertEquals("Old Schedule", foundTemplate.getScheduleTemplateName());
        verify(scheduleTemplateRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("특정 매장의 모든 스케줄 템플릿 조회")
    void getAllScheduleTemplatesByStore() {
        // Given
        List<ScheduleTemplate> templates = List.of(existingTemplate);
        when(scheduleTemplateRepository.findAllByStoreId(1L)).thenReturn(Optional.of(templates));

        // When
        AllScheduleTemplateResponse result = scheduleTemplateService.getAllScheduleTemplatesByStore(
                1L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getScheduleTemplateResponses().size());
        verify(scheduleTemplateRepository, times(1)).findAllByStoreId(1L);
    }

    @Test
    @DisplayName("스케줄 템플릿 업데이트 시 기존 ShiftTemplate 삭제 및 새로운 ShiftTemplate 추가 확인")
    void updateScheduleTemplate_ShouldRemoveExistingAndAddNewShiftTemplates() {
        // Given
        ScheduleTemplateRequest updatedRequest = ScheduleTemplateRequest.builder()
                .scheduleTemplateName("Updated Schedule")
                .shiftTemplates(List.of(
                        new ShiftTemplateRequest("Updated Morning Shift", LocalTime.of(8, 0), LocalTime.of(12, 0)),
                        new ShiftTemplateRequest("Updated Evening Shift", LocalTime.of(16, 0), LocalTime.of(20, 0))
                ))
                .build();

        when(storeRepository.findById(1L)).thenReturn(Optional.of(store));
        when(scheduleTemplateRepository.findById(1L)).thenReturn(Optional.of(existingTemplate));

        // When
        scheduleTemplateService.updateScheduleTemplate(1L, 1L, updatedRequest);

        // Then
        assertEquals("Updated Schedule", existingTemplate.getScheduleTemplateName());
        assertEquals(2, existingTemplate.getShiftTemplates().getList().size()); // ✅ 일급 컬렉션에서 리스트 가져오기
        assertEquals("Updated Morning Shift", existingTemplate.getShiftTemplates().getList().get(0).getShiftTemplateName());
        assertEquals("Updated Evening Shift", existingTemplate.getShiftTemplates().getList().get(1).getShiftTemplateName());

        // Mocking 환경에서는 save() 호출을 검증해야 한다.
        verify(scheduleTemplateRepository, times(1)).save(existingTemplate);
    }

    @Test
    @DisplayName("스케줄 템플릿 삭제")
    void deleteScheduleTemplate() {
        // Given
        when(scheduleTemplateRepository.findById(1L)).thenReturn(Optional.of(existingTemplate));
        doNothing().when(scheduleTemplateRepository).deleteById(1L);

        // When
        scheduleTemplateService.deleteScheduleTemplate(1L);

        // Then
        verify(scheduleTemplateRepository, times(1)).deleteById(1L);
    }
}
