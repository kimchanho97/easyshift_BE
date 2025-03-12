package com.burntoburn.easyshift.service.user;

import com.burntoburn.easyshift.dto.user.UserInfoRequest;
import com.burntoburn.easyshift.entity.user.User;
import com.burntoburn.easyshift.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User update(UserInfoRequest request) throws Exception {
        User updatableUser = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new IllegalArgumentException("해당 이메일을 가진 사용자가 없습니다."));
        updatableUser.updateUser(request.toEntity());

        return userRepository.save(updatableUser);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("unexpected User"));
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Unexpected User"));
    }
}
