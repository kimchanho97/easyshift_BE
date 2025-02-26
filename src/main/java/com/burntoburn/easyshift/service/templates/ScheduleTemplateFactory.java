package com.burntoburn.easyshift.service.templates;

import com.burntoburn.easyshift.dto.template.req.ScheduleTemplateRequest;
import com.burntoburn.easyshift.dto.template.req.ShiftTemplateRequest;
import com.burntoburn.easyshift.entity.templates.ScheduleTemplate;
import com.burntoburn.easyshift.entity.templates.ShiftTemplate;
import com.burntoburn.easyshift.entity.templates.collection.ShiftTemplates;
import com.burntoburn.easyshift.entity.store.Store;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class ScheduleTemplateFactory {

    public ScheduleTemplate createScheduleTemplate(Store store, ScheduleTemplateRequest request) {
        // ShiftTemplates 객체를 미리 초기화하여 null 방지
        ScheduleTemplate scheduleTemplate = ScheduleTemplate.builder()
                .scheduleTemplateName(request.getScheduleTemplateName())
                .store(store)
                .shiftTemplates(new ShiftTemplates()) // ✅ 일급 컬렉션 초기화
                .build();

        // ✅ ShiftTemplate 리스트를 생성하는 별도 메서드 활용
        List<ShiftTemplate> shiftTemplates = createShiftTemplates(request.getShiftTemplates());
        scheduleTemplate.getShiftTemplates().update(shiftTemplates);

        return scheduleTemplate;
    }

    // ✅ ShiftTemplate 리스트 생성 메서드 추가
    public List<ShiftTemplate> createShiftTemplates(List<ShiftTemplateRequest> shiftTemplateRequests) {
        return Optional.ofNullable(shiftTemplateRequests)
                .orElse(Collections.emptyList()) // null 방지
                .stream()
                .map(shiftRequest -> ShiftTemplate.builder()
                        .shiftTemplateName(shiftRequest.getShiftTemplateName())
                        .startTime(shiftRequest.getStartTime())
                        .endTime(shiftRequest.getEndTime())
                        .build())
                .collect(Collectors.toList());
    }
}
