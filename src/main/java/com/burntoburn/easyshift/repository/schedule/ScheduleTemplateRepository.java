package com.burntoburn.easyshift.repository.schedule;

import com.burntoburn.easyshift.entity.templates.ScheduleTemplate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleTemplateRepository extends JpaRepository<ScheduleTemplate, Long> {
    Optional<List<ScheduleTemplate>> findAllByStoreId(Long storeId);
}
