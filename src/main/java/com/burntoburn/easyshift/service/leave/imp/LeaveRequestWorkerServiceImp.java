package com.burntoburn.easyshift.service.leave.imp;

import com.burntoburn.easyshift.dto.schedule.req.LeaveRequestDto;
import com.burntoburn.easyshift.entity.leave.LeaveRequest;
import com.burntoburn.easyshift.entity.schedule.Schedule;
import com.burntoburn.easyshift.entity.user.User;
import com.burntoburn.easyshift.repository.leave.LeaveRequestRepository;
import com.burntoburn.easyshift.repository.schedule.ScheduleRepository;
import com.burntoburn.easyshift.repository.user.UserRepository;
import com.burntoburn.easyshift.service.leave.LeaveRequestFactory;
import com.burntoburn.easyshift.service.leave.LeaveRequestWorkerService;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LeaveRequestWorkerServiceImp implements LeaveRequestWorkerService {
    private final LeaveRequestRepository leaveRequestRepository;
    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;
    private final LeaveRequestFactory leaveRequestFactory;

    @Override
    public LeaveRequest createLeaveRequest(Long userId, LeaveRequestDto requestDto) {
        // user 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("user 존재하지 않음"));

        // Schedule 확인
        Schedule schedule = scheduleRepository.findById(requestDto.getScheduleId())
                .orElseThrow(() -> new NoSuchElementException("getScheduleId fails"));

        LeaveRequest leaveRequest = leaveRequestFactory.createLeaveRequest(user, schedule, requestDto.getDate());
        leaveRequestRepository.save(leaveRequest);
        return leaveRequest;
    }

    @Override
    public LeaveRequest getLeaveRequest(Long leaveRequestId) {
        return leaveRequestRepository.findById(leaveRequestId)
                .orElseThrow(()->new NoSuchElementException("leaveRequestId 존재하지 않음"));
    }

    @Override
    public List<LeaveRequest> getLeaveRequestsByUser(Long userId) {
        return leaveRequestRepository.findAllByUserId(userId);
    }

    @Transactional
    @Override
    public LeaveRequest updateLeaveRequest(Long leaveRequestId, LeaveRequestDto requestDto) {
        LeaveRequest leaveRequest = getLeaveRequest(leaveRequestId);

        // 팩토리 패턴으로 수정
        leaveRequestFactory.updateLeaveRequest(leaveRequest, requestDto.getDate());

        return leaveRequestRepository.save(leaveRequest); // [명시적 저장] JPA 의 변경 감지로 수정 예정
    }

    @Override
    public void cancelLeaveRequest(Long leaveRequestId, Long userId) {
        getLeaveRequest(leaveRequestId);
        leaveRequestRepository.deleteById(leaveRequestId);
    }
}
