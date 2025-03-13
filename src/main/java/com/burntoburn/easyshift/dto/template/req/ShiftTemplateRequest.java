package com.burntoburn.easyshift.dto.template.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShiftTemplateRequest {
    private String shiftName;
    private String startTime;
    private String endTime;
}
