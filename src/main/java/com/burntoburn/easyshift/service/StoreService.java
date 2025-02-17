package com.burntoburn.easyshift.service;

import com.burntoburn.easyshift.dto.StoreCreateRequest;
import com.burntoburn.easyshift.entity.store.Store;
import com.burntoburn.easyshift.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;

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

}
