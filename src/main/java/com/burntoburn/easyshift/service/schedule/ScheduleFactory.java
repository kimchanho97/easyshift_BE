package com.burntoburn.easyshift.service.schedule;

import com.burntoburn.easyshift.dto.schedule.req.scheduleCreate.ScheduleRequest;
import com.burntoburn.easyshift.dto.schedule.req.scheduleCreate.ShiftRequest;
import com.burntoburn.easyshift.entity.schedule.Schedule;
import com.burntoburn.easyshift.entity.schedule.ScheduleStatus;
import com.burntoburn.easyshift.entity.schedule.Shift;
import com.burntoburn.easyshift.entity.schedule.collection.Shifts;
import com.burntoburn.easyshift.entity.store.Store;
import com.burntoburn.easyshift.entity.templates.ScheduleTemplate;
import com.burntoburn.easyshift.entity.templates.ShiftTemplate;
import org.springframework.stereotype.Component;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ScheduleFactory {

    public Schedule createSchedule(Store store, ScheduleTemplate scheduleTemplate, ScheduleRequest request) {
        Schedule schedule = Schedule.builder()
                .scheduleName(request.getScheduleName())
                .scheduleMonth(request.getScheduleMonth())
                .scheduleStatus(ScheduleStatus.PENDING)
                .store(store)
                .shifts(new Shifts())
                .build();

        List<ShiftRequest> shiftDetails = request.getShiftDetails();
        List<Shift> shifts = createShifts(schedule, scheduleTemplate, shiftDetails);
        schedule.addShift(shifts);

        return schedule;
    }

    public List<Shift> createShifts(Schedule schedule, ScheduleTemplate scheduleTemplate, List<ShiftRequest> shiftDetails) {
        YearMonth scheduleMonth = schedule.getScheduleMonth();
        int daysInMonth = scheduleMonth.lengthOfMonth();
        Map<Long, Integer> shiftWorkerMap = new HashMap<>();

        for (ShiftRequest shiftRequest : shiftDetails) {
            shiftWorkerMap.put(shiftRequest.getShiftTemplateId(), shiftRequest.getExpectedWorkers());
        }

        List<Shift> shifts = new ArrayList<>();
        for (int day = 1; day <= daysInMonth; day++) {
            shifts.addAll(createShiftsForDay(schedule, scheduleTemplate, day, shiftWorkerMap));
        }
        return shifts;
    }

    private List<Shift> createShiftsForDay(Schedule schedule, ScheduleTemplate scheduleTemplate, int day, Map<Long, Integer> shiftWorkerMap) {
        List<Shift> shifts = new ArrayList<>();
        Map<Long, ShiftTemplate> uniqueTemplates = new HashMap<>();

        for (ShiftTemplate shiftTemplate : scheduleTemplate.getShiftTemplates().getList()) {
            uniqueTemplates.putIfAbsent(shiftTemplate.getId(), shiftTemplate);
        }

        for (ShiftTemplate shiftTemplate : uniqueTemplates.values()) {
            int expectedWorkers = shiftWorkerMap.getOrDefault(shiftTemplate.getId(), 1);
            for (int i = 0; i < expectedWorkers; i++) {
                shifts.add(createShift(schedule, shiftTemplate, day));
            }
        }
        return shifts;
    }

    private Shift createShift(Schedule schedule, ShiftTemplate shiftTemplate, int day) {
        return Shift.builder()
                .schedule(schedule)
                .shiftDate(schedule.getScheduleMonth().atDay(day))
                .shiftName(shiftTemplate.getShiftTemplateName())
                .startTime(shiftTemplate.getStartTime())
                .endTime(shiftTemplate.getEndTime())
                .user(null)
                .build();
    }

    public Schedule updateSchedule(Schedule schedule, ScheduleRequest request) {
        schedule.updateSchedule(
                request.getScheduleName(),
                request.getScheduleMonth(),
                schedule.getShifts().getList()
        );
        return schedule;
    }
}
