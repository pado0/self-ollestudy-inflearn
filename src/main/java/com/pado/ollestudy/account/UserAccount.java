package com.pado.ollestudy.account;

import com.pado.ollestudy.domain.Account;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.List;

// 로그인 principal에서 account 프로퍼티를 들고있는 중간 역할을 하는 객체
// 스프링 시큐리티가 다루는 유저정보와, 우리 도메인에 정의한 유저정보 사이의 갭을 처리해줌.
@Getter
public class UserAccount extends User { // extends User는 시큐리티에서 오는 객체

    private Account account;

    public UserAccount(Account account) {
        super(account.getNickname(), account.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
        this.account = account;
    }
}
