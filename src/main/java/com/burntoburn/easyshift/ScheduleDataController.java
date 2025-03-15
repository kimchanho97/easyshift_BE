package com.burntoburn.easyshift;

import com.burntoburn.easyshift.dto.schedule.res.ScheduleDetailDTO;
import com.burntoburn.easyshift.entity.leave.ApprovalStatus;
import com.burntoburn.easyshift.entity.leave.LeaveRequest;
import com.burntoburn.easyshift.entity.schedule.Schedule;
import com.burntoburn.easyshift.entity.schedule.Shift;
import com.burntoburn.easyshift.entity.templates.ScheduleTemplate;
import com.burntoburn.easyshift.entity.templates.ShiftTemplate;
import com.burntoburn.easyshift.entity.user.Role;
import com.burntoburn.easyshift.entity.user.User;
import com.burntoburn.easyshift.scheduler.AutoAssignmentScheduler;
import com.burntoburn.easyshift.scheduler.ShiftAssignmentData;
import com.burntoburn.easyshift.scheduler.ShiftAssignmentProcessor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
public class ScheduleDataController {

    @GetMapping("/data/schedule")
    public ScheduleDetailDTO getScheduleData() {
        // 1. 스케줄 객체 생성
        Schedule schedule = Schedule.builder()
                .id(1L)
                .scheduleName("Barista April")
                .build();

        // 2. 사용자 객체 생성 (빌더 패턴이 있다고 가정)
        // 사용자 객체 생성 (빌더 패턴 사용)
        List<User> users = new ArrayList<>();
        users.add(User.builder()
                .id(1L)
                .name("Ko Juhyong")
                .email("dury.ko@gmail.com")
                .phoneNumber("010-1234-5678")
                .avatarUrl("https://avatars.githubusercontent.com/u/38830620?s=70&v=4")
                .role(Role.WORKER)
                .build());
        users.add(User.builder()
                .id(2L)
                .name("Kim Chanho")
                .email("nh0903@pusan.ac.kr")
                .phoneNumber("010-9876-5432")
                .avatarUrl("https://avatars.githubusercontent.com/u/104095041?v=4")
                .role(Role.WORKER)
                .build());
        users.add(User.builder()
                .id(3L)
                .name("Son Taein")
                .email("handtaein@gmail.com")
                .phoneNumber("010-9876-5432")
                .avatarUrl("https://avatars.githubusercontent.com/u/111020615?s=70&v=4")
                .role(Role.WORKER)
                .build());
        users.add(User.builder()
                .id(4L)
                .name("Yang Soyeon")
                .email("jayy_19@ewhain.net")
                .phoneNumber("010-9876-5432")
                .avatarUrl("https://avatars.githubusercontent.com/u/125855539?s=70&v=4")
                .role(Role.WORKER)
                .build());
        users.add(User.builder()
                .id(5L)
                .name("Lee Youngjae")
                .email("zerojae175@gmail.com")
                .phoneNumber("010-9876-5432")
                .avatarUrl("https://avatars.githubusercontent.com/u/112526208?s=70&v=4")
                .role(Role.WORKER)
                .build());
        users.add(User.builder()
                .id(6L)
                .name("Cho Yunhae")
                .email("susu12356@gmail.com")
                .phoneNumber("010-9876-5432")
                .avatarUrl("https://avatars.githubusercontent.com/u/91577577?s=70&v=4")
                .role(Role.WORKER)
                .build());
        users.add(User.builder()
                .id(7L)
                .name("Jo Jangho")
                .email("26dev@naver.com")
                .phoneNumber("010-9876-5432")
                .avatarUrl("https://avatars.githubusercontent.com/u/109591135?s=70&v=4")
                .role(Role.WORKER)
                .build());


        // 3. LeaveRequest 객체 생성 (각 사용자별 승인된 휴무 신청)
        List<LeaveRequest> leaveRequests = new ArrayList<>();
        // 사용자 1
        leaveRequests.add(LeaveRequest.builder()
                .date(LocalDate.parse("2025-04-02"))
                .approvalStatus(ApprovalStatus.APPROVED)
                .user(users.get(0))
                .schedule(schedule)
                .build());
        leaveRequests.add(LeaveRequest.builder()
                .date(LocalDate.parse("2025-04-11"))
                .approvalStatus(ApprovalStatus.APPROVED)
                .user(users.get(0))
                .schedule(schedule)
                .build());
        leaveRequests.add(LeaveRequest.builder()
                .date(LocalDate.parse("2025-04-24"))
                .approvalStatus(ApprovalStatus.APPROVED)
                .user(users.get(0))
                .schedule(schedule)
                .build());

        // 사용자 2
        leaveRequests.add(LeaveRequest.builder()
                .date(LocalDate.parse("2025-04-04"))
                .approvalStatus(ApprovalStatus.APPROVED)
                .user(users.get(1))
                .schedule(schedule)
                .build());
        leaveRequests.add(LeaveRequest.builder()
                .date(LocalDate.parse("2025-04-13"))
                .approvalStatus(ApprovalStatus.APPROVED)
                .user(users.get(1))
                .schedule(schedule)
                .build());
        leaveRequests.add(LeaveRequest.builder()
                .date(LocalDate.parse("2025-04-25"))
                .approvalStatus(ApprovalStatus.APPROVED)
                .user(users.get(1))
                .schedule(schedule)
                .build());

        // 사용자 3
        leaveRequests.add(LeaveRequest.builder()
                .date(LocalDate.parse("2025-04-06"))
                .approvalStatus(ApprovalStatus.APPROVED)
                .user(users.get(2))
                .schedule(schedule)
                .build());
        leaveRequests.add(LeaveRequest.builder()
                .date(LocalDate.parse("2025-04-15"))
                .approvalStatus(ApprovalStatus.APPROVED)
                .user(users.get(2))
                .schedule(schedule)
                .build());
        leaveRequests.add(LeaveRequest.builder()
                .date(LocalDate.parse("2025-04-26"))
                .approvalStatus(ApprovalStatus.APPROVED)
                .user(users.get(2))
                .schedule(schedule)
                .build());

        // 사용자 4
        leaveRequests.add(LeaveRequest.builder()
                .date(LocalDate.parse("2025-04-08"))
                .approvalStatus(ApprovalStatus.APPROVED)
                .user(users.get(3))
                .schedule(schedule)
                .build());
        leaveRequests.add(LeaveRequest.builder()
                .date(LocalDate.parse("2025-04-17"))
                .approvalStatus(ApprovalStatus.APPROVED)
                .user(users.get(3))
                .schedule(schedule)
                .build());
        leaveRequests.add(LeaveRequest.builder()
                .date(LocalDate.parse("2025-04-27"))
                .approvalStatus(ApprovalStatus.APPROVED)
                .user(users.get(3))
                .schedule(schedule)
                .build());

        // 사용자 5
        leaveRequests.add(LeaveRequest.builder()
                .date(LocalDate.parse("2025-04-10"))
                .approvalStatus(ApprovalStatus.APPROVED)
                .user(users.get(4))
                .schedule(schedule)
                .build());
        leaveRequests.add(LeaveRequest.builder()
                .date(LocalDate.parse("2025-04-19"))
                .approvalStatus(ApprovalStatus.APPROVED)
                .user(users.get(4))
                .schedule(schedule)
                .build());
        leaveRequests.add(LeaveRequest.builder()
                .date(LocalDate.parse("2025-04-28"))
                .approvalStatus(ApprovalStatus.APPROVED)
                .user(users.get(4))
                .schedule(schedule)
                .build());

        // 사용자 6
        leaveRequests.add(LeaveRequest.builder()
                .date(LocalDate.parse("2025-04-12"))
                .approvalStatus(ApprovalStatus.APPROVED)
                .user(users.get(5))
                .schedule(schedule)
                .build());
        leaveRequests.add(LeaveRequest.builder()
                .date(LocalDate.parse("2025-04-21"))
                .approvalStatus(ApprovalStatus.APPROVED)
                .user(users.get(5))
                .schedule(schedule)
                .build());
        leaveRequests.add(LeaveRequest.builder()
                .date(LocalDate.parse("2025-04-29"))
                .approvalStatus(ApprovalStatus.APPROVED)
                .user(users.get(5))
                .schedule(schedule)
                .build());

        // 사용자 7
        leaveRequests.add(LeaveRequest.builder()
                .date(LocalDate.parse("2025-04-14"))
                .approvalStatus(ApprovalStatus.APPROVED)
                .user(users.get(6))
                .schedule(schedule)
                .build());
        leaveRequests.add(LeaveRequest.builder()
                .date(LocalDate.parse("2025-04-23"))
                .approvalStatus(ApprovalStatus.APPROVED)
                .user(users.get(6))
                .schedule(schedule)
                .build());
        leaveRequests.add(LeaveRequest.builder()
                .date(LocalDate.parse("2025-04-30"))
                .approvalStatus(ApprovalStatus.APPROVED)
                .user(users.get(6))
                .schedule(schedule)
                .build());

        // 4. Shift 객체 생성
        // 4월 1일부터 4월 30일까지 각 날짜별로 shiftTemplates에 따른 Shift 생성
        // shiftTemplateId 1 (Open): 1개, 2 (Middle): 2개, 3 (Close): 1개
        List<Shift> shifts = new ArrayList<>();
        LocalDate startDate = LocalDate.of(2025, 4, 1);
        LocalDate endDate = LocalDate.of(2025, 4, 30);
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            // Open shift
            shifts.add(Shift.builder()
                    .shiftName("Open")
                    .shiftDate(date)
                    .startTime(LocalTime.of(7, 0))
                    .endTime(LocalTime.of(12, 0))
                    .schedule(schedule)
                    .shiftTemplateId(1L)
                    .build());

            // Middle shifts (2개)
            shifts.add(Shift.builder()
                    .shiftName("Middle")
                    .shiftDate(date)
                    .startTime(LocalTime.of(12, 0))
                    .endTime(LocalTime.of(17, 0))
                    .schedule(schedule)
                    .shiftTemplateId(2L)
                    .build());
            shifts.add(Shift.builder()
                    .shiftName("Middle")
                    .shiftDate(date)
                    .startTime(LocalTime.of(12, 0))
                    .endTime(LocalTime.of(17, 0))
                    .schedule(schedule)
                    .shiftTemplateId(2L)
                    .build());

            // Close shift
            shifts.add(Shift.builder()
                    .shiftName("Close")
                    .shiftDate(date)
                    .startTime(LocalTime.of(17, 0))
                    .endTime(LocalTime.of(22, 0))
                    .schedule(schedule)
                    .shiftTemplateId(3L)
                    .build());
        }

