package com.burntoburn.easyshift.dto.store.use;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StoreUpdateRequest {
    String storeName;
    String description;
}
