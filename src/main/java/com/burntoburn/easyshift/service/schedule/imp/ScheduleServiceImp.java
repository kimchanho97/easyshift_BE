package com.burntoburn.easyshift.service.schedule.imp;

import com.burntoburn.easyshift.dto.schedule.req.ScheduleRequest;
import com.burntoburn.easyshift.entity.schedule.Schedule;
import com.burntoburn.easyshift.entity.store.Store;
import com.burntoburn.easyshift.entity.templates.ScheduleTemplate;
import com.burntoburn.easyshift.repository.schedule.ScheduleRepository;
import com.burntoburn.easyshift.repository.schedule.ScheduleTemplateRepository;
import com.burntoburn.easyshift.repository.store.StoreRepository;
import com.burntoburn.easyshift.service.schedule.ScheduleFactory;
import com.burntoburn.easyshift.service.schedule.ScheduleService;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImp implements ScheduleService {
    private final ScheduleFactory scheduleFactory;
    private final ScheduleTemplateRepository scheduleTemplateRepository;
    private final ScheduleRepository scheduleRepository;
    private final StoreRepository storeRepository;

    /**
     * 스케줄 생성
     */
    @Transactional
    @Override
    public Schedule createSchedule(Long storeId, Long scheduleTemplateId, ScheduleRequest request) {
        // Store 확인
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new NoSuchElementException("Store not found"));

        // scheduleTemplate 확인
        ScheduleTemplate scheduleTemplate = scheduleTemplateRepository.findById(scheduleTemplateId)
                .orElseThrow(() -> new NoSuchElementException("ScheduleTemplate not found"));

        // 스케줄 생성 (ScheduleFactory 활용)
        Schedule schedule = scheduleFactory.createSchedule(store, scheduleTemplate, request);

        // 스케줄 저장 및 반환
        return scheduleRepository.save(schedule);
    }

    /**
     * 스케줄 삭제
     */
    @Transactional
    @Override
    public void deleteSchedule(Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new NoSuchElementException("Schedule not found"));
        scheduleRepository.delete(schedule);
    }

    /**
     * 스케줄 수정
     */
    @Transactional
    @Override
    public Schedule updateSchedule(Long scheduleId, ScheduleRequest request) {
        // 기존 스케줄 조회
        Schedule existingSchedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new NoSuchElementException("Schedule not found"));

        // ScheduleFactory를 사용하여 업데이트 적용
        Schedule updatedSchedule = scheduleFactory.updateSchedule(existingSchedule, request);

        // 변경 감지를 통해 자동 반영
        return scheduleRepository.save(updatedSchedule);
    }

    /**
     * 매장의 모든 스케줄 조회
     */
    @Override
    public List<Schedule> getSchedulesByStore(Long storeId) {
        return scheduleRepository.findByStoreId(storeId);
    }
}
