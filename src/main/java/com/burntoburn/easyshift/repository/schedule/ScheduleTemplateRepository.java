package com.burntoburn.easyshift.repository.schedule;

import com.burntoburn.easyshift.entity.schedule.ScheduleTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleTemplateRepository extends JpaRepository<ScheduleTemplate, Long> {

}
