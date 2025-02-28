package com.burntoburn.easyshift.entity.templates;

import com.burntoburn.easyshift.dto.template.res.ScheduleTemplateResponse;
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
    @Column(name = "schedule_template_id")
    private Long id;

    @Column(nullable = false)
    private String scheduleTemplateName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    /**
     * -> 이거 값 타입으로 하면 연관관계 매핑 안됩니다.
     * -> 값 타입은 애초에 배열을 가질 수 없어요.
     * -> 그리고 조인도 안 됩니다.
     * 양방향 매핑을 고려하는 방향 또는 ShiftTemplate 으로 조회하는 방법을 고려!
     * 또는 사용하고 싶으면 @Transient를 사용해서 서비스 계층에서만 사용하는 용도로 수정해야 합니다.
     */
    @Embedded
    @Builder.Default
    private ShiftTemplates shiftTemplates = new ShiftTemplates(); // 일급 컬렉션 적용

    // 스케줄 템플릿 수정 메서드
    public void updateScheduleTemplate(String scheduleTemplateName, List<ShiftTemplate> updatedShiftTemplates) {
        this.scheduleTemplateName = scheduleTemplateName;
        this.shiftTemplates.update(updatedShiftTemplates); // ✅ 일급 컬렉션 내부에서 관리
    }

    public ScheduleTemplateResponse toDTO() {
        return ScheduleTemplateResponse.builder()
                .ScheduleTemplateId(this.id)
                .scheduleTemplateName(this.scheduleTemplateName)
                .storeId(this.store.getId()) // ✅ Lazy Loading 문제 방지: ID만 반환
                .shiftTemplates(this.shiftTemplates.getList().stream()
                        .map(ShiftTemplateResponse::fromEntity) // ✅ ShiftTemplate 변환
                        .toList())
                .build();
    }


}
