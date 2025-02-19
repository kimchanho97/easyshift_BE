package com.burntoburn.easyshift.dto.store.res;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class StoreResponse {

    private Long id;
    private String storeName;
    private UUID storeCode;

}
