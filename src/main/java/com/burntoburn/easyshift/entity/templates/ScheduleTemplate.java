package com.burntoburn.easyshift.entity.templates;

import com.burntoburn.easyshift.dto.template.res.ScheduleTemplateResponse;
import com.burntoburn.easyshift.dto.template.res.ShiftTemplateResponse;
import com.burntoburn.easyshift.entity.store.Store;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
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

    @OneToMany(mappedBy = "scheduleTemplate", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<ShiftTemplate> shiftTemplates = new ArrayList<>();

    // 엔티티 내부에 DTO 변환 로직이 있으면 안 됩니다!!
    public ScheduleTemplateResponse toDTO() {
        return ScheduleTemplateResponse.builder()
                .ScheduleTemplateId(this.id)
                .scheduleTemplateName(this.scheduleTemplateName)
                .storeId(this.store.getId()) // ✅ Lazy Loading 문제 방지: ID만 반환
                .shiftTemplates(this.shiftTemplates.stream()
                        .map(ShiftTemplateResponse::fromEntity) // ✅ ShiftTemplate 변환
                        .toList())
                .build();
    }

    public void addShiftTemplate(ShiftTemplate shiftTemplate) {
        this.shiftTemplates.add(shiftTemplate);
    }
}
