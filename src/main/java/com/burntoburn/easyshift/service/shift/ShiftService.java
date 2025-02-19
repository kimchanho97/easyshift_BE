package com.burntoburn.easyshift.service.shift;

import com.burntoburn.easyshift.entity.schedule.Shift;
import java.util.List;

public interface ShiftService{
    // Create (새로운 Shift 생성)
    Shift createShift(Shift shift);

    // Read (단일 및 전체 조회)
    Shift getShiftOne(Long id);
    List<Shift> getAllShifts();

    // Update (Shift 수정)
    Shift updateShift(Long id, Shift shift);

    // Delete (Shift 삭제)
    void deleteShift(Long id);
}
