package com.burntoburn.easyshift.service.schedule;

import com.burntoburn.easyshift.dto.schedule.req.ScheduleRequest;
import com.burntoburn.easyshift.entity.schedule.Schedule;
import com.burntoburn.easyshift.entity.schedule.ScheduleStatus;
import com.burntoburn.easyshift.entity.schedule.Shift;
import com.burntoburn.easyshift.entity.schedule.collection.Shifts;
import com.burntoburn.easyshift.entity.store.Store;
import com.burntoburn.easyshift.entity.templates.ScheduleTemplate;
import com.burntoburn.easyshift.entity.templates.ShiftTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ScheduleFactory {

    // schedule 객체 생성
    public Schedule createSchedule(Store store, ScheduleTemplate scheduleTemplate, ScheduleRequest request) {
        Schedule schedule = Schedule.builder()
                .scheduleName(request.getScheduleName())
                .scheduleMonth(request.getScheduleMonth())
                .scheduleStatus(ScheduleStatus.PENDING) // 초기 상태는 PENDING
                .store(store)
                .shifts(new Shifts()) // Shifts 초기화
                .build();

        // ShiftTemplate을 기반으로 빈 Shift 목록 생성
        List<Shift> shifts = createShifts(schedule, scheduleTemplate);
        schedule.getShifts().addAll(shifts);
        return schedule;
    }

    // ScheduleTemplate 을 기반으로 Shift 생성
    public List<Shift> createShifts(Schedule schedule, ScheduleTemplate scheduleTemplate) {
        return scheduleTemplate.getShiftTemplates().getList().stream()
                .map(shiftTemplate -> createShift(schedule, shiftTemplate))
                .toList();
    }

    // 개별 Shift 객체 생성
    private Shift createShift(Schedule schedule, ShiftTemplate shiftTemplate) {
        return Shift.builder()
                .schedule(schedule)
                .shiftDate(null)
                .shiftName(shiftTemplate.getShiftTemplateName())
                .startTime(shiftTemplate.getStartTime())
                .endTime(shiftTemplate.getEndTime())
                .build();
    }

    public Schedule updateSchedule(Schedule schedule, ScheduleRequest request) {
        schedule.updateSchedule(
                request.getScheduleName(),
                request.getScheduleMonth(),
                schedule.getShifts().getList() // 기존 Shift 유지
        );
        return schedule;
    }

}
