package com.burntoburn.easyshift.service;

import com.burntoburn.easyshift.dto.StoreCreateRequest;
import com.burntoburn.easyshift.entity.store.Store;
import com.burntoburn.easyshift.entity.store.UserStore;
import com.burntoburn.easyshift.repository.store.StoreRepository;
import com.burntoburn.easyshift.repository.store.UserStoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;
    private final UserStoreRepository userStoreRepository;

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
}
