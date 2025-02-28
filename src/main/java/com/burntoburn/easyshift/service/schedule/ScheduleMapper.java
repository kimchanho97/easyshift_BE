package com.burntoburn.easyshift.service.schedule;

import com.burntoburn.easyshift.dto.schedule.res.ScheduleData.StoreScheduleListResponse;
import com.burntoburn.easyshift.dto.schedule.res.ScheduleData.StoreScheduleResponse;
import com.burntoburn.easyshift.dto.schedule.res.ScheduleWithShifts.ScheduleWithShiftsDto;
import com.burntoburn.easyshift.dto.schedule.res.ScheduleWithShifts.ShiftsDto;
import com.burntoburn.easyshift.entity.schedule.Schedule;
import com.burntoburn.easyshift.entity.schedule.Shift;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class ScheduleMapper {

    /** 개별 Schedule 변환 */
    public StoreScheduleResponse toResponse(Schedule schedule) {
        return StoreScheduleResponse.builder()
                .scheduleId(schedule.getId())
                .scheduleName(schedule.getScheduleName())
                .scheduleMonth(schedule.getScheduleMonth().toString())
                .status(schedule.getScheduleStatus().name())
                .build();
    }

    /** 매장의 모든 Schedule 변환 */
    public StoreScheduleListResponse toListResponse(List<Schedule> schedules) {
        List<StoreScheduleResponse> scheduleResponses = schedules.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return StoreScheduleListResponse.builder()
                .schedules(scheduleResponses)
                .build();
    }

    /** 개별 Schedule을 Shift와 함께 변환 */
    public ScheduleWithShiftsDto toScheduleWithShifts(Schedule schedule) {
        List<ShiftsDto> shiftDtos = schedule.getShifts().getList().stream()
                .map(this::toShiftDto)
                .collect(Collectors.toList());

        return ScheduleWithShiftsDto.builder()
                .scheduleId(schedule.getId())
                .scheduleName(schedule.getScheduleName())
                .scheduleMonth(schedule.getScheduleMonth())
                .scheduleStatus(schedule.getScheduleStatus().name())
                .shifts(shiftDtos)
                .build();
    }

    /** Shift 변환 */
    private ShiftsDto toShiftDto(Shift shift) {
        return ShiftsDto.builder()
                .shiftId(shift.getId())
                .shiftName(shift.getShiftName())
                .shiftDate(shift.getShiftDate())
                .startTime(shift.getStartTime())
                .endTime(shift.getEndTime())
                .build();
    }
}
