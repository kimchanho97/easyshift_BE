package com.burntoburn.easyshift.repository.schedule;

import com.burntoburn.easyshift.entity.schedule.Shift;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ShiftRepository extends JpaRepository<Shift, Long> {
    Optional<Shift> getShiftById(Long id);

    // Shift 엔티티와 연결된 User 정보를 패치 조인
    @Query("SELECT s FROM Shift s LEFT JOIN FETCH s.user WHERE s.id = :id")
    Optional<Shift> findByIdWithUser(@Param("id") Long id);
}
