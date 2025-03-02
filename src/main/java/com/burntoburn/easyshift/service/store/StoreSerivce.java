package com.burntoburn.easyshift.service.store;

import com.burntoburn.easyshift.dto.store.use.*;

public interface StoreSerivce {
    StoreCreateResponse createStore(StoreCreateRequest request);

    void updateStore(Long storeId, StoreUpdateRequest request);

    void deleteStore(Long storeId);

    UserStoresResponse getUserStores(Long userId);

    StoreInfoResponse getStoreInfo(Long storeId, Long userId);
}
