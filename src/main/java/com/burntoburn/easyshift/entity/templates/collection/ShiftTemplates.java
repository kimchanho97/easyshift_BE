package com.burntoburn.easyshift.entity.templates.collection;

import com.burntoburn.easyshift.entity.templates.ShiftTemplate;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Embeddable
public class ShiftTemplates {
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "schedule_template_id") // 단방향 연관관계 유지
    private final List<ShiftTemplate> shiftTemplateList = new ArrayList<>();

    // 새로운 ShiftTemplate 목록으로 업데이트
    public void update(List<ShiftTemplate> newShifts) {
        this.shiftTemplateList.clear();
        this.shiftTemplateList.addAll(newShifts);
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

    // Getter 추가 (테스트에서 사용 가능하도록)
    public List<ShiftTemplate> getList() {
        return new ArrayList<>(shiftTemplateList); // 방어적 복사 (원본 리스트 보호)
    }
}
