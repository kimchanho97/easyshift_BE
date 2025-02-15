package com.burntoburn.easyshift.entity.store;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Entity
@Table(name = "store") // 테이블명 명시
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder // Lombok Builder 적용
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE) // id는 Builder에서 설정 불가
    private Long id;

    @Column(nullable = false)
    private String storeName;

    @Column(unique = true, nullable = false)
    private UUID storeCode;
}
