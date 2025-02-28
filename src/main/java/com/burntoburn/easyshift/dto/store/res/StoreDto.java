package com.burntoburn.easyshift.dto.store.res;

import lombok.Getter;

@Getter
public class StoreDto {
    private Long id;
    private String name;

    public StoreDto(Long id, String name){
        this.id = id;
        this.name = name;
    }
}
