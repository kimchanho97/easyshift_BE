package com.burntoburn.easyshift.controller;

import com.burntoburn.easyshift.common.response.ApiResponse;
import com.burntoburn.easyshift.dto.user.UserInfoRequest;
import com.burntoburn.easyshift.dto.user.UserInfoResponse;
import com.burntoburn.easyshift.entity.user.CustomUserDetails;
import com.burntoburn.easyshift.entity.user.User;
import com.burntoburn.easyshift.service.user.TokenService;
import com.burntoburn.easyshift.service.user.UserService;
import com.burntoburn.easyshift.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserApiController {

    private final UserService userService;
    private final TokenService tokenService;

    @PostMapping("/info")
    public ResponseEntity<ApiResponse<Void>> signup(@RequestBody UserInfoRequest request) throws Exception{
        User newUser = userService.update(request);

        //입력 받은 정보로 다시 access token을 생성
        String accessToken = tokenService.createNewAccessToken(newUser);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);

        return ResponseEntity.ok().headers(headers).body(ApiResponse.success());
    }


    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<User>> getUserProfile() {
        CustomUserDetails userDetails = SecurityUtil.getCurrentUser();
        User user = userService.findById(userDetails.getUserId());

        return ResponseEntity.ok(ApiResponse.success(user));
    }

//    @GetMapping("/logout")
//    public String logout(HttpServletRequest request, HttpServletResponse response) {
//        new SecurityContextLogoutHandler().logout(request, response, SecurityContextHolder.getContext().getAuthentication());
//        return "redirect:/login"; // frontend 경로로 리다렉트 수정?
//    }
}