        // 5. ShiftAssignmentProcessor를 통해 데이터 가공
        ShiftAssignmentProcessor processor = new ShiftAssignmentProcessor();
        ShiftAssignmentData assignmentData = processor.processData(shifts, leaveRequests);

        // 6. 가공된 결과 출력
        System.out.println("ShiftAssignmentData:");
        System.out.println(" - 총 Shift 개수: " + assignmentData.shifts().size());
        System.out.println(" - 배정 대상 사용자: " + assignmentData.users().stream()
                .map(User::getName)
                .collect(Collectors.joining(", ")));
        System.out.println(" - Shift별 최대 필요 인원: " + assignmentData.maxRequired());
        System.out.println("\nUser Leave Dates:");
        assignmentData.userLeaveDates().forEach((user, dates) ->
                System.out.println("   " + user.getName() + " -> " + dates)
        );

        // 7. ShiftAssignmentProcessor를 통해 자동 배정 수행
        AutoAssignmentScheduler autoAssignmentScheduler = new AutoAssignmentScheduler();
        autoAssignmentScheduler.assignShifts(assignmentData);

        // ScheduleTemplate 객체 생성 (예: Barista 템플릿)
        ScheduleTemplate scheduleTemplate = ScheduleTemplate.builder()
                .id(1L)
                .scheduleTemplateName("Barista")
                .build();

