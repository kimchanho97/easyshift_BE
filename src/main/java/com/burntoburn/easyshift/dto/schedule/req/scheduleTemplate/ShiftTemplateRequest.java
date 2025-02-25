package com.burntoburn.easyshift.dto.schedule.req.scheduleTemplate;

import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShiftTemplateRequest {
    private String shiftTemplateName;
    private LocalTime startTime;
    private LocalTime endTime;
}
