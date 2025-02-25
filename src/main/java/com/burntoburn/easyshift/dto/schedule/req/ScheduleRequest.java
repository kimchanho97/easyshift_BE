package com.burntoburn.easyshift.dto.schedule.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScheduleRequest {
    private String scheduleName;
    private String scheduleMonth;
}
