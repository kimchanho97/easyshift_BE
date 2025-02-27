package com.burntoburn.easyshift.store;

import com.burntoburn.easyshift.entity.store.Store;
import com.burntoburn.easyshift.entity.store.UserStore;
import com.burntoburn.easyshift.entity.user.Role;
import com.burntoburn.easyshift.entity.user.User;
import com.burntoburn.easyshift.repository.store.StoreRepository;
import com.burntoburn.easyshift.repository.store.UserStoreRepository;
import com.burntoburn.easyshift.repository.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class StoreDeleteTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private UserStoreRepository userStoreRepository;

    @Autowired
    private UserRepository userRepository;

    private Store store;
    private User user;

    @BeforeEach
    public void setup() {
        // Given: 매장 및 유저 데이터 저장
        store = storeRepository.save(
                Store.builder()
                        .storeName("Test Store")
                        .storeCode(UUID.randomUUID())
                        .build()
        );

        user = userRepository.save(
                User.builder()
                        .email("test@example.com")
                        .name("홍길동")
                        .phoneNumber("010-1234-5678")
                        .role(Role.WORKER)  // 예제 기준으로 WORKER 역할
                        .build()
        );

        // Given: 매장과 유저 간 연관 관계 설정 (UserStore 생성)
        userStoreRepository.save(
                UserStore.builder()
                        .user(user)
                        .store(store)
                        .build()
        );
    }

    @Test
    public void testDeleteStoreWithUserStore() throws Exception {
        // When: 매장 삭제 요청 수행
        mockMvc.perform(delete("/api/stores/" + store.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string("매장이 성공적으로 삭제되었습니다."));

        // Then: 매장이 DB에서 삭제되었는지 확인
        assertFalse(storeRepository.findById(store.getId()).isPresent());

        // Then: 해당 매장과 연결된 UserStore도 삭제되었는지 확인
        assertTrue(userStoreRepository.findAllByStoreId(store.getId()).isEmpty());
    }
}
