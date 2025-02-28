package com.burntoburn.easyshift.dto.schedule.res.ScheduleData;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StoreScheduleResponse {
    private Long scheduleId;
    private String scheduleName;
    private String scheduleMonth;
    private String status;
}
