package com.burntoburn.easyshift;

import com.burntoburn.easyshift.service.schedule.ScheduleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest
public class ScheduleServicePerformanceTest {

    @Autowired
    private ScheduleService scheduleService;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final Long testScheduleId = 1L; // 테스트에 사용할 스케줄 ID

    /**
     * 백업 데이터를 기반으로 DB를 초기 상태로 복원합니다.
     */
    private void initializeDatabase() {
        jdbcTemplate.execute("DELETE FROM leave_request");
        jdbcTemplate.execute("DELETE FROM shift");
        jdbcTemplate.execute("DELETE FROM users");
        jdbcTemplate.execute("DELETE FROM schedule");

        jdbcTemplate.execute("INSERT INTO schedule SELECT * FROM schedule_backup");
        jdbcTemplate.execute("INSERT INTO shift SELECT * FROM shift_backup");
        jdbcTemplate.execute("INSERT INTO users SELECT * FROM users_backup");
        jdbcTemplate.execute("INSERT INTO leave_request SELECT * FROM leave_request_backup");
    }

    @Test
    public void testAutoAssignSchedulePerformance() {
        int iterations = 10;
        long totalExecutionTime = 0L;

        // 워밍업 5회 실행
        for (int i = 0; i < 5; i++) {
            initializeDatabase();
            scheduleService.autoAssignSchedule(testScheduleId);
        }

        for (int i = 0; i < iterations; i++) {
            // 각 반복마다 DB를 초기 상태로 복원
            initializeDatabase();

            // 실제 autoAssignSchedule() 실행 시간만 측정
            long start = System.nanoTime();
            scheduleService.autoAssignSchedule(testScheduleId);
            long end = System.nanoTime();

            long duration = end - start;
            totalExecutionTime += duration;
            System.out.println("Run " + (i + 1) + " execution time: " + (duration / 1_000_000) + " ms");
        }

        double average = totalExecutionTime / (double) iterations;
        System.out.println("Average execution time: " + (average / 1_000_000) + " ms");
    }
}
