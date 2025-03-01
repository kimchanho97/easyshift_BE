package com.burntoburn.easyshift.dto.template.res;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScheduleTemplateResponse {
    private Long ScheduleTemplateId;
    private String scheduleTemplateName;
    private Long storeId;
    private List<ShiftTemplateResponse> shiftTemplates;
}
