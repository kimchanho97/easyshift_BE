package com.burntoburn.easyshift.store;

import com.burntoburn.easyshift.config.jwt.TokenProvider;
import com.burntoburn.easyshift.entity.store.Store;
import com.burntoburn.easyshift.entity.store.UserStore;
import com.burntoburn.easyshift.repository.store.StoreRepository;
import com.burntoburn.easyshift.repository.store.UserStoreRepository;
import com.burntoburn.easyshift.repository.user.UserRepository;
import com.burntoburn.easyshift.service.StoreService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class StoresGetByUserIdTest {

    private StoreRepository storeRepository;
    private UserRepository userRepository;
    private UserStoreRepository userStoreRepository;
    private StoreService storeService;
    private TokenProvider tokenProvider;

    @BeforeEach
    void setUp(){
        // Repository 의존성 모킹
        storeRepository = mock(StoreRepository.class);
        userStoreRepository = mock(UserStoreRepository.class);
        storeService = new StoreService(storeRepository, userRepository, userStoreRepository, tokenProvider);
    }

    @Test
    void testGetStoreNamesByUserId_Success() {
        // given
        Long userId = 1L;
        // 더미 매장 생성
        Store store1 = Store.builder()
                .storeName("Store A")
                .storeCode(UUID.randomUUID())
                .build();
        Store store2 = Store.builder()
                .storeName("Store B")
                .storeCode(UUID.randomUUID())
                .build();
        // 더미 유저 생성 (User 엔티티가 없으므로 mock 사용)
        var user = mock(com.burntoburn.easyshift.entity.user.User.class);
        // UserStore 객체 생성 (Lombok Builder 활용)
        UserStore userStore1 = UserStore.builder()
                .user(user)
                .store(store1)
                .build();
        UserStore userStore2 = UserStore.builder()
                .user(user)
                .store(store2)
                .build();
        List<UserStore> userStores = List.of(userStore1, userStore2);

        // when: Repository가 해당 사용자에 대해 2개의 UserStore 정보를 반환하도록 모킹
        when(userStoreRepository.findAllByUserId(userId)).thenReturn(userStores);

        // then
        List<String> storeNames = storeService.getStoreNamesByUserId(userId);
        assertNotNull(storeNames);
        assertEquals(2, storeNames.size());
        assertTrue(storeNames.contains("Store A"));
        assertTrue(storeNames.contains("Store B"));
    }

    @Test
    void testGetStoreNamesByUserId_Failure() {
        // given
        Long userId = 1L;
        // 해당 사용자의 UserStore 정보가 없는 경우
        when(userStoreRepository.findAllByUserId(userId)).thenReturn(List.of());

        // when & then: 예외가 발생하는지 검증
        Exception exception = assertThrows(RuntimeException.class, () -> {
            storeService.getStoreNamesByUserId(userId);
        });
        String expectedMessage = "해당 사용자와 연결된 매장이 없습니다.";
        assertTrue(exception.getMessage().contains(expectedMessage));
    }


}
