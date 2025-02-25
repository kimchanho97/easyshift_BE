package com.burntoburn.easyshift.repository.leave;

import com.burntoburn.easyshift.entity.leave.LeaveRequest;
import java.time.YearMonth;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {

    // 특정 userId 의 모든 휴뮤일 조회
    @Query("SELECT lr FROM LeaveRequest lr WHERE lr.user.id = :userId")
    List<LeaveRequest> findAllByUserId(Long userId);

    @Query("SELECT lr FROM LeaveRequest lr " +
            "JOIN lr.schedule s " +
            "WHERE s.scheduleMonth = :scheduleMonth")
    List<LeaveRequest> findAllByScheduleMonth(YearMonth scheduleMonth);
}
