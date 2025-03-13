package com.burntoburn.easyshift.entity.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {

    GUEST("GUEST"),
    WORKER("USER"),
    ADMINISTRATOR("ADMIN");

    private final String key;

    public static Role fromString(String key) {
        for (Role role : Role.values()) {
            if (role.getKey().equals(key)) {
                return role;
            }
        }
        return null;
    }
}
