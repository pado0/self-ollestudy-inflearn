package com.pado.ollestudy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AppConfig {

    @Bean
    public PasswordEncoder passwordEncoder(){
        // bcrypt 인코더를 사용하게 한다.
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
