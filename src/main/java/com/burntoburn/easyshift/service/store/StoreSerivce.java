package com.burntoburn.easyshift.service.store;

import com.burntoburn.easyshift.dto.store.use.*;

import java.util.UUID;

public interface StoreSerivce {
    StoreCreateResponse createStore(StoreCreateRequest request);

    void updateStore(Long storeId, StoreUpdateRequest request);

    void deleteStore(Long storeId);

    UserStoresResponse getUserStores(Long userId);

    StoreUsersResponse getStoreUsers(Long storeId);

    StoreResponse getStoresInfo(UUID storeCode);

    StoreInfoResponse getStoreInfo(Long storeId, Long userId);
}
