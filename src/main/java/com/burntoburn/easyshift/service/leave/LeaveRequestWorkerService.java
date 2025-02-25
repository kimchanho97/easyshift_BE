package com.burntoburn.easyshift.service.leave;

import com.burntoburn.easyshift.dto.schedule.req.LeaveRequestDto;
import com.burntoburn.easyshift.entity.leave.LeaveRequest;
import java.util.List;

public interface LeaveRequestWorkerService {
    // 휴무일 생성
    LeaveRequest createLeaveRequest(Long userId, LeaveRequestDto requestDto);

    // 특정 ID 로 휴무일 조회
    LeaveRequest getLeaveRequest(Long leaveRequestId);

    // 특정 userId 로 휴무일 조회
    List<LeaveRequest> getLeaveRequestsByUser(Long userId);

    // 휴무일 생성
    LeaveRequest updateLeaveRequest(Long leaveRequestId, LeaveRequestDto requestDto);

    // 휴무일 삭제
    void cancelLeaveRequest(Long leaveRequestId, Long userId);
}
