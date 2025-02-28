package com.burntoburn.easyshift.repository.schedule;

import com.burntoburn.easyshift.entity.templates.ScheduleTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleTemplateRepository extends JpaRepository<ScheduleTemplate, Long> {
    List<ScheduleTemplate> findAllByStoreId(Long storeId);

    @Query("""
            SELECT st 
            FROM ScheduleTemplate st
            JOIN FETCH st.shiftTemplates shiftTemplates 
            WHERE st.store.id = :storeId
            """)
    List<ScheduleTemplate> findAllWithShiftTemplatesByStoreId(@Param("storeId") Long storeId);

}
