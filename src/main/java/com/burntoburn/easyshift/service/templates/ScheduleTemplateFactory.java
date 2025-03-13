package com.burntoburn.easyshift.service.templates;

import com.burntoburn.easyshift.dto.template.req.ScheduleTemplateRequest;
import com.burntoburn.easyshift.dto.template.req.ShiftTemplateRequest;
import com.burntoburn.easyshift.entity.store.Store;
import com.burntoburn.easyshift.entity.templates.ScheduleTemplate;
import com.burntoburn.easyshift.entity.templates.ShiftTemplate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ScheduleTemplateFactory {
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    public ScheduleTemplate createScheduleTemplate(Store store, ScheduleTemplateRequest request) {
        // ShiftTemplates 객체를 미리 초기화하여 null 방지
        ScheduleTemplate scheduleTemplate = ScheduleTemplate.builder()
                .scheduleTemplateName(request.getScheduleTemplateName())
                .store(store)
                .build();

        // ✅ ShiftTemplate 리스트를 생성하는 별도 메서드 활용
        List<ShiftTemplate> shiftTemplates = createShiftTemplates(scheduleTemplate, request.getShiftTemplates());
        shiftTemplates.forEach(scheduleTemplate::addShiftTemplate);

        return scheduleTemplate;
    }

    // ✅ ShiftTemplate 리스트 생성 메서드 추가
    public List<ShiftTemplate> createShiftTemplates(ScheduleTemplate scheduleTemplate, List<ShiftTemplateRequest> shiftTemplateRequests) {
        return Optional.ofNullable(shiftTemplateRequests)
                .orElse(Collections.emptyList()) // null 방지
                .stream()
                // ScheduleTemplate 연관관계 주입
                .map(shiftRequest -> ShiftTemplate.builder()
                        .scheduleTemplate(scheduleTemplate)
                        .shiftTemplateName(shiftRequest.getShiftName())
                        .startTime(LocalTime.parse(shiftRequest.getStartTime(), TIME_FORMATTER))
                        .endTime(LocalTime.parse(shiftRequest.getEndTime(),TIME_FORMATTER))
                        .build())
                .collect(Collectors.toList());
    }
}
