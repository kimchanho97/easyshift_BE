package com.burntoburn.easyshift.controller.schedule;

import com.burntoburn.easyshift.dto.schedule.req.scheduleCreate.ScheduleRequest;
import com.burntoburn.easyshift.dto.schedule.res.ScheduleResponse;
import com.burntoburn.easyshift.dto.schedule.res.ScheduleData.StoreScheduleListResponse;
import com.burntoburn.easyshift.dto.schedule.res.ScheduleWithShifts.ScheduleWithShiftsDto;
import com.burntoburn.easyshift.entity.schedule.Schedule;
import com.burntoburn.easyshift.service.schedule.ScheduleMapper;
import com.burntoburn.easyshift.service.schedule.ScheduleService;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ScheduleController {
    private final ScheduleService scheduleService;
    private final ScheduleMapper scheduleMapper;

    /**
     * 1. 스케줄 생성 API
     * @param storeId 매장 ID (PathVariable)
     * @param request 스케줄 생성 요청 DTO
     * @return 생성된 스케줄 정보
     */
    @PostMapping("/stores/{storeId}/schedule")
    public ResponseEntity<Void> createSchedule(@PathVariable Long storeId,
                                                   @RequestBody ScheduleRequest request) {
        scheduleService.createSchedule(storeId, request);

        return ResponseEntity.noContent().build();
    }

    /**
     * 2. 스케줄 수정 API
     * @param storeId 매장 ID
     * @param scheduleId 스케줄 ID
     * @param request 스케줄 수정 요청 DTO
     * @return 204 No Content 응답
     */
    @PatchMapping("/stores/{storeId}/schedule/{scheduleId}")
    public ResponseEntity<Void> updateSchedule(@PathVariable Long storeId,
                                               @PathVariable Long scheduleId,
                                               @RequestBody ScheduleRequest request) {
        scheduleService.updateSchedule(storeId, scheduleId, request);
        return ResponseEntity.noContent().build();
    }

    /**
     * 3. 특정 Worker의 특정 날짜 스케줄 조회 API
     * @param storeId 매장 ID
     * @param userId 워커 ID
     * @param date 조회할 날짜
     * @return 해당 날짜의 Worker 스케줄 목록
     */
    @GetMapping("/stores/{storeId}/workers/{userId}/schedules")
    public ResponseEntity<List<ScheduleResponse>> getWorkerSchedule(@PathVariable Long storeId,
                                                                    @PathVariable Long userId,
                                                                    @RequestParam LocalDate date) {
        return null;
    }

    /**
     * 4. 특정 매장의 생성된 스케줄 목록 조회 API
     * @param storeId 매장 ID
     * @return 해당 매장의 스케줄 목록
     */
    @GetMapping("/stores/{storeId}/schedules")
    public ResponseEntity<StoreScheduleListResponse> getStoreSchedules(@PathVariable Long storeId) {
        List<Schedule> schedulesByStore = scheduleService.getSchedulesByStore(storeId);
        StoreScheduleListResponse response = scheduleMapper.toListResponse(schedulesByStore);

        return ResponseEntity.ok(response);
    }

    /**
     * 5. 특정 스케줄 조회 API
     * @param scheduleId 스케줄 ID
     * @param date 조회할 날짜 (쿼리 파라미터)
     * @return 해당 날짜의 스케줄 상세 정보
     */
    @GetMapping("/schedules/{scheduleId}")
    public ResponseEntity<ScheduleResponse> getSchedule(@PathVariable Long scheduleId,
                                                                  @RequestParam(required = false) LocalDate date) {

        return null;
    }

    /**
     * 6. 특정 스케줄 조회 API + Shift 포함 조회
     * @param scheduleId 스케줄 ID
     * @return 해당 날짜의 스케줄 상세 정보
     */
    @GetMapping("/schedules/{scheduleId}/shifts")
    public ResponseEntity<ScheduleWithShiftsDto> getScheduleWithShifts(@PathVariable Long scheduleId) {
        ScheduleWithShiftsDto scheduleWithShifts = scheduleMapper.toScheduleWithShifts(
                scheduleService.getScheduleWithShifts(scheduleId));
        return ResponseEntity.ok(scheduleWithShifts);
    }


    /**
     * 7. 스케줄 삭제 API
     * @param scheduleId 삭제할 스케줄 ID
     * @return 204 No Content 응답
     */
    @DeleteMapping("/schedules/{scheduleId}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Long scheduleId) {
        scheduleService.deleteSchedule(scheduleId);
        return ResponseEntity.noContent().build();
    }


}
