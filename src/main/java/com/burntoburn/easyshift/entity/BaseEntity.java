package com.burntoburn.easyshift.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class) // 자동으로 날짜 필드 값 설정
public abstract class BaseEntity {
    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt; // 최초 생성 시간

    @LastModifiedDate // 엔티티가 변경될 때 자동으로 업데이트
    @Column(nullable = false)
    private LocalDateTime updatedAt; // 마지막 수정 시간
}
