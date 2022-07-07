package com.pado.ollestudy.account;

import lombok.Data;

// 회원가입 할 때 받아올 데이터
@Data
public class SignUpForm {

    private String nickname;
    private String email;
    private String password;

}
