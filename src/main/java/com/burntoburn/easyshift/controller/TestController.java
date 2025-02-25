package com.burntoburn.easyshift.controller;

import com.burntoburn.easyshift.dto.user.AddUserRequest;
import com.burntoburn.easyshift.entity.user.User;
import com.burntoburn.easyshift.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * TODO: 나중에 삭제할 클래스
 */
@RestController("/api/test")
public class TestController {

    private UserService userService;
    
    @Operation(
            summary = "테스트 엔드포인트",
            description = "Swagger 및 API 동작 테스트를 위한 엔드포인트입니다."
    )
    @GetMapping
    public ResponseEntity<TestResponse> testEndpoint() {
        return ResponseEntity.ok(new TestResponse("테스트 성공"));
    }


    @PostMapping("/login")
    public ResponseEntity<User> signup(@RequestBody AddUserRequest request) throws Exception{
        User newUser = userService.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/signup")
    public String signup() {
        return "signup";
    }
    
    public record TestResponse(String message) {}
    
}
