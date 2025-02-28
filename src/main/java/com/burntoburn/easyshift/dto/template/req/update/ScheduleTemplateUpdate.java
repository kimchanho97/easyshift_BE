package com.burntoburn.easyshift.dto.template.req.update;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScheduleTemplateUpdate {
    private String scheduleTemplateName;
    private List<ShiftTemplateUpdate> shiftTemplates;
}
