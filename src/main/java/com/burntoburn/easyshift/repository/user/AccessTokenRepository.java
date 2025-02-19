package com.burntoburn.easyshift.repository.user;

import com.burntoburn.easyshift.entity.user.Token;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccessTokenRepository extends JpaRepository<Token, Long> {

    Optional<Token> findByUserId(Long userId);

}
