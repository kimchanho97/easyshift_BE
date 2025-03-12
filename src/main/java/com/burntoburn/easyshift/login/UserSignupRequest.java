package com.burntoburn.easyshift.login;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserSignupRequest {

    @NotBlank(message = "이름은 필수 입력 값입니다.")
    private String name;
    @NotBlank(message = "이메일은 필수 입력 값입니다.")
    private String phoneNumber;
    @NotBlank(message = "role은 필수 입력 값입니다.")
    private String role;
}
