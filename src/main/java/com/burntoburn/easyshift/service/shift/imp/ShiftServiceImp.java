package com.burntoburn.easyshift.service.shift.imp;

import com.burntoburn.easyshift.entity.schedule.Shift;
import com.burntoburn.easyshift.repository.schedule.ShiftRepository;
import com.burntoburn.easyshift.service.shift.ShiftService;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShiftServiceImp implements ShiftService {
    private final ShiftRepository shiftRepository;

    @Override
    public Shift createShift(Shift shift) {
        return shiftRepository.save(shift);
    }

    public Shift getShiftOne(Long id) {
        return shiftRepository.getShiftById(id)
                .orElseThrow(() -> new NoSuchElementException("Shift not found with id: " + id));
    }

    @Override
    public List<Shift> getAllShifts() {
        return shiftRepository.findAll();
    }

    @Override
    public Shift updateShift(Long id, Shift shiftDetails) {
        // 기존 Shift 찾기 (없으면 예외 발생)
        Shift existingShift = getShiftOne(id);

        // 새로운 값으로 업데이트
        existingShift = Shift.builder()
                .id(existingShift.getId()) // 기존 ID 유지
                .shiftName(shiftDetails.getShiftName()) // 새로운 Shift 이름
                .shiftDate(shiftDetails.getShiftDate()) // 새로운 날짜
                .startTime(shiftDetails.getStartTime()) // 새로운 시작 시간
                .endTime(shiftDetails.getEndTime()) // 새로운 종료 시간
                .schedule(shiftDetails.getSchedule()) // 새로운 Schedule
                .user(shiftDetails.getUser()) // 새로운 User
                .build();

        // 저장 후 반환
        return shiftRepository.save(existingShift);
    }


    @Override
    public void deleteShift(Long id) {
        // 먼저 존재하는 Shift인지 확인
        shiftRepository.findById(id).orElseThrow(() ->
                new NoSuchElementException("Shift not found with id: " + id)
        );
        // 삭제 수행
        shiftRepository.deleteById(id);
    }

}
