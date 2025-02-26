package com.burntoburn.easyshift.dto.leave.req;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LeaveRequestDto {
    private Long scheduleId;
    private LocalDate date;
}
