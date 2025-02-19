package com.burntoburn.easyshift.repository.schedule;

import com.burntoburn.easyshift.entity.schedule.ScheduleTemplate;
import com.burntoburn.easyshift.entity.schedule.ShiftTemplate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleTemplateRepository extends JpaRepository<ScheduleTemplate, Long> {
    Optional<ScheduleTemplate> getScheduleTemplateById(Long id);
}
