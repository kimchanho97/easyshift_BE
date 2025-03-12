package com.burntoburn.easyshift.login;

import com.burntoburn.easyshift.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserServiceImpl userServiceImpl;

    /**
     * 로그인
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserLoginResponse>> login(@RequestBody @Valid UserLoginRequest request) {
        UserLoginResult loginResult = userServiceImpl.login(request.getCode());

        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + loginResult.getAccessToken())
                .body(ApiResponse.success(loginResult.getUserLoginResponse()));
    }

    /**
     * 회원가입
     */
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Void>> signup(@RequestBody @Valid UserSignupRequest request,
                                                    @AuthenticationPrincipal Long userId) {
        userServiceImpl.signup(request, userId);
        return ResponseEntity.ok().body(ApiResponse.success(null));
    }
}
