package com.pado.ollestudy.config;

import com.pado.ollestudy.account.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity // 내가 시큐리티의 모든 설정을 제어하겠다
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final AccountService accountService;
    private final DataSource dataSource;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable() // todo: disable 하지말고 postman에서 토큰 처리하는 법 확인하기
                .authorizeRequests()
                // 그냥 허용할 응답들
                .mvcMatchers("/", "/index", "/login", "/sign-up", "/check-email-token", "/email-login", "/check-email-login", "/login-link", "/api/sign-up").permitAll()
                .mvcMatchers(HttpMethod.GET, "/profile/*").permitAll() //프로필 요청은 get만 허용
                .anyRequest().authenticated(); // 나머지는 로그인을 해야 쓸 수 있다.

        http.formLogin()
                .loginPage("/login") // 커스텀 로그인 폼 경로
                .permitAll(); // 로그인 폼에 접근할 수 있는 사용자는 전체

        http.logout() // 기본적으로 로그아웃이 켜져있다.
                .logoutSuccessUrl("/"); // 로그아웃하고 어디로 갈지만 지정.

        // 가장 안전한 토큰 리퍼지토리 방법
        http.rememberMe()
                .tokenRepository(tokenRepository());

    }

    @Bean
    public PersistentTokenRepository tokenRepository(){
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        jdbcTokenRepository.setDataSource(dataSource);
        return jdbcTokenRepository;
    }

    // 스프링 시큐리티로 인해 뷰에서 로고 이미지가 불러와 지지 않는 문제 해결
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
                .mvcMatchers("/node_modules/**")
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }
}
