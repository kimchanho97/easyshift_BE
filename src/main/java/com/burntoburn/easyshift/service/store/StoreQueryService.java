package com.burntoburn.easyshift.service.store;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StoreQueryService {


    public void getStoreInfo(Long storeId, Long userId) {

    }
}
