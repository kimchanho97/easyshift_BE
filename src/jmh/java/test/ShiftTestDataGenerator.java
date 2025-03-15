package test;

import com.burntoburn.easyshift.entity.schedule.Schedule;
import com.burntoburn.easyshift.entity.schedule.Shift;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ShiftTestDataGenerator {

    /**
     * 2025년 4월을 기준으로 30일간 근무를 생성
     *
     * @param schedule     해당 근무가 속한 스케줄
     * @param shiftsPerDay 하루 총 근무 개수 (반드시 3의 배수)
     * @return 2025년 4월 1일 ~ 4월 30일까지 하루 shiftsPerDay 개씩 Shift 목록
     */
    public static List<Shift> generateShifts(Schedule schedule, int shiftsPerDay) {
        if (shiftsPerDay % 3 != 0) {
            throw new IllegalArgumentException("하루 근무 개수는 반드시 3의 배수여야 합니다.");
        }

        List<Shift> shifts = new ArrayList<>();
        LocalDate startDate = LocalDate.of(2025, 4, 1);
        LocalDate endDate = startDate.plusDays(29); // 30일 설정

        int shiftsPerType = shiftsPerDay / 3; // Open, Middle, Close 각각의 개수

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            // Open 근무 생성
            for (int i = 0; i < shiftsPerType; i++) {
                shifts.add(Shift.builder()
                        .shiftName("Open")
                        .shiftDate(date)
                        .startTime(LocalTime.of(7, 0))
                        .endTime(LocalTime.of(12, 0))
                        .schedule(schedule)
                        .shiftTemplateId(1L)
                        .build());
            }
            // Middle 근무 생성
            for (int i = 0; i < shiftsPerType; i++) {
                shifts.add(Shift.builder()
                        .shiftName("Middle")
                        .shiftDate(date)
                        .startTime(LocalTime.of(12, 0))
                        .endTime(LocalTime.of(17, 0))
                        .schedule(schedule)
                        .shiftTemplateId(2L)
                        .build());
            }
            // Close 근무 생성
            for (int i = 0; i < shiftsPerType; i++) {
                shifts.add(Shift.builder()
                        .shiftName("Close")
                        .shiftDate(date)
                        .startTime(LocalTime.of(17, 0))
                        .endTime(LocalTime.of(22, 0))
                        .schedule(schedule)
                        .shiftTemplateId(3L)
                        .build());
            }
        }
        return shifts;
    }
}