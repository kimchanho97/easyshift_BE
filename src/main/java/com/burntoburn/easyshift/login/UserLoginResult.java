package com.burntoburn.easyshift.login;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserLoginResult {
    private final UserLoginResponse userLoginResponse;
    private final String accessToken;
}

