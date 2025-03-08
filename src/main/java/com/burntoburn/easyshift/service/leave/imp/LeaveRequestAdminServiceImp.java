package com.burntoburn.easyshift.service.leave.imp;

import com.burntoburn.easyshift.dto.leave.res.LeaveCheckResponseDto;
import com.burntoburn.easyshift.entity.leave.LeaveRequest;
import com.burntoburn.easyshift.exception.leave.LeaveException;
import com.burntoburn.easyshift.repository.leave.LeaveRequestRepository;
import com.burntoburn.easyshift.service.leave.LeaveRequestAdminService;
import com.burntoburn.easyshift.service.leave.LeaveRequestFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaveRequestAdminServiceImp implements LeaveRequestAdminService {
    private final LeaveRequestRepository leaveRequestRepository;
    private final LeaveRequestFactory leaveRequestFactory;


    @Override
    public LeaveRequest approveLeaveRequest(Long leaveRequestId) {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(leaveRequestId)
                .orElseThrow(LeaveException::leaveNotFound);
        leaveRequestFactory.approvedRequest(leaveRequest);

        return leaveRequest;
    }

    @Override
    public LeaveRequest rejectLeaveRequest(Long leaveRequestId) {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(leaveRequestId)
                .orElseThrow(LeaveException::leaveNotFound);
        leaveRequestFactory.rejectRequest(leaveRequest);
        return leaveRequest;
    }

    @Override
    public List<LeaveRequest> getLeaveRequestsByMonth(YearMonth scheduleMonth) {

        return leaveRequestRepository.findAllByScheduleMonth(scheduleMonth);
    }

    @Transactional
    public LeaveCheckResponseDto getLeaveRequestsForSchedule(Long scheduleId) {
        // 스케줄 아이디로 LeaveRequest들을 조회
        List<LeaveRequest> leaveRequests = leaveRequestRepository.findByScheduleId(scheduleId);

        // LeaveRequest 데이터가 없는 경우 서비스 로직에서 예외 처리
        if (leaveRequests == null || leaveRequests.isEmpty()) {
            throw LeaveException.leaveNotFound();
        }

        // DTO 내부의 fromEntity 메서드를 사용해 변환
        return LeaveCheckResponseDto.fromEntity(leaveRequests);
    }
}
