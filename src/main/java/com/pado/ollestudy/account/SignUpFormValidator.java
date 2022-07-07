package com.pado.ollestudy.account;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

// 커스텀 validator
@Component // 빈을 주입받아야하니 의존관계 자동주입을 위해 빈으로 설정
@RequiredArgsConstructor // 자동주입 Autowired 생략을 위함
public class SignUpFormValidator implements Validator {

    // 빈을 주입받아야 함
    private final AccountRepository accountRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        // SignUpForm의 인스턴스를 검사
        // 인자로 넘어온 클래스가 이 검증 클래스를 지원하는지 확인
        return clazz.isAssignableFrom(SignUpForm.class);
    }

    // 실제 검증 로직 구현
    @Override
    public void validate(Object target, Errors errors) {
        // todo: email, nickname 중복여부 검사 필요
        SignUpForm signUpForm = (SignUpForm) target; // target에 SignUpForm이 있음
        if (accountRepository.existsByEmail(signUpForm.getEmail())) {
            errors.rejectValue("email", "invalid.email", new Object[]{signUpForm.getEmail()}, "이미 사용중인 이메일입니다");
        }

        if (accountRepository.existsByNickname(signUpForm.getNickname())) {
            errors.rejectValue("nickname", "invalid.nickname", new Object[]{signUpForm.getNickname()}, "이미 사용중인 닉네임입니다");
        }
    }
}
