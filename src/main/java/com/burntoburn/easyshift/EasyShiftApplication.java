package com.burntoburn.easyshift;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class EasyShiftApplication {
    
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(EasyShiftApplication.class);
        app.setBannerMode(Banner.Mode.OFF); // 실행 시, 처음에 뜨는 배너 출력 끄기
        app.run(args);
        
        log.info("EasyShiftApplication started successfully.");
    }
    
}
