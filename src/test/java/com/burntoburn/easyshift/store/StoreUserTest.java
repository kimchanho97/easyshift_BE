package com.burntoburn.easyshift.store;

import com.burntoburn.easyshift.entity.store.Store;
import com.burntoburn.easyshift.entity.store.UserStore;
import com.burntoburn.easyshift.entity.user.Role;
import com.burntoburn.easyshift.entity.user.User;
import com.burntoburn.easyshift.repository.store.StoreRepository;
import com.burntoburn.easyshift.repository.store.UserStoreRepository;
import com.burntoburn.easyshift.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class StoreUserTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserStoreRepository userStoreRepository;

    private Store testStore;

    @BeforeEach
    public void setup() {
        // 데이터베이스 초기화 (테스트에 따라 @Transactional이나 DB 클리어 로직을 추가할 수 있음)
        userStoreRepository.deleteAll();
        storeRepository.deleteAll();
        userRepository.deleteAll();

        // 테스트용 매장 생성
        testStore = Store.builder()
                .storeName("매장 A")
                .build();
        testStore = storeRepository.save(testStore);
    }

    @Test
    public void testGetStoreUsers_whenUsersExist() throws Exception {
        // 사용자 2명 생성
        User user1 = User.builder()
                .email("worker@example.com")
                .name("Worker User")
                .phoneNumber("010-1234-5678")
                .role(Role.WORKER)
                .avatarUrl("https://example.com/avatar5.png")
                .build();
        user1 = userRepository.save(user1);

        User user2 = User.builder()
                .email("admin@example.com")
                .name("Admin User")
                .phoneNumber("010-9876-5432")
                .role(Role.ADMINISTRATOR)
                .avatarUrl("https://example.com/avatar6.png")
                .build();
        user2 = userRepository.save(user2);

        // 매장과 사용자 연결(UserStore 생성)
        UserStore us1 = UserStore.builder()
                .store(testStore)
                .user(user1)
                .build();
        UserStore us2 = UserStore.builder()
                .store(testStore)
                .user(user2)
                .build();
        userStoreRepository.saveAll(List.of(us1, us2));

        // GET /api/stores/{storeId}/users 호출
        mockMvc.perform(get("/api/stores/{storeId}/users", testStore.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                // 응답 JSON 구조 검증
                .andExpect(jsonPath("$.storeId").value(testStore.getId()))
                .andExpect(jsonPath("$.storeName").value(testStore.getStoreName()))
                // 두 명의 사용자가 반환되어야 함
                .andExpect(jsonPath("$.users.length()").value(2))
                // 첫 번째 사용자 검증 (순서는 보장되지 않으므로 email 등으로 검사)
                .andExpect(jsonPath("$.users[?(@.email=='worker@example.com')].phoneNumber").exists())
                .andExpect(jsonPath("$.users[?(@.email=='admin@example.com')].avatarUrl").exists());
    }

    @Test
    public void testGetStoreUsers_whenNoUsers() throws Exception {
        // 매장에 연결된 사용자가 없는 경우
        mockMvc.perform(get("/api/stores/{storeId}/users", testStore.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                // users 배열는 빈 리스트여야 함
                .andExpect(jsonPath("$.storeId").value(testStore.getId()))
                .andExpect(jsonPath("$.storeName").value(testStore.getStoreName()))
                .andExpect(jsonPath("$.users").isEmpty());
    }
}
