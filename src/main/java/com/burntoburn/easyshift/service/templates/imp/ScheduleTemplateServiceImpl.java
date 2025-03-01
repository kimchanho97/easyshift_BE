package com.burntoburn.easyshift.service.templates.imp;

import com.burntoburn.easyshift.dto.template.req.ScheduleTemplateRequest;
import com.burntoburn.easyshift.dto.template.res.AllScheduleTemplateResponse;
import com.burntoburn.easyshift.entity.store.Store;
import com.burntoburn.easyshift.entity.templates.ScheduleTemplate;
import com.burntoburn.easyshift.repository.schedule.ScheduleTemplateRepository;
import com.burntoburn.easyshift.repository.store.StoreRepository;
import com.burntoburn.easyshift.service.templates.ScheduleTemplateFactory;
import com.burntoburn.easyshift.service.templates.ScheduleTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

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

    @Override
    public void deleteScheduleTemplate(Long id) {
        scheduleTemplateRepository.deleteById(id);
    }
}
