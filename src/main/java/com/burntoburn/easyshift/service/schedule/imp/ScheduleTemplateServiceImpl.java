package com.burntoburn.easyshift.service.schedule.imp;

import com.burntoburn.easyshift.entity.schedule.ScheduleTemplate;
import com.burntoburn.easyshift.repository.schedule.ScheduleTemplateRepository;
import com.burntoburn.easyshift.service.schedule.ScheduleTemplateService;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScheduleTemplateServiceImpl implements ScheduleTemplateService{
    private final ScheduleTemplateRepository scheduleTemplateRepository;


    @Override
    public ScheduleTemplate createScheduleTemplate(ScheduleTemplate scheduleTemplate) {
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

    @Override
    public ScheduleTemplate updateScheduleTemplate(Long id, ScheduleTemplate scheduleTemplate) {
        ScheduleTemplate existingTemplate = getScheduleTemplateOne(id);

        existingTemplate = ScheduleTemplate.builder()
                .id(existingTemplate.getId()) // 기존 ID 유지
                .scheduleTemplateName(scheduleTemplate.getScheduleTemplateName())
                .build();

        return scheduleTemplateRepository.save(existingTemplate);
    }

    @Override
    public void deleteScheduleTemplate(Long id) {
        getScheduleTemplateOne(id);
        scheduleTemplateRepository.deleteById(id);
    }
}
