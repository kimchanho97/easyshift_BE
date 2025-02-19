package com.burntoburn.easyshift.store;

import com.burntoburn.easyshift.config.jwt.TokenProvider;
import com.burntoburn.easyshift.entity.store.Store;
import com.burntoburn.easyshift.entity.store.UserStore;
import com.burntoburn.easyshift.entity.user.User;
import com.burntoburn.easyshift.repository.store.StoreRepository;
import com.burntoburn.easyshift.repository.store.UserStoreRepository;
import com.burntoburn.easyshift.repository.user.UserRepository;
import com.burntoburn.easyshift.service.StoreService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class StoreUserConnectTest {


    @Mock
    private StoreRepository storeRepository;

    @Mock
    private UserStoreRepository userStoreRepository;

    @Mock
    private TokenProvider tokenProvider;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private StoreService storeService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Happy Path 테스트
     * 사용자 토큰에서 ID가 2로 추출되고, 매장 ID 3이 존재하며,
     * 아직 연결되어 있지 않다면 연결 후 사용자와 연결된 매장 목록(예: "Store 3")을 반환해야 합니다.
     */
    @Test
    void testLinkStoreToUser_HappyPath() {
        // given
        String token = "dummyToken";
        Long storeId = 3L;
        Long userId = 2L;

        // 매장 엔티티 (테스트용)
        Store store = Store.builder()
                // 실제 코드에서는 id가 자동 생성되지만 테스트에서는 setter나 builder로 설정한다고 가정
                .storeName("Store 3")
                .build();
        // (Optional) 테스트용으로 id 설정이 가능하다면 아래와 같이 설정할 수 있음
        // store.setId(storeId);

        // TokenProvider가 토큰에서 사용자 ID를 올바르게 추출하도록 모킹
        when(tokenProvider.getUserIdFromToken(token)).thenReturn(userId);

        // storeRepository가 storeId로 매장을 반환하도록 설정
        when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));

        // 아직 해당 사용자와 매장 간 연관관계가 없다고 가정
        when(userStoreRepository.existsByUserIdAndStoreId(userId, storeId)).thenReturn(false);

        // UserRepository에서 프록시 객체 반환 (실제 DB 조회 없이 연관관계 설정용)
        User user = User.builder().id(userId).build();
        when(userRepository.getReferenceById(userId)).thenReturn(user);

        // 새로 생성된 UserStore를 저장할 때, 전달한 객체를 그대로 반환하도록 설정
        when(userStoreRepository.save(any(UserStore.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // 최종적으로 해당 사용자와 연결된 매장 목록을 반환하도록 설정
        UserStore userStore = UserStore.builder()
                .store(store)
                .build();
        List<UserStore> userStoreList = new ArrayList<>();
        userStoreList.add(userStore);
        when(userStoreRepository.findAllByUserId(userId)).thenReturn(userStoreList);

        // when
        List<String> result = storeService.linkStoreToUser(token, storeId);

        // then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains("Store 3"));

        // 연결 생성이 한 번만 호출되었는지 검증
        verify(userStoreRepository, times(1)).save(any(UserStore.class));
    }

    /**
     * 실패 케이스 테스트: 존재하지 않는 매장 ID를 전달하면 RuntimeException이 발생해야 합니다.
     */
    @Test
    void testLinkStoreToUser_StoreNotFound() {
        // given
        String token = "dummyToken";
        Long storeId = 99L;  // 존재하지 않는 매장 ID
        Long userId = 2L;

        when(tokenProvider.getUserIdFromToken(token)).thenReturn(userId);
        // storeRepository에서 매장을 찾지 못하도록 설정
        when(storeRepository.findById(storeId)).thenReturn(Optional.empty());

        // when & then
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                storeService.linkStoreToUser(token, storeId)
        );
        assertTrue(exception.getMessage().contains("해당 매장을 찾을 수 없습니다"));
    }

    @Test
    void testLinkMultipleStoresToUser() {
        // given
        String token = "dummyToken";
        Long userId = 2L;
        when(tokenProvider.getUserIdFromToken(token)).thenReturn(userId);

        // 테스트용 매장 엔티티 생성
        Store store3 = Store.builder().storeName("Store 3").build();
        Store store4 = Store.builder().storeName("Store 4").build();
        Store store5 = Store.builder().storeName("Store 5").build();

        // storeRepository가 각각의 storeId에 대해 올바른 매장을 반환하도록 설정
        when(storeRepository.findById(3L)).thenReturn(Optional.of(store3));
        when(storeRepository.findById(4L)).thenReturn(Optional.of(store4));
        when(storeRepository.findById(5L)).thenReturn(Optional.of(store5));

        // 내부 저장소 역할을 하는 리스트 (상태 유지)
        List<UserStore> userStoreState = new ArrayList<>();

        // userStoreRepository.findAllByUserId는 내부 리스트의 복사본을 반환
        when(userStoreRepository.findAllByUserId(userId)).thenAnswer(invocation -> new ArrayList<>(userStoreState));

        // existsByUserIdAndStoreId는 내부 리스트에 동일한 매장이 있는지 확인 (storeName으로 판별)
        when(userStoreRepository.existsByUserIdAndStoreId(eq(userId), anyLong()))
                .thenAnswer(invocation -> {
                    Long storeIdArg = invocation.getArgument(1, Long.class);
                    final String expectedName;  // final 또는 effectively final로 선언
                    if (storeIdArg.equals(3L)) {
                        expectedName = "Store 3";
                    } else if (storeIdArg.equals(4L)) {
                        expectedName = "Store 4";
                    } else if (storeIdArg.equals(5L)) {
                        expectedName = "Store 5";
                    } else {
                        expectedName = "";
                    }
                    return userStoreState.stream()
                            .anyMatch(us -> us.getStore().getStoreName().equals(expectedName));
                });

        // userRepository.getReferenceById는 프록시 User 객체 반환
        User user = User.builder().id(userId).build();
        when(userRepository.getReferenceById(userId)).thenReturn(user);

        // userStoreRepository.save는 전달받은 UserStore를 내부 리스트에 추가
        when(userStoreRepository.save(any(UserStore.class))).thenAnswer(invocation -> {
            UserStore us = invocation.getArgument(0);
            userStoreState.add(us);
            return us;
        });

        // when - 순차적으로 매장 3, 4, 5와 연관관계 설정 호출
        List<String> result1 = storeService.linkStoreToUser(token, 3L);
        List<String> result2 = storeService.linkStoreToUser(token, 4L);
        List<String> result3 = storeService.linkStoreToUser(token, 5L);

        // then - 최종적으로 사용자와 연결된 매장 목록이 모두 반환되어야 함.
        List<String> finalResult = storeService.linkStoreToUser(token, 3L); // idempotency 확인
        assertEquals(3, finalResult.size());
        assertTrue(finalResult.contains("Store 3"));
        assertTrue(finalResult.contains("Store 4"));
        assertTrue(finalResult.contains("Store 5"));
    }
}
