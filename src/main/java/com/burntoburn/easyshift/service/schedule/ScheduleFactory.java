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
import java.time.YearMonth;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.IntStream;

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
        List<ShiftRequest> shiftDetails = request.getShiftDetails();
        List<Shift> shifts = createShifts(schedule, scheduleTemplate, shiftDetails);

        schedule.addShift(shifts);

        return schedule;
    }

    // ScheduleTemplate 을 기반으로 한 달간 Shift 생성
    public List<Shift> createShifts(Schedule schedule, ScheduleTemplate scheduleTemplate, List<ShiftRequest> shiftDetails) {
        YearMonth scheduleMonth = schedule.getScheduleMonth();
        int daysInMonth = scheduleMonth.lengthOfMonth(); // 한 달의 일 수 계산

        // ShiftRequest 리스트를 Map 형태로 변환 (shiftTemplateId -> expectedWorkers)
        Map<Long, Integer> shiftWorkerMap = shiftDetails.stream()
                .collect(Collectors.toMap(ShiftRequest::getShiftTemplateId, ShiftRequest::getExpectedWorkers));

        return scheduleTemplate.getShiftTemplates().getList().stream()
                .flatMap(shiftTemplate -> createDailyShifts(schedule, shiftTemplate, daysInMonth, shiftWorkerMap).stream())
                .toList();
    }

    // 한 달간 모든 날짜에 대해 expectedWorkers 수만큼 Shift 생성
    private List<Shift> createDailyShifts(Schedule schedule, ShiftTemplate shiftTemplate, int daysInMonth, Map<Long, Integer> shiftWorkerMap) {
        int expectedWorkers = shiftWorkerMap.getOrDefault(shiftTemplate.getId(), 1); // 기본값 1

        return IntStream.rangeClosed(1, daysInMonth)
                .boxed()
                .flatMap(day -> createWorkerShifts(schedule, shiftTemplate, day, expectedWorkers).stream()) // 하루에 여러 개의 Shift 생성
                .toList();
    }

    // 하루 동안 expectedWorkers 수만큼 Shift 생성
    private List<Shift> createWorkerShifts(Schedule schedule, ShiftTemplate shiftTemplate, int day, int expectedWorkers) {
        return IntStream.range(0, expectedWorkers)
                .mapToObj(workerIndex -> createShift(schedule, shiftTemplate, day, workerIndex))
                .toList();
    }

    // 개별 Shift 객체 생성 (날짜 포함, workerIndex 추가)
    private Shift createShift(Schedule schedule, ShiftTemplate shiftTemplate, int day, int workerIndex) {
        return Shift.builder()
                .schedule(schedule)
                .shiftDate(schedule.getScheduleMonth().atDay(day)) // 날짜 설정
                .shiftName(shiftTemplate.getShiftTemplateName() + " - " + (workerIndex + 1)) // 근무 번호 추가
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