        // ShiftTemplate 배열 생성
        List<ShiftTemplate> shiftTemplates = new ArrayList<>();

        // Open shift (shiftTemplateId: 1)
        shiftTemplates.add(ShiftTemplate.builder()
                .id(1L)
                .shiftTemplateName("Open")
                .startTime(LocalTime.parse("07:00"))
                .endTime(LocalTime.parse("12:00"))
                .scheduleTemplate(scheduleTemplate)
                .build());

        // Middle shift (shiftTemplateId: 2)
        // JSON에서 expectedWorkers가 2로 되어 있지만, 해당 정보는 ShiftTemplate 클래스에 없으므로 ShiftTemplate 객체에는 반영하지 않습니다.
        shiftTemplates.add(ShiftTemplate.builder()
                .id(2L)
                .shiftTemplateName("Middle")
                .startTime(LocalTime.parse("12:00"))
                .endTime(LocalTime.parse("17:00"))
                .scheduleTemplate(scheduleTemplate)
                .build());

        // Close shift (shiftTemplateId: 3)
        shiftTemplates.add(ShiftTemplate.builder()
                .id(3L)
                .shiftTemplateName("Close")
                .startTime(LocalTime.parse("17:00"))
                .endTime(LocalTime.parse("22:00"))
                .scheduleTemplate(scheduleTemplate)
                .build());

        Map<User, Set<LocalDate>> userLeaveDates = assignmentData.userLeaveDates();
        int counter = 0;
        for (Shift shift : assignmentData.shifts()) {
            // Shift 기본 정보 출력
            System.out.print("Date: " + shift.getShiftDate());
            System.out.print(", Start Time: " + shift.getStartTime());
            System.out.print(", End Time: " + shift.getEndTime());

            // 할당된 사용자 정보 출력
            if (shift.getUser() != null) {
                System.out.print(", Assigned User: " + shift.getUser().getName());
                Set<LocalDate> leaveDates = userLeaveDates.get(shift.getUser());
                System.out.print(", Leave Dates: " + (leaveDates != null ? leaveDates : "None"));
            } else {
                System.out.print(", No assigned user");
            }
            System.out.println(); // 한 Shift 정보 출력 완료 후 개행

            counter++;
            // 하루에 4개 단위로 출력하므로, 4개 출력 후 추가 개행 처리
            if (counter % 4 == 0) {
                System.out.println(); // 추가 개행: 하루 근무별 구분
            }
        }

        // 8. 배정 결과 출력
        ScheduleDetailDTO scheduleDetailDTO = ScheduleDetailDTO.fromEntity(schedule.getId(), schedule.getScheduleName(), shiftTemplates, shifts);
        return scheduleDetailDTO;
    }
}
