package com.burntoburn.easyshift.dto.schedule.req.scheduleCreate;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.YearMonth;
import java.util.List;
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

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM")
    private YearMonth scheduleMonth;
    private Long scheduleTemplateId;
    private List<ShiftRequest> shiftDetails;
}
