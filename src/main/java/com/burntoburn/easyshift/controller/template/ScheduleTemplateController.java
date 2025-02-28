package com.burntoburn.easyshift.controller.template;

import com.burntoburn.easyshift.dto.template.req.ScheduleTemplateRequest;
import com.burntoburn.easyshift.dto.template.req.update.ScheduleTemplateUpdate;
import com.burntoburn.easyshift.dto.template.res.AllScheduleTemplateResponse;
import com.burntoburn.easyshift.dto.template.res.StoreTemplates;
import com.burntoburn.easyshift.entity.templates.ScheduleTemplate;
import com.burntoburn.easyshift.service.templates.ScheduleTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ScheduleTemplateController {
    private final ScheduleTemplateService scheduleTemplateService;

    /**
     * 스케줄 템플릿 생성 API
     * @param storeId 매장 ID (PathVariable)
     * @param request 생성할 스케줄 템플릿 정보 (RequestBody)
     * @return 생성된 스케줄 템플릿 정보를 포함한 DTO 응답
     */
    @PostMapping("/stores/{storeId}/schedule-templates")
    public ResponseEntity<StoreTemplates> createScheduleTemplate(
            @PathVariable Long storeId,
            @RequestBody ScheduleTemplateRequest request) {
        ScheduleTemplate createdTemplate = scheduleTemplateService.createScheduleTemplate(storeId, request);
        StoreTemplates dto = createdTemplate.toDTO();
        return ResponseEntity.ok(dto);
    }

    /**
     * @param storeId  매장 ID (PathVariable)
     * @param scheduleTemplateId 수정할 스케줄 템플릿 ID (PathVariable)*
     * @param request 수정할 스케줄 템플릿 정보 (RequestBody)
     * @return 삭제 후 204 No Content 응답 반환
     **/
    @PatchMapping("/stores/{storeId}/schedule-templates/{scheduleTemplateId}")
    public ResponseEntity<StoreTemplates> updateScheduleTemplate(@PathVariable Long storeId,
                                                                 @PathVariable Long scheduleTemplateId,
                                                                 @RequestBody ScheduleTemplateUpdate request){
        scheduleTemplateService.updateScheduleTemplate(storeId, scheduleTemplateId, request);
        return ResponseEntity.noContent().build();
    }


    /**
     * @param @param storeId 매장 ID
     * @return 해당 매장의 스케줄 템플릿 리스트 (DTO 변환 후 반환)
     **/
    @GetMapping("/stores/{storeId}/schedule-templates")
    public ResponseEntity<AllScheduleTemplateResponse> getScheduleTemplate(@PathVariable Long storeId) {
        AllScheduleTemplateResponse allScheduleTemplatesByStore = scheduleTemplateService
                .getAllScheduleTemplatesByStore(storeId);

        return ResponseEntity.ok(allScheduleTemplatesByStore);
    }

    /**
     * @param scheduleTemplateId 특정 스케줄 템플릿 ID (PathVariable)*
     * @return 특정 스케줄 템플릿의 쉬프트 템플릿 조회
     **/
    @GetMapping("/schedule-templates/{scheduleTemplateId}")
    public ResponseEntity<StoreTemplates> getShiftTemplatesByScheduleTemplate(@PathVariable Long scheduleTemplateId){
        ScheduleTemplate scheduleTemplateOne = scheduleTemplateService.getScheduleTemplateOne(scheduleTemplateId);
        StoreTemplates scheduleTemplate = scheduleTemplateOne.toDTO();
        return ResponseEntity.ok(scheduleTemplate);
    }

    /**
     * @param scheduleTemplateId 삭제할 스케줄 템플릿 ID (PathVariable)*
     * @return 삭제 후 204 No Content 응답 반환
    **/
    @DeleteMapping("/schedule-templates/{scheduleTemplateId}")
    public ResponseEntity<Void> deleteScheduleTemplate(@PathVariable Long scheduleTemplateId){
        scheduleTemplateService.deleteScheduleTemplate(scheduleTemplateId);
        return ResponseEntity.noContent().build();
    }







}
