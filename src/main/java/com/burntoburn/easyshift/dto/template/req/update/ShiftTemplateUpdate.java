package com.burntoburn.easyshift.dto.template.req.update;

import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShiftTemplateUpdate {
    private Long shiftTemplateId;
    private String shiftName;
    private LocalTime startTime;
    private LocalTime endTime;
}
