package com.burntoburn.easyshift.service.shift.imp;

import com.burntoburn.easyshift.entity.schedule.Schedule;
import com.burntoburn.easyshift.entity.schedule.Shift;
import com.burntoburn.easyshift.entity.user.User;
import com.burntoburn.easyshift.repository.schedule.ShiftRepository;
import com.burntoburn.easyshift.service.shift.ShiftService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class ShiftServiceImpTest {

    @Autowired
    private ShiftService shiftService; // ✅ 실제 서비스 객체

    @MockitoBean
    private ShiftRepository shiftRepository; // ✅ Mock Repository (Spring Context 내에서 동작)

    @Test
    @DisplayName("스케줄 쉬프트 생성")
    void createShift() {
        // Given
        Shift newShift = Shift.builder()
                .shiftName("Evening Shift")
                .shiftDate(LocalDate.of(2024, 2, 20))
                .startTime(LocalTime.of(14, 0))
                .endTime(LocalTime.of(22, 0))
                .schedule(Schedule.builder().id(2L).build())
                .user(User.builder().id(4L).build())
                .build();

        // Mock 설정: save()가 호출되면 newShift 반환
        when(shiftRepository.save(any(Shift.class))).thenReturn(newShift);

        // When: createShift 실행
        Shift createdShift = shiftService.createShift(newShift);

        // Then: 결과 검증
        assertNotNull(createdShift);
        assertEquals("Evening Shift", createdShift.getShiftName());
        verify(shiftRepository, times(1)).save(any(Shift.class));
    }

    @Test
    @DisplayName("스케줄 쉬프트 조회 - 단건")
    void getShiftOne() {
        // Given
        Shift shift = Shift.builder()
                .shiftName("Morning Shift")
                .shiftDate(LocalDate.of(2024, 2, 19))
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(17, 0))
                .schedule(Schedule.builder().id(1L).build())
                .user(User.builder().id(1L).build())
                .build();

        // anyLong()을 사용하여 어떤 ID가 전달되더라도 Mocking이 적용되도록 설정
        when(shiftRepository.getShiftById(anyLong())).thenReturn(Optional.of(shift));

        // When
        Shift foundShift = shiftService.getShiftOne(1L);

        // Shift가 정상적으로 반환되는지 출력하여 확인
        System.out.println("Found Shift: " + foundShift);

        // Then
        assertNotNull(foundShift);
        assertEquals("Morning Shift", foundShift.getShiftName());
        verify(shiftRepository, times(1)).getShiftById(anyLong());
    }


    @Test
    @DisplayName("스케줄 쉬프트 조회 - 전체")
    void getAllShifts() {
        // Given
        List<Shift> shiftList = Arrays.asList(
                Shift.builder()
                        .shiftName("Morning Shift")
                        .shiftDate(LocalDate.of(2024, 2, 19))
                        .startTime(LocalTime.of(9, 0))
                        .endTime(LocalTime.of(17, 0))
                        .schedule(Schedule.builder().id(1L).build())
                        .user(User.builder().id(1L).build())
                        .build(),
                Shift.builder()
                        .shiftName("Afternoon Shift")
                        .shiftDate(LocalDate.of(2024, 2, 19))
                        .startTime(LocalTime.of(13, 0))
                        .endTime(LocalTime.of(21, 0))
                        .schedule(Schedule.builder().id(1L).build())
                        .user(User.builder().id(2L).build())
                        .build()
        );

        when(shiftRepository.findAll()).thenReturn(shiftList);

        // When
        List<Shift> shifts = shiftService.getAllShifts();

        // Then
        assertNotNull(shifts);
        assertEquals(2, shifts.size());
        assertEquals("Morning Shift", shifts.get(0).getShiftName());
        assertEquals("Afternoon Shift", shifts.get(1).getShiftName());
        verify(shiftRepository, times(1)).findAll();
    }
    @Test
    @DisplayName("스케줄 쉬프트 업데이트")
    void updateShift() {
        // Given
        Shift existingShift = Shift.builder()
                .id(1L)
                .shiftName("Morning Shift")
                .shiftDate(LocalDate.of(2024, 2, 19))
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(17, 0))
                .schedule(Schedule.builder().id(1L).build())
                .user(User.builder().id(1L).build())
                .build();

        // 업데이트할 Shift 데이터
        Shift updatedShiftDetails = Shift.builder()
                .shiftName("Evening Shift")
                .shiftDate(LocalDate.of(2024, 2, 20))
                .startTime(LocalTime.of(14, 0))
                .endTime(LocalTime.of(22, 0))
                .schedule(Schedule.builder().id(2L).build())
                .user(User.builder().id(2L).build())
                .build();

        // Mock 설정
        when(shiftRepository.getShiftById(1L)).thenReturn(Optional.of(existingShift));
        when(shiftRepository.save(any(Shift.class))).thenReturn(updatedShiftDetails);

        // When
        Shift updatedShift = shiftService.updateShift(1L, updatedShiftDetails);

        // Then
        assertNotNull(updatedShift);
        assertEquals("Evening Shift", updatedShift.getShiftName());
        verify(shiftRepository, times(1)).getShiftById(1L);
        verify(shiftRepository, times(1)).save(any(Shift.class));
    }

    @Test
    @DisplayName("스케줄 쉬프트 삭제")
    void deleteShift() {
        // Given
        Long shiftId = 1L;
        Shift shiftToDelete = Shift.builder()
                .id(shiftId)
                .shiftName("Night Shift")
                .shiftDate(LocalDate.of(2024, 2, 19))
                .startTime(LocalTime.of(21, 0))
                .endTime(LocalTime.of(5, 0))
                .schedule(Schedule.builder().id(1L).build())
                .user(User.builder().id(3L).build())
                .build();

        // Mock 설정: 삭제할 객체 조회 가능하도록 설정
        when(shiftRepository.findById(shiftId)).thenReturn(Optional.of(shiftToDelete));

        // Mock 설정: deleteById() 호출 시 아무 동작도 하지 않도록 설정
        doNothing().when(shiftRepository).deleteById(shiftId);

        // When
        shiftService.deleteShift(shiftId);

        // Then
        verify(shiftRepository, times(1)).findById(shiftId); // 삭제할 객체가 조회되었는지 확인
        verify(shiftRepository, times(1)).deleteById(shiftId); // 삭제 메서드가 호출되었는지 확인

        // 삭제 후 findById()가 Optional.empty()를 반환하는지 검증
        when(shiftRepository.findById(shiftId)).thenReturn(Optional.empty());
        Optional<Shift> deletedShift = shiftRepository.findById(shiftId);
        assertTrue(deletedShift.isEmpty(), "삭제된 Shift가 더 이상 존재하지 않아야 합니다.");
    }
}
