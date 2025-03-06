package com.burntoburn.easyshift.util;

import com.burntoburn.easyshift.entity.user.CustomUserDetails;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {
    public static CustomUserDetails getCurrentUser() {
        //저장된 유저의 인증 정보를 가져옴
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof CustomUserDetails) {
            return (CustomUserDetails) principal;
        }
        throw new RuntimeException("User not authenticated");
    }
}
