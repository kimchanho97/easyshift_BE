package com.burntoburn.easyshift.service.templates.imp;

import com.burntoburn.easyshift.dto.template.req.ScheduleTemplateRequest;
import com.burntoburn.easyshift.dto.template.req.update.ScheduleTemplateUpdate;
import com.burntoburn.easyshift.dto.template.res.AllScheduleTemplateResponse;
import com.burntoburn.easyshift.entity.templates.ScheduleTemplate;
import com.burntoburn.easyshift.entity.templates.ShiftTemplate;
import com.burntoburn.easyshift.entity.store.Store;
import com.burntoburn.easyshift.entity.templates.collection.ShiftTemplates;
import com.burntoburn.easyshift.repository.schedule.ScheduleTemplateRepository;
import com.burntoburn.easyshift.repository.store.StoreRepository;
import com.burntoburn.easyshift.service.templates.ScheduleTemplateFactory;
import com.burntoburn.easyshift.service.templates.ScheduleTemplateService;
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
    private final ScheduleTemplateFactory scheduleTemplateFactory;


    @Override
    @Transactional
    public ScheduleTemplate createScheduleTemplate(Long storeId, ScheduleTemplateRequest request) {
        // Store 조회
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new NoSuchElementException("Store not found with id: " + storeId));

        // ScheduleTemplate 생성
        ScheduleTemplate scheduleTemplate = scheduleTemplateFactory.createScheduleTemplate(store, request);



        return scheduleTemplateRepository.save(scheduleTemplate);
    }

    @Override
    public ScheduleTemplate getScheduleTemplateOne(Long id) {
        return scheduleTemplateRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("ScheduleTemplate not found with id: " + id));
    }

    @Transactional(readOnly = true)
    @Override
    public AllScheduleTemplateResponse getAllScheduleTemplatesByStore(Long storeId) {
        List<ScheduleTemplate> scheduleTemplateList = scheduleTemplateRepository.findAllByStoreId(storeId);

        return AllScheduleTemplateResponse
                .fromEntityList(scheduleTemplateList);
    }

    @Transactional
    @Override
    public ScheduleTemplate updateScheduleTemplate(Long storeId, Long scheduleTemplateId, ScheduleTemplateUpdate request) {
        // Store 조회
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new NoSuchElementException("Store not found with id: " + storeId));

        // 기존 ScheduleTemplate 조회
        // getScheduleTemplateOne()로도 가능
        ScheduleTemplate existingTemplate = scheduleTemplateRepository.findById(scheduleTemplateId)
                .orElseThrow(() -> new NoSuchElementException("ScheduleTemplate not found with id: " + scheduleTemplateId));

        // 수정 요청의 ShiftTemplateUpdate DTO를 엔티티 객체로 변환
        List<ShiftTemplate> updatedShifts = request.getShiftTemplates()
                .stream()
                .map(shiftReq -> ShiftTemplate.builder()
                        .id(shiftReq.getShiftTemplateId())
                        .shiftTemplateName(shiftReq.getShiftName())
                        .startTime(shiftReq.getStartTime())
                        .endTime(shiftReq.getEndTime())
                        .build())
                .collect(Collectors.toList());

        // 일급 컬렉션 내부에서 기존 ShiftTemplate들을 찾아 필드만 업데이트하는 로직 호출
        existingTemplate.getShiftTemplates().update(updatedShifts);

        // 더티 체킹 또는 명시적 save()를 통해 변경사항 반영
        return scheduleTemplateRepository.save(existingTemplate);
    }

    @Override
    @Transactional(readOnly = true)
    public ScheduleTemplate getShiftTemplateByScheduleTemplateId(Long scheduleTemplateId) {

        return scheduleTemplateRepository.findByIdWithShiftTemplates(scheduleTemplateId)
                .orElseThrow(() -> new NoSuchElementException("not found scheduleTemplateId"));
    }

    @Override
    public void deleteScheduleTemplate(Long id) {
        scheduleTemplateRepository.deleteById(id);
    }
}
