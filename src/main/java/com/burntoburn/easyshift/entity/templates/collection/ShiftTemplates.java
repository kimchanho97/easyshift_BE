package com.burntoburn.easyshift.entity.templates.collection;

import com.burntoburn.easyshift.entity.templates.ShiftTemplate;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Embeddable
public class ShiftTemplates {
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "schedule_template_id") // 단방향 연관관계 유지
    private List<ShiftTemplate> shiftTemplateList = new ArrayList<>();

    public void update(List<ShiftTemplate> newShifts) {
        for (ShiftTemplate newShift : newShifts) {
            if (newShift.getId() != null) {
                ShiftTemplate existing = findById(newShift.getId());
                if (existing != null) {
                    // 기존 엔티티의 필드만 업데이트 (id는 그대로 유지)
                    existing.updateShiftTemplate(newShift);
                } else {
                    // 만약 해당 id가 기존 컬렉션에 없으면, 신규 항목으로 추가할 수도 있음.
                    shiftTemplateList.add(newShift);
                }
            } else {
                // id가 없는 경우 신규 항목으로 간주하여 추가
                shiftTemplateList.add(newShift);
            }
        }
    }

    // 새로운 ShiftTemplate 추가
    public void add(ShiftTemplate shiftTemplate) {
        this.shiftTemplateList.add(shiftTemplate);
    }

    // ShiftTemplate 여러 개 추가
    public void addAll(List<ShiftTemplate> shiftTemplates) {
        this.shiftTemplateList.addAll(shiftTemplates);
    }

    // ShiftTemplate 전체 삭제
    public void clear() {
        this.shiftTemplateList.clear();
    }

    // 특정 ID로 ShiftTemplate 찾기
    public ShiftTemplate findById(Long shiftTemplateId) {
        return shiftTemplateList.stream()
                .filter(st -> st.getId().equals(shiftTemplateId))
                .findFirst()
                .orElse(null);
    }

    // Getter 추가 (테스트에서 사용 가능하도록)
    public List<ShiftTemplate> getList() {
        return new ArrayList<>(shiftTemplateList); // 방어적 복사 (원본 리스트 보호)
    }
}
