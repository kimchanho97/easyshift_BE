package com.burntoburn.easyshift.service.schedule;

import com.burntoburn.easyshift.dto.schedule.req.LeaveRequestDto;
import com.burntoburn.easyshift.dto.schedule.res.LeaveRequestResponse;
import java.util.List;

public interface ScheduleLeaveService {
    // 스케줄 휴무 신청
    LeaveRequestResponse requestLeave(Long scheduleId, LeaveRequestDto request);

    // 특정 스케줄의 휴무 신청 목록 조회
    List<LeaveRequestResponse> getLeaveRequests(Long scheduleId);

    // 휴무 신청 승인 또는 거절
    LeaveRequestResponse approveLeaveRequest(Long leaveRequestId, boolean isApproved);

}
