package com.burntoburn.easyshift.controller;

import com.burntoburn.easyshift.dto.user.UserInfoRequest;
import com.burntoburn.easyshift.entity.user.CustomUserDetails;
import com.burntoburn.easyshift.entity.user.User;
import com.burntoburn.easyshift.repository.user.UserRepository;
import com.burntoburn.easyshift.service.user.UserService;
import com.burntoburn.easyshift.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
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
    private final UserRepository userRepository;

    @PostMapping("/info")
    public ResponseEntity<User> signup(@RequestBody UserInfoRequest request) throws Exception{
        User newUser = userService.update(request);
        return ResponseEntity.status(HttpStatus.OK).body(newUser);
    }


    @GetMapping("/profile")
    public ResponseEntity<UserDetails> getUserProfile() {
        CustomUserDetails userDetails = SecurityUtil.getCurrentUser();
        System.out.println("User ID: " + userDetails.getUserId() + ", Email: " + userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.OK).body(userDetails);
    }

//    @GetMapping("/logout")
//    public String logout(HttpServletRequest request, HttpServletResponse response) {
//        new SecurityContextLogoutHandler().logout(request, response, SecurityContextHolder.getContext().getAuthentication());
//        return "redirect:/login"; // frontend 경로로 리다렉트 수정?
//    }
}
