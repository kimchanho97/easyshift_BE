package com.burntoburn.easyshift.entity.templates;

import com.burntoburn.easyshift.dto.template.res.StoreTemplates;
import com.burntoburn.easyshift.dto.template.res.ShiftTemplateResponse;
import com.burntoburn.easyshift.entity.store.Store;
import com.burntoburn.easyshift.entity.templates.collection.ShiftTemplates;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Entity
@Table(name = "schedule_template") // 테이블명 명시
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 기본 생성자 (protected)
@AllArgsConstructor // 모든 필드를 포함한 생성자 자동 생성
@Builder // Lombok Builder 적용
public class ScheduleTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE) // ID는 자동 생성되므로 Builder에서 제외
    private Long id;

    @Column(nullable = false)
    private String scheduleTemplateName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Embedded
    @Builder.Default
    private ShiftTemplates shiftTemplates = new ShiftTemplates(); // 일급 컬렉션 적용

    // 스케줄 템플릿 수정 메서드
    public void updateScheduleTemplate(String scheduleTemplateName, List<ShiftTemplate> updatedShiftTemplates) {
        this.scheduleTemplateName = scheduleTemplateName;
        this.shiftTemplates.update(updatedShiftTemplates); // ✅ 일급 컬렉션 내부에서 관리
    }

    public StoreTemplates toDTO() {
        return StoreTemplates.builder()
                .ScheduleTemplateId(this.id)
                .scheduleTemplateName(this.scheduleTemplateName)
                .storeId(this.store.getId()) // ✅ Lazy Loading 문제 방지: ID만 반환
                .shiftTemplates(this.shiftTemplates.getList().stream()
                        .map(ShiftTemplateResponse::fromEntity) // ✅ ShiftTemplate 변환
                        .toList())
                .build();
    }


}
