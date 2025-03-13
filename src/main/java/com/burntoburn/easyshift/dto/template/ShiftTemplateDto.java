package com.burntoburn.easyshift.dto.template;

import com.burntoburn.easyshift.entity.templates.ShiftTemplate;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ShiftTemplateDto {
    private Long shiftTemplateId;
    private String shiftTemplateName;

    @JsonFormat(pattern = "HH:mm")
    private String startTime;

    @JsonFormat(pattern = "HH:mm")
    private String endTime;

    public static ShiftTemplateDto fromEntity(ShiftTemplate shiftTemplate) {
        return new ShiftTemplateDto(
                shiftTemplate.getId(),
                shiftTemplate.getShiftTemplateName(),
                shiftTemplate.getStartTime().toString(),
                shiftTemplate.getEndTime().toString()
        );
    }
}
