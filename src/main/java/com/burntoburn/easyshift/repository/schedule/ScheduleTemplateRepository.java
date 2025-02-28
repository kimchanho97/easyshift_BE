package com.burntoburn.easyshift.repository.schedule;

import com.burntoburn.easyshift.entity.templates.ScheduleTemplate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleTemplateRepository extends JpaRepository<ScheduleTemplate, Long> {
   List<ScheduleTemplate> findAllByStoreId(Long storeId);

   @Query("select st from ScheduleTemplate st LEFT join fetch st.shiftTemplates.shiftTemplateList where st.id = :id")
   Optional<ScheduleTemplate> findByIdWithShiftTemplates(@Param("id") Long id);
}
