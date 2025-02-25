package com.burntoburn.easyshift.dto.schedule.req.schedulUpdate;

import java.time.LocalDate;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShiftRequestDto {
    private String shiftName;
    private LocalDate shiftDate;
    private LocalTime startTime;
    private LocalTime endTime;
}
