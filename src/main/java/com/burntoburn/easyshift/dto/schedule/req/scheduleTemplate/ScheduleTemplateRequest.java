package com.burntoburn.easyshift.dto.schedule.req.scheduleTemplate;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScheduleTemplateRequest {
    private String scheduleTemplateName;
    private List<ShiftTemplateRequest> shiftTemplates;
}
