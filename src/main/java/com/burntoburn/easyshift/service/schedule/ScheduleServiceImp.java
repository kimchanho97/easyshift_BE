package com.burntoburn.easyshift.service.schedule;

import com.burntoburn.easyshift.dto.schedule.req.ScheduleUpload;
import com.burntoburn.easyshift.dto.schedule.res.ScheduleDetailDTO;
import com.burntoburn.easyshift.dto.schedule.res.ScheduleInfoResponse;
import com.burntoburn.easyshift.dto.schedule.res.WorkerScheduleResponse;
import com.burntoburn.easyshift.dto.store.SelectedScheduleTemplateDto;
import com.burntoburn.easyshift.entity.leave.ApprovalStatus;
import com.burntoburn.easyshift.entity.leave.LeaveRequest;
import com.burntoburn.easyshift.entity.schedule.Schedule;
import com.burntoburn.easyshift.entity.schedule.Shift;
import com.burntoburn.easyshift.entity.store.Store;
import com.burntoburn.easyshift.entity.templates.ScheduleTemplate;
import com.burntoburn.easyshift.entity.templates.ShiftTemplate;
import com.burntoburn.easyshift.exception.schedule.ScheduleException;
import com.burntoburn.easyshift.exception.shift.ShiftException;
import com.burntoburn.easyshift.exception.store.StoreException;
import com.burntoburn.easyshift.exception.template.TemplateException;
import com.burntoburn.easyshift.repository.leave.LeaveRequestRepository;
import com.burntoburn.easyshift.repository.schedule.ScheduleRepository;
import com.burntoburn.easyshift.repository.schedule.ScheduleTemplateRepository;
import com.burntoburn.easyshift.repository.schedule.ShiftRepository;
import com.burntoburn.easyshift.repository.store.StoreRepository;
import com.burntoburn.easyshift.scheduler.AutoAssignmentScheduler;
import com.burntoburn.easyshift.scheduler.ShiftAssignmentData;
import com.burntoburn.easyshift.scheduler.ShiftAssignmentProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScheduleServiceImp implements ScheduleService {
    private final ScheduleFactory scheduleFactory;
    private final ScheduleTemplateRepository scheduleTemplateRepository;
    private final ScheduleRepository scheduleRepository;
    private final StoreRepository storeRepository;
    private final ShiftRepository shiftRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final ShiftAssignmentProcessor shiftAssignmentProcessor;
    private final AutoAssignmentScheduler autoAssignmentScheduler;

    // 스케줄 삭제
    @Transactional
    @Override
    public void deleteSchedule(Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(ScheduleException::scheduleNotFound);
        scheduleRepository.delete(schedule);
    }

    // 매장 스케줄 목록 조회
    @Override
    public ScheduleInfoResponse getSchedulesByStore(Long storeId, Pageable pageable) {
        Page<Schedule> schedulePage = scheduleRepository.findByStoreIdOrderByCreatedAtDesc(storeId, pageable);

        if (schedulePage.isEmpty()) {
            if (!scheduleRepository.existsByStoreId(storeId)) {
                throw ScheduleException.scheduleNotFound();  // 스토어에 대한 스케줄이 없음
            }
            throw ScheduleException.schedulePageNotFound(); // 요청한 페이지에 데이터 없음
        }

        return ScheduleInfoResponse.formEntity(schedulePage, schedulePage.isLast());
    }

    // worker의 스케줄 조회
    @Override
    public WorkerScheduleResponse getSchedulesByWorker(Long storeId, Long userId, String date) {
        YearMonth scheduleMonth = YearMonth.parse(date, DateTimeFormatter.ofPattern("yyyy-MM"));

        List<Schedule> workerSchedules = scheduleRepository.findWorkerSchedules(storeId, scheduleMonth, userId);
        if (workerSchedules.isEmpty()) {
            throw ScheduleException.scheduleNotFound();
        }

        Store store = workerSchedules.getFirst().getStore(); // 첫 번째 스케줄에서 store 가져오기

        Map<Long, String> scheduleIdToTemplateNameMap = getScheduleIdToTemplateNameMap(workerSchedules);

        return WorkerScheduleResponse.fromEntity(store, workerSchedules, scheduleIdToTemplateNameMap);
    }

    // 스케줄 조회(일주일치)
    @Override
    public SelectedScheduleTemplateDto getWeeklySchedule(Long scheduleTemplateId, String date) {
        LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDate monday = localDate.with(DayOfWeek.MONDAY);
        LocalDate endDate = monday.plusDays(6);

        List<Schedule> schedules = scheduleRepository.findSchedulesWithTemplate(scheduleTemplateId);


        List<Long> scheduleIds = schedules.stream().map(Schedule::getId).toList();

        List<Shift> shifts = shiftRepository.findShiftsByScheduleIdWithUser(scheduleIds, monday, endDate);


        ScheduleTemplate scheduleTemplate = scheduleTemplateRepository
                .findScheduleTemplateWithShiftsById(schedules.get(0).getScheduleTemplateId())
                .orElseThrow(TemplateException::scheduleTemplateNotFound);

        return SelectedScheduleTemplateDto.fromEntity(scheduleTemplate, shifts);
    }

    // 스케줄 조회(all)
    @Override
    public ScheduleDetailDTO getAllSchedules(Long scheduleId) {
        Optional<Schedule> optionalSchedule = scheduleRepository.findScheduleWithShifts(scheduleId);

        if (optionalSchedule.isEmpty()) {
            return ScheduleDetailDTO.emptyResponse(scheduleId);
        }

        Schedule schedule = optionalSchedule.get();

        ScheduleTemplate scheduleTemplate = scheduleTemplateRepository
                .findScheduleTemplateWithShiftsById(schedule.getScheduleTemplateId())
                .orElseThrow(TemplateException::scheduleTemplateNotFound);

        List<ShiftTemplate> shiftTemplates = scheduleTemplate.getShiftTemplates();
        List<Shift> shifts = schedule.getShifts();

        return ScheduleDetailDTO.fromEntity(scheduleId, schedule.getScheduleName(), shiftTemplates, shifts);
    }

    // 스케줄 생성
    @Transactional
    @Override
    public void createSchedule(ScheduleUpload upload) {
        Store store = storeRepository.findById(upload.getStoreId())
                .orElseThrow(StoreException::storeNotFound);

        ScheduleTemplate scheduleTemplate = scheduleTemplateRepository.findById(upload.getScheduleTemplateId())
                .orElseThrow(TemplateException::scheduleTemplateNotFound);

        Schedule schedule = scheduleFactory.createSchedule(store, scheduleTemplate, upload);
        scheduleRepository.save(schedule);
    }

    @Override
    @Transactional
    public void autoAssignSchedule(Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(ScheduleException::scheduleNotFound);

        List<Shift> shifts = shiftRepository.findAllBySchedule(schedule);
        if (shifts == null || shifts.isEmpty()) {
            throw ShiftException.shiftNotFound();
        }

        List<LeaveRequest> leaveRequests = leaveRequestRepository.findAllByScheduleAndApprovalStatus(schedule, ApprovalStatus.APPROVED);

        ShiftAssignmentData assignmentData = shiftAssignmentProcessor.processData(shifts, leaveRequests);
        if (assignmentData.users().size() < assignmentData.maxRequired()) {
            throw ScheduleException.insufficientUsersForAssignment();
        }

        autoAssignmentScheduler.assignShifts(assignmentData);
    }

    private Map<Long, String> getScheduleIdToTemplateNameMap(List<Schedule> workerSchedules) {
        // 1. scheduleId -> scheduleTemplateId 매핑
        Map<Long, Long> scheduleToTemplateMap = workerSchedules.stream()
                .collect(Collectors.toMap(Schedule::getId, Schedule::getScheduleTemplateId));

        // 2. 중복 제거된 scheduleTemplateId 리스트 추출
        List<Long> uniqueTemplateIds = new ArrayList<>(new HashSet<>(scheduleToTemplateMap.values()));

        // 3. scheduleTemplateId -> scheduleTemplateName 매핑 (한 번의 DB 조회)
        Map<Long, String> templateIdToNameMap = scheduleTemplateRepository.findByIdIn(uniqueTemplateIds).stream()
                .collect(Collectors.toMap(ScheduleTemplate::getId, ScheduleTemplate::getScheduleTemplateName));

        // 4. scheduleId -> scheduleTemplateName 매핑 생성 후 반환
        return workerSchedules.stream()
                .collect(Collectors.toMap(
                        Schedule::getId,
                        schedule -> templateIdToNameMap.get(schedule.getScheduleTemplateId())
                ));
    }
}
