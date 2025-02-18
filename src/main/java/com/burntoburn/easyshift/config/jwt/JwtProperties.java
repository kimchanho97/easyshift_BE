package com.burntoburn.easyshift.config.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties("jwt")
public class Jwtproperties { // jwt 생성시 사용할 페이로드?
    private String issuer;
    private String secretKey;
}
