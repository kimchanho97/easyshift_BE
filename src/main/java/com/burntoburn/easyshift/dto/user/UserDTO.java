package com.burntoburn.easyshift.dto.user;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserDTO {
    private Long id;
    private String name;
    private String email;
    private String phoneNumber;
    private String avatarUrl;
    private String role;
}
