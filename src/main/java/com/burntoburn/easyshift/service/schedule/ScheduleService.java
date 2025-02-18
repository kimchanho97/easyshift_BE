package com.burntoburn.easyshift.service.schedule;


import com.burntoburn.easyshift.dto.schedule.req.ScheduleRequest;
import com.burntoburn.easyshift.dto.schedule.res.ScheduleResponse;
import java.util.List;

public interface ScheduleService {

    // 스케줄 생성
    ScheduleResponse createSchedule(ScheduleRequest request);

    // 스케줄 삭제
    void deleteSchedule(Long scheduleId);

    // 스케줄 조회 (매장)
    List<ScheduleResponse> getSchedulesByStore(Long storeId);

    // 스케줄 조회 (근로자) - 자신의 특정 스케줄(월 단위) 조회
    List<ScheduleResponse> getSchedulesByWorker(Long storeId, Long userId, String date);
}
