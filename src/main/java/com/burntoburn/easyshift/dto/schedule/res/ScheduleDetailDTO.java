package com.burntoburn.easyshift.dto.schedule.res;


import com.burntoburn.easyshift.dto.shift.res.ShiftGroupDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class ScheduleDetailDTO {
    private Long scheduleId;
    private String scheduleName;
    private List<ShiftGroupDTO> shifts;
}
