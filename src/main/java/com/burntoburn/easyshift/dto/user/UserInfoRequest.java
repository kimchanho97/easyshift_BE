package com.burntoburn.easyshift.dto.user;

import com.burntoburn.easyshift.entity.user.Role;
import com.burntoburn.easyshift.entity.user.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserInfoRequest {

    private String email;
    private String name;
    private String phoneNumber;
    private String role;
    private String avatarUrl;

    public User toEntity(){

        return User.builder()
                .email(email)
                .name(name)
                .phoneNumber(phoneNumber)
                .role(Enum.valueOf(Role.class, role))
                .avatarUrl(avatarUrl)
                .build();
    }

}
