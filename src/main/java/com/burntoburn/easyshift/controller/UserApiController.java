package com.burntoburn.easyshift.controller;

import com.burntoburn.easyshift.dto.user.AddUserRequest;
import com.burntoburn.easyshift.entity.user.User;
import com.burntoburn.easyshift.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserApiController {

    private final UserService userService;

    @PostMapping("/info")
    public ResponseEntity<User> signup(@RequestBody AddUserRequest request) throws Exception{
        User newUser = userService.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }
}
