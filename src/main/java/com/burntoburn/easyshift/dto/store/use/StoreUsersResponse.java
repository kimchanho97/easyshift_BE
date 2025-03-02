package com.burntoburn.easyshift.dto.store.use;

import com.burntoburn.easyshift.dto.user.UserDTO;
import com.burntoburn.easyshift.entity.store.Store;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class StoreUsersResponse {
    private UUID storeCode;
    private Long storeId;
    private String storeName;
    private String description;
    private List<UserDTO> users;

    public static StoreUsersResponse fromEntity(Store store, List<UserDTO> users) {

        return new StoreUsersResponse(
                store.getStoreCode(),
                store.getId(),
                store.getStoreName(),
                store.getDescription(),
                users
        );
    }
}
