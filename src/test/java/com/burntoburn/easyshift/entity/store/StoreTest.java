package com.burntoburn.easyshift.entity.store;

import com.burntoburn.easyshift.repository.store.StoreRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class StoreTest {
    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("✅ StoreCode(UUID) 자동 생성 성공")
    void shouldGenerateUUIDWhenStoreIsCreated() {
        // Given
        Store store = Store.builder().storeName("Test Store").build();

        // When
        Store savedStore = storeRepository.save(store);

        // Then
        assertNotNull(savedStore.getStoreCode(), "StoreCode(UUID)는 엔티티 생성 시 자동으로 설정되어야 합니다.");
    }

    @Test
    @Transactional
    @DisplayName("✅ StoreCode(UUID) 의 setter 없기 때문에 변경이 불가")
    void shouldNotUpdateStoreCodeWhenExplicitlyChanged(){

    }
}
