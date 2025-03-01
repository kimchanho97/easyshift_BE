package com.burntoburn.easyshift.service.templates;

import com.burntoburn.easyshift.dto.template.req.ScheduleTemplateRequest;
import com.burntoburn.easyshift.dto.template.res.AllScheduleTemplateResponse;
import com.burntoburn.easyshift.entity.templates.ScheduleTemplate;

public interface ScheduleTemplateService {

    // 스케줄 템플릿 생성
    ScheduleTemplate createScheduleTemplate(Long storeId, ScheduleTemplateRequest request);

    // ShiftTemplate 조회 - 단건
    ScheduleTemplate getScheduleTemplateOne(Long id);

    // ShiftTemplate 조회 - 전체
    AllScheduleTemplateResponse getAllScheduleTemplatesByStore(Long storeId);

    // 스케줄 템플릿 삭제
    void deleteScheduleTemplate(Long id);


}
