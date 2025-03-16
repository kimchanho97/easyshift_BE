package com.burntoburn.easyshift.scheduler;

import com.nimbusds.oauth2.sdk.util.CollectionUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ShiftAssignmentJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    /**
     * 배정된 유저 정보를 Shift 테이블에 배치 업데이트 수행
     * (User ID, Shift ID) 쌍
     */
    public void batchUpdateShiftAssignments(List<Pair<Long, Long>> assignments) {
        if (CollectionUtils.isEmpty(assignments)) {
            return;
        }

        String sql = "UPDATE shift SET user_id = ? WHERE shift_id = ?";
        int batchSize = 100;  // ✅ 배치 크기 조정

        jdbcTemplate.batchUpdate(sql, assignments, batchSize, (ps, assignment) -> {
            ps.setLong(1, assignment.getFirst());  // user_id
            ps.setLong(2, assignment.getSecond()); // shift_id
        });
    }
}
