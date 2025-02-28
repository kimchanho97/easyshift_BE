package com.burntoburn.easyshift.dto.schedule.res.ScheduleData;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StoreScheduleListResponse {
    private List<StoreScheduleResponse> schedules;

}
