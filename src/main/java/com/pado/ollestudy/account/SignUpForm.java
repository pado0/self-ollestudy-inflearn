package com.pado.ollestudy.account;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

// 회원가입 할 때 받아올 데이터
@Data
public class SignUpForm {

    // 서브밋 폼 검증 추가
    @NotBlank
    @Length(min = 3, max = 20)
    // 정규식으로 표현, []범위에 있는 값들을 {}내 자리수로 표현
    // 백엔드에도 검증 필요. 프론트에서 js로 바로 submit 해버릴 수 있다. 뚫린다.
    @Pattern(regexp = "^[ㄱ-ㅎ가-힣a-z0-9_-]{3,20}$")
    private String nickname;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    @Length(min = 8, max = 50)
    private String password;

}
