package com.burntoburn.easyshift.service.schedule.imp;

import com.burntoburn.easyshift.entity.schedule.ScheduleTemplate;
import com.burntoburn.easyshift.repository.schedule.ScheduleTemplateRepository;
import com.burntoburn.easyshift.service.schedule.ScheduleTemplateService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class ScheduleTemplateServiceImplTest {

    @Autowired
    private ScheduleTemplateService scheduleTemplateService; // ✅ 실제 서비스 객체

    @MockitoBean
    private ScheduleTemplateRepository scheduleTemplateRepository; // ✅ Mock Repository (Spring Context 내에서 동작)

    @Test
    @DisplayName("스케줄 템플릿 생성")
    void createScheduleTemplate() {
        // Given
        ScheduleTemplate newScheduleTemplate = ScheduleTemplate.builder()
                .scheduleTemplateName("Weekly Schedule")
                .build();

        // Mock 설정: save()가 호출되면 newScheduleTemplate 반환
        when(scheduleTemplateRepository.save(any(ScheduleTemplate.class))).thenReturn(newScheduleTemplate);

        // When
        ScheduleTemplate createdTemplate = scheduleTemplateService.createScheduleTemplate(newScheduleTemplate);

        // Then
        assertNotNull(createdTemplate);
        assertEquals("Weekly Schedule", createdTemplate.getScheduleTemplateName());
        verify(scheduleTemplateRepository, times(1)).save(any(ScheduleTemplate.class));
    }

    @Test
    @DisplayName("스케줄 템플릿 조회 - 단건")
    void getScheduleTemplateOne() {
        // Given
        ScheduleTemplate scheduleTemplate = ScheduleTemplate.builder()
                .id(1L)
                .scheduleTemplateName("Daily Schedule")
                .build();

        when(scheduleTemplateRepository.findById(1L)).thenReturn(Optional.of(scheduleTemplate));

        // When
        ScheduleTemplate foundTemplate = scheduleTemplateService.getScheduleTemplateOne(1L);

        // Then
        assertNotNull(foundTemplate);
        assertEquals(1L, foundTemplate.getId());
        assertEquals("Daily Schedule", foundTemplate.getScheduleTemplateName());
        verify(scheduleTemplateRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("스케줄 템플릿 조회 - 전체")
    void getAllScheduleTemplates() {
        // Given
        List<ScheduleTemplate> templateList = Arrays.asList(
                ScheduleTemplate.builder().scheduleTemplateName("Morning Shift").build(),
                ScheduleTemplate.builder().scheduleTemplateName("Evening Shift").build()
        );

        when(scheduleTemplateRepository.findAll()).thenReturn(templateList);

        // When
        List<ScheduleTemplate> templates = scheduleTemplateService.getAllScheduleTemplates();

        // Then
        assertNotNull(templates);
        assertEquals(2, templates.size());
        assertEquals("Morning Shift", templates.get(0).getScheduleTemplateName());
        assertEquals("Evening Shift", templates.get(1).getScheduleTemplateName());
        verify(scheduleTemplateRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("스케줄 템플릿 업데이트")
    void updateScheduleTemplate() {
        // Given
        ScheduleTemplate existingTemplate = ScheduleTemplate.builder()
                .id(1L)
                .scheduleTemplateName("Old Schedule")
                .build();

        ScheduleTemplate updatedTemplateDetails = ScheduleTemplate.builder()
                .scheduleTemplateName("Updated Schedule")
                .build();

        when(scheduleTemplateRepository.findById(1L)).thenReturn(Optional.of(existingTemplate));
        when(scheduleTemplateRepository.save(any(ScheduleTemplate.class))).thenReturn(updatedTemplateDetails);

        // When
        ScheduleTemplate updatedTemplate = scheduleTemplateService.updateScheduleTemplate(1L, updatedTemplateDetails);

        // Then
        assertNotNull(updatedTemplate);
        assertEquals("Updated Schedule", updatedTemplate.getScheduleTemplateName());
        verify(scheduleTemplateRepository, times(1)).findById(1L);
        verify(scheduleTemplateRepository, times(1)).save(any(ScheduleTemplate.class));
    }

    @Test
    @DisplayName("스케줄 템플릿 삭제")
    void deleteScheduleTemplate() {
        // Given
        Long templateId = 1L;
        ScheduleTemplate templateToDelete = ScheduleTemplate.builder()
                .id(templateId)
                .scheduleTemplateName("Old Schedule")
                .build();

        // Mock 설정: 삭제할 객체 조회 가능하도록 설정
        when(scheduleTemplateRepository.findById(templateId)).thenReturn(Optional.of(templateToDelete));

        // Mock 설정: deleteById() 호출 시 아무 동작도 하지 않도록 설정
        doNothing().when(scheduleTemplateRepository).deleteById(templateId);

        // When
        scheduleTemplateService.deleteScheduleTemplate(templateId);

        // Then
        verify(scheduleTemplateRepository, times(1)).findById(templateId);
        verify(scheduleTemplateRepository, times(1)).deleteById(templateId);

        // 삭제 후 findById()가 Optional.empty()를 반환하는지 검증
        when(scheduleTemplateRepository.findById(templateId)).thenReturn(Optional.empty());
        Optional<ScheduleTemplate> deletedTemplate = scheduleTemplateRepository.findById(templateId);
        assertTrue(deletedTemplate.isEmpty(), "삭제된 ScheduleTemplate이 더 이상 존재하지 않아야 합니다.");
    }
}
