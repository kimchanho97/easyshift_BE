package com.burntoburn.easyshift.service.schedule.imp;

import com.burntoburn.easyshift.dto.schedule.req.ScheduleTemplateRequest;
import com.burntoburn.easyshift.entity.schedule.ScheduleTemplate;
import com.burntoburn.easyshift.entity.schedule.ShiftTemplate;
import com.burntoburn.easyshift.entity.store.Store;
import com.burntoburn.easyshift.repository.schedule.ScheduleTemplateRepository;
import com.burntoburn.easyshift.repository.store.StoreRepository;
import com.burntoburn.easyshift.service.schedule.ScheduleTemplateService;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ScheduleTemplateServiceImpl implements ScheduleTemplateService {
    private final ScheduleTemplateRepository scheduleTemplateRepository;
    private final StoreRepository storeRepository;

    @Override
    public ScheduleTemplate createScheduleTemplate(Long storeId, ScheduleTemplateRequest request) {
        // Store 조회
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new NoSuchElementException("Store not found with id: " + storeId));

        // ScheduleTemplate 생성
        ScheduleTemplate scheduleTemplate = ScheduleTemplate.builder()
                .scheduleTemplateName(request.getScheduleTemplateName())
                .store(store)
                .build();

        // ShiftTemplate 리스트 추가
        List<ShiftTemplate> shiftTemplates = request.getShiftTemplates().stream()
                .map(shiftTemplateRequest -> ShiftTemplate.builder()
                        .shiftTemplateName(shiftTemplateRequest.getShiftTemplateName())
                        .startTime(shiftTemplateRequest.getStartTime())
                        .endTime(shiftTemplateRequest.getEndTime())
                        .scheduleTemplate(scheduleTemplate) // 연관관계 설정
                        .build())
                .collect(Collectors.toList());

        // ScheduleTemplate에 ShiftTemplates 설정
        scheduleTemplate.getShiftTemplates().addAll(shiftTemplates);

        return scheduleTemplateRepository.save(scheduleTemplate);
    }

    @Override
    public ScheduleTemplate getScheduleTemplateOne(Long id) {
        return scheduleTemplateRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("ScheduleTemplate not found with id: " + id));
    }

    @Override
    public List<ScheduleTemplate> getAllScheduleTemplates() {
        return scheduleTemplateRepository.findAll();
    }

    @Transactional
    @Override
    public ScheduleTemplate updateScheduleTemplate(Long storeId, Long scheduleTemplateId, ScheduleTemplateRequest request) {
        // Store 조회
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new NoSuchElementException("Store not found with id: " + storeId));

        // 기존 ScheduleTemplate 조회
        ScheduleTemplate existingTemplate = getScheduleTemplateOne(scheduleTemplateId);

        // 새로운 ShiftTemplates 리스트 추가
        List<ShiftTemplate> updatedShifts = request.getShiftTemplates().stream()
                .map(shiftRequest -> ShiftTemplate.builder()
                        .shiftTemplateName(shiftRequest.getShiftTemplateName())
                        .startTime(shiftRequest.getStartTime())
                        .endTime(shiftRequest.getEndTime())
                        .scheduleTemplate(existingTemplate) // 연관관계 설정
                        .build())
                .toList();

        // 정보 업데이트
        existingTemplate.updateScheduleTemplate(request.getScheduleTemplateName(), updatedShifts);

        scheduleTemplateRepository.save(existingTemplate); // Mock 환경에서 테스트를 위해서 save() 명시 [더티 체킹으로 없어질 수 있음]

        return existingTemplate;
    }


    @Override
    public void deleteScheduleTemplate(Long id) {
        scheduleTemplateRepository.deleteById(id);
    }
}
