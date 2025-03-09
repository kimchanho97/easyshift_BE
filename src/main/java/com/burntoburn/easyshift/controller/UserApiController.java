package com.burntoburn.easyshift.controller;

import com.burntoburn.easyshift.dto.user.UserInfoRequest;
import com.burntoburn.easyshift.entity.user.CustomUserDetails;
import com.burntoburn.easyshift.entity.user.User;
import com.burntoburn.easyshift.service.user.UserService;
import com.burntoburn.easyshift.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserApiController {

    private final UserService userService;

    @PostMapping("/info")
    public ResponseEntity<User> signup(@RequestBody UserInfoRequest request) throws Exception{
        User newUser = userService.update(request);
        return ResponseEntity.status(HttpStatus.OK).body(newUser);
    }


    @GetMapping("/profile")
    public ResponseEntity<User> getUserProfile() {
        CustomUserDetails userDetails = SecurityUtil.getCurrentUser();
        System.out.println("User ID: " + userDetails.getUserId() + ", Email: " + userDetails.getUsername());
        User user = userService.findById(userDetails.getUserId());
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

//    @GetMapping("/logout")
//    public String logout(HttpServletRequest request, HttpServletResponse response) {
//        new SecurityContextLogoutHandler().logout(request, response, SecurityContextHolder.getContext().getAuthentication());
//        return "redirect:/login"; // frontend 경로로 리다렉트 수정?
//    }
}
