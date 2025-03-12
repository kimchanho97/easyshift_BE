package com.burntoburn.easyshift.login;

import com.burntoburn.easyshift.entity.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginResponse {

    private Long userId;
    private String email;
    private String name;
    private String avatarUrl;
    private Boolean needsSignUp;

    public static UserLoginResponse fromEntity(User user, Boolean needsSignUp) {
        return new UserLoginResponse(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getAvatarUrl(),
                needsSignUp
        );
    }
}
