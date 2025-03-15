package test;

import com.burntoburn.easyshift.entity.schedule.Schedule;
import com.burntoburn.easyshift.entity.schedule.Shift;
import com.burntoburn.easyshift.entity.user.User;
import com.burntoburn.easyshift.scheduler.AutoAssignmentScheduler;
import com.burntoburn.easyshift.scheduler.ShiftAssignmentData;
import org.openjdk.jmh.annotations.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)  // 실행 시간 측정 (Mode.Throughput: 초당 실행 횟수)
@State(Scope.Thread)  // 각 벤치마크가 독립적으로 실행되도록 설정
@OutputTimeUnit(TimeUnit.MILLISECONDS)  // 실행 시간 단위: 밀리초
public class AutoAssignmentSchedulerBenchmark {

    private AutoAssignmentScheduler scheduler;
    private ShiftAssignmentData assignmentData;

    @Setup(Level.Iteration)  // 각 테스트 실행 전에 데이터 초기화
    public void setUp() {
        scheduler = new AutoAssignmentScheduler();
        assignmentData = createTestData();
    }

    @Benchmark
    public void testAssignShifts() {
        scheduler.assignShifts(assignmentData);
    }

    private ShiftAssignmentData createTestData() {
        // 하루 근무 개수 설정 (반드시 3의 배수)
        int shiftsPerDay = 30;  // 하루 30개의 근무
        int userCount = 20;  // 유저 수
        int leaveDaysPerUser = 3;  // 각 유저당 3일의 휴무

        Schedule schedule = ScheduleTestDataGenerator.generateSchedule();
        List<Shift> shifts = ShiftTestDataGenerator.generateShifts(schedule, shiftsPerDay);

        Long maxRequired = (long) (shiftsPerDay / 3);
        List<User> users = UserTestDataGenerator.generateUsers(userCount);

        Map<User, Set<LocalDate>> leaveDates = UserTestDataGenerator.generateUserLeaveDates(users, leaveDaysPerUser);

        return new ShiftAssignmentData(shifts, users, leaveDates, maxRequired);
    }
}
