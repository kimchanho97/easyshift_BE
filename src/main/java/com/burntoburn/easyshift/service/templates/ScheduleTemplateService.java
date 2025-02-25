package com.burntoburn.easyshift.service.templates;

import com.burntoburn.easyshift.dto.schedule.req.scheduleTemplate.ScheduleTemplateRequest;
import com.burntoburn.easyshift.entity.templates.ScheduleTemplate;
import java.util.List;

public interface ScheduleTemplateService {

    // 스케줄 템플릿 생성
    ScheduleTemplate createScheduleTemplate(Long storeId ,ScheduleTemplateRequest request);

    // ShiftTemplate 조회 - 단건
    ScheduleTemplate getScheduleTemplateOne(Long id);

    // ShiftTemplate 조회 - 전체
    List<ScheduleTemplate> getAllScheduleTemplates();

    // 스케줄 템플릿 수정
    ScheduleTemplate updateScheduleTemplate(Long storeId, Long scheduleTemplateId, ScheduleTemplateRequest request);

    // 스케줄 템플릿 삭제
    void deleteScheduleTemplate(Long id);





}
