package com.burntoburn.easyshift.repository.leave;

import com.burntoburn.easyshift.entity.leave.LeaveRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {
}
