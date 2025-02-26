package com.burntoburn.easyshift.controller.template;

import com.burntoburn.easyshift.dto.template.req.ScheduleTemplateRequest;
import com.burntoburn.easyshift.dto.template.res.ScheduleTemplateResponse;
import com.burntoburn.easyshift.entity.templates.ScheduleTemplate;
import com.burntoburn.easyshift.service.templates.ScheduleTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/schedule-templates")
public class ScheduleTemplateController {
    private final ScheduleTemplateService scheduleTemplateService;

    /**
     * 스케줄 템플릿 생성 API
     * @param storeId 매장 ID (PathVariable)
     * @param request 생성할 스케줄 템플릿 정보 (RequestBody)
     * @return 생성된 스케줄 템플릿 정보를 포함한 DTO 응답
     */
    @PostMapping("/{storeId}")
    public ResponseEntity<ScheduleTemplateResponse> createScheduleTemplate(
            @PathVariable Long storeId,
            @RequestBody ScheduleTemplateRequest request) {
        ScheduleTemplate createdTemplate = scheduleTemplateService.createScheduleTemplate(storeId, request);
        ScheduleTemplateResponse dto = createdTemplate.toDTO();
        return ResponseEntity.ok(dto);
    }



}
