package com.burntoburn.easyshift.dto.user;

import com.burntoburn.easyshift.entity.user.Role;
import com.burntoburn.easyshift.entity.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UserInfoResponse {
    private String name;
    private String email;
    private String phoneNumber;
    private String avatarUrl;
    private Role role;

    public UserInfoResponse(User user) {
        this.name = user.getName();
        this.email = user.getEmail();
        this.phoneNumber = user.getPhoneNumber();
        this.role = user.getRole();
        this.avatarUrl = user.getAvatarUrl();
    }
}
