package com.burntoburn.easyshift.repository.schedule;

import com.burntoburn.easyshift.entity.schedule.Schedule;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    List<Schedule> findByStoreId(Long storeId);

    // 특정 매장의 특정 스케줄 조회 (storeId 검증 포함)
    Optional<Schedule> findByIdAndStoreId(Long scheduleId, Long storeId);

    @Query("SELECT s FROM Schedule s LEFT JOIN FETCH s.shifts.shiftList WHERE s.id = :scheduleId")
    Optional<Schedule> findByIdWithShifts(@Param("scheduleId") Long scheduleId);

}
