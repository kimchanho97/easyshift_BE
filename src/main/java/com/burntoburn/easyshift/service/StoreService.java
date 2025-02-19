package com.burntoburn.easyshift.service;

import com.burntoburn.easyshift.config.jwt.TokenProvider;
import com.burntoburn.easyshift.dto.store.req.StoreCreateRequest;
import com.burntoburn.easyshift.entity.store.Store;
import com.burntoburn.easyshift.entity.store.UserStore;
import com.burntoburn.easyshift.entity.user.User;
import com.burntoburn.easyshift.repository.store.StoreRepository;
import com.burntoburn.easyshift.repository.store.UserStoreRepository;
import com.burntoburn.easyshift.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;
    private final UserRepository userRepository;
    private final UserStoreRepository userStoreRepository;
    private final TokenProvider tokenProvider;

    public Store getStoreById(Long storeId) {
        return storeRepository.findById(storeId)
                .orElseThrow(() -> new RuntimeException("해당 매장을 찾을 수 없습니다. id: " + storeId));
    }

    public Store createStore(StoreCreateRequest request) {
        Store store = Store.builder()
                .storeName(request.getStoreName())
                .storeCode(UUID.randomUUID())
                .build();

        return storeRepository.save(store);
    }

    public List<String> getStoreNamesByUserId(Long userId) {
        List<UserStore> userStores = userStoreRepository.findAllByUserId(userId);
        if (userStores.isEmpty()) {
            throw new RuntimeException("해당 사용자와 연결된 매장이 없습니다.");
        }

        return userStores.stream()
                .map(userStore -> userStore.getStore().getStoreName())
                .collect(Collectors.toList());
    }

    public List<String> linkStoreToUser(String token, Long storeId){
        Long userId = tokenProvider.getUserIdFromToken(token);

        Store store = getStoreById(storeId);

        boolean exists = userStoreRepository.existsByUserIdAndStoreId(userId, storeId);
        if(!exists){
            // 실제 DB 조회 없이 프록시 객체를 이용하여 연관관계 설정
            User user = userRepository.getReferenceById(userId);

            UserStore userStore = UserStore.builder()
                    .user(user)
                    .store(store)
                    .build();

            userStoreRepository.save(userStore);
        }

        return userStoreRepository.findAllByUserId(userId)
                .stream()
                .map(us -> us.getStore().getStoreName())
                .collect(Collectors.toList());
    }
}
