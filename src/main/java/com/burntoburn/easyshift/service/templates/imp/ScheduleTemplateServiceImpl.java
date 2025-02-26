package com.burntoburn.easyshift.service.templates.imp;

import com.burntoburn.easyshift.dto.template.req.ScheduleTemplateRequest;
import com.burntoburn.easyshift.entity.templates.ScheduleTemplate;
import com.burntoburn.easyshift.entity.templates.ShiftTemplate;
import com.burntoburn.easyshift.entity.store.Store;
import com.burntoburn.easyshift.repository.schedule.ScheduleTemplateRepository;
import com.burntoburn.easyshift.repository.store.StoreRepository;
import com.burntoburn.easyshift.service.templates.ScheduleTemplateFactory;
import com.burntoburn.easyshift.service.templates.ScheduleTemplateService;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ScheduleTemplateServiceImpl implements ScheduleTemplateService {
    private final ScheduleTemplateRepository scheduleTemplateRepository;
    private final StoreRepository storeRepository;
    private final ScheduleTemplateFactory scheduleTemplateFactory;

    @Override
    public ScheduleTemplate createScheduleTemplate(Long storeId, ScheduleTemplateRequest request) {
        // Store 조회
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new NoSuchElementException("Store not found with id: " + storeId));

        // ScheduleTemplate 생성
        ScheduleTemplate scheduleTemplate = scheduleTemplateFactory.createScheduleTemplate(store,request);

        // ShiftTemplate 리스트 추가 (scheduleTemplate 참조 X)
        List<ShiftTemplate> shiftTemplates = scheduleTemplateFactory.createShiftTemplates(request.getShiftTemplates());

        // ShiftTemplates를 이용하여 추가 (일급 컬렉션 적용)
        scheduleTemplate.getShiftTemplates().update(shiftTemplates);

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

        // 새로운 ShiftTemplates 리스트 추가 (scheduleTemplate 참조 X)
        List<ShiftTemplate> updatedShifts = scheduleTemplateFactory.createShiftTemplates(request.getShiftTemplates());

        // 일급 컬렉션 내부에서 관리
        existingTemplate.updateScheduleTemplate(request.getScheduleTemplateName(), updatedShifts);

        return scheduleTemplateRepository.save(existingTemplate); // Mock 환경에서 save() 호출함 [더티 체킹으로 전환힐 예정]
    }

    @Override
    public void deleteScheduleTemplate(Long id) {
        scheduleTemplateRepository.deleteById(id);
    }
}
