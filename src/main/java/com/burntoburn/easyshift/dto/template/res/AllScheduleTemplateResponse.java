package com.burntoburn.easyshift.dto.template.res;

import com.burntoburn.easyshift.entity.templates.ScheduleTemplate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AllScheduleTemplateResponse {
    private List<StoreTemplates> storeTemplateRespons;

    // ✅ 리스트 변환을 위한 정적 메서드 추가
    public static AllScheduleTemplateResponse fromEntityList(List<ScheduleTemplate> scheduleTemplates) {
        List<StoreTemplates> responseList = scheduleTemplates.stream()
                .map(ScheduleTemplate::toDTO) // 각 엔티티를 DTO로 변환
                .toList();
        return new AllScheduleTemplateResponse(responseList);
    }
}
