package com.burntoburn.easyshift.repository.schedule;

import com.burntoburn.easyshift.entity.schedule.Schedule;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    List<Schedule> findByStoreId(Long storeId);
}
