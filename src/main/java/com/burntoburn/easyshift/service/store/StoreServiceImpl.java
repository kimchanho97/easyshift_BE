package com.burntoburn.easyshift.service.store;

import com.burntoburn.easyshift.common.util.DateUtil;
import com.burntoburn.easyshift.dto.store.StoreInfoResponse;
import com.burntoburn.easyshift.entity.schedule.Shift;
import com.burntoburn.easyshift.entity.templates.ScheduleTemplate;
import com.burntoburn.easyshift.exception.store.StoreException;
import com.burntoburn.easyshift.repository.schedule.ScheduleTemplateRepository;
import com.burntoburn.easyshift.repository.schedule.ShiftRepository;
import com.burntoburn.easyshift.repository.store.UserStoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StoreServiceImpl {

    private final UserStoreRepository userStoreRepository;
    private final ScheduleTemplateRepository scheduleTemplateRepository;
    private final ShiftRepository shiftRepository;

    public StoreInfoResponse getStoreInfo(Long storeId, Long userId) {
        // 1. 사용자 매장 접근 권한 확인
        boolean isAuthorizedForStore = userStoreRepository.existsByUserIdAndStoreId(userId, storeId);
        if (!isAuthorizedForStore) {
            throw StoreException.storeAccessDenied();
        }

        // 2. 매장의 스케줄 템플릿 조회(없는 경우 빈 응답 반환)
        List<ScheduleTemplate> scheduleTemplates = scheduleTemplateRepository.findAllWithShiftTemplatesByStoreId(storeId);
        if (scheduleTemplates.isEmpty()) {
            return new StoreInfoResponse(storeId, Collections.emptyList(), null);
        }

        // 3. 가장 처음의 템플릿을 선택
        ScheduleTemplate selectedTemplate = scheduleTemplates.getFirst();

        // 4. 조회 기간 설정 (이번 주)
        LocalDate today = LocalDate.now();
        LocalDate startDate = DateUtil.getStartOfWeek(today);
        LocalDate endDate = DateUtil.getEndOfWeek(startDate);

        // 5. 선택된 템플릿에 해당하는 Shift 조회
        List<Shift> shifts = shiftRepository.findAllByScheduleTemplateIdAndDateBetween(
                storeId, selectedTemplate.getId(), startDate, endDate
        );

        return StoreInfoResponse.fromEntity(storeId, scheduleTemplates, selectedTemplate, shifts);
    }
}
