package com.burntoburn.easyshift.service.schedule;

import com.burntoburn.easyshift.dto.schedule.req.ScheduleTemplateRequest;
import com.burntoburn.easyshift.dto.schedule.res.ScheduleTemplateResponse;
import java.util.List;

public interface ScheduleTemplateService {

    // 특정 매장의 모든 스케줄 템플릿 조회
    List<ScheduleTemplateResponse> getScheduleTemplatesByStore(Long storeId);

    // 스케줄 템플릿 생성
    ScheduleTemplateResponse createScheduleTemplate(ScheduleTemplateRequest request);

    // 스케줄 템플릿 수정
    ScheduleTemplateResponse updateScheduleTemplate(Long templateId, ScheduleTemplateRequest request);

    // 스케줄 템플릿 삭제
    void deleteScheduleTemplate(Long templateId);
}
