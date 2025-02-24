package com.burntoburn.easyshift.service.schedule;

import com.burntoburn.easyshift.dto.schedule.req.ScheduleRequest;
import com.burntoburn.easyshift.entity.schedule.Schedule;
import java.util.List;

public interface ScheduleService {

    // 스케줄 생성
    Schedule createSchedule(Long storeId, Long scheduleTemplateId, ScheduleRequest request);

    // 스케줄 수정 (이름, 설명, 날짜 변경)
    Schedule updateSchedule(Long scheduleId, ScheduleRequest request);

    // 스케줄 삭제
    void deleteSchedule(Long scheduleId);

    // 매장별 스케줄 조회
    List<Schedule> getSchedulesByStore(Long storeId);

}
