package test;

import com.burntoburn.easyshift.entity.schedule.Schedule;

public class ScheduleTestDataGenerator {
    public static Schedule generateSchedule() {
        return Schedule.builder()
                .id(1L)
                .scheduleName("Barista April")
                .build();
    }
}
