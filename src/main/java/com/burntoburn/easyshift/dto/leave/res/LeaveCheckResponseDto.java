package com.burntoburn.easyshift.dto.leave.res;

import com.burntoburn.easyshift.entity.leave.LeaveRequest;
import com.burntoburn.easyshift.entity.schedule.Schedule;
import com.burntoburn.easyshift.entity.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LeaveCheckResponseDto {
    private LeaveScheduleDto schedule;
    private List<LeaveUserDto> users;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LeaveScheduleDto {
        private Long scheduleId;
        private String scheduleName;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LeaveUserDto {
        private Long userId;
        private String name;
        private String email;
        private List<LocalDate> dates;
    }

    public static LeaveCheckResponseDto fromEntity(List<LeaveRequest> leaveRequests) {
        // 스케줄 정보는 첫번째 LeaveRequest에서 가져옴 (모두 동일한 스케줄이라 가정)
        Schedule schedule = leaveRequests.get(0).getSchedule();
        LeaveScheduleDto scheduleDto = new LeaveScheduleDto(schedule.getId(), schedule.getScheduleName());

        // 사용자별로 LeaveRequest의 날짜를 그룹화
        Map<User, List<LocalDate>> userDates = leaveRequests.stream()
                .collect(Collectors.groupingBy(
                        LeaveRequest::getUser,
                        Collectors.mapping(LeaveRequest::getDate, Collectors.toList())
                ));

        // 그룹화된 결과를 DTO의 사용자 목록으로 변환
        List<LeaveUserDto> userLeaveDtos = userDates.entrySet().stream()
                .map(entry -> new LeaveUserDto(
                        entry.getKey().getId(),
                        entry.getKey().getName(),
                        entry.getKey().getEmail(),
                        entry.getValue()
                ))
                .collect(Collectors.toList());

        return new LeaveCheckResponseDto(scheduleDto, userLeaveDtos);
    }
}
