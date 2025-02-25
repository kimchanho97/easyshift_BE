package com.burntoburn.easyshift.dto.schedule.req.scheduleCreate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShiftRequest {
    private Long shiftTemplateId;  // ShiftTemplate의 ID
    private int expectedWorkers;   // 해당 Shift에 배정할 인원 수

}
