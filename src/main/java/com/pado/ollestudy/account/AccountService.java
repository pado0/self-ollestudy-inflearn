package com.pado.ollestudy.account;

import com.pado.ollestudy.domain.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;

    // 우선 consoleMailSender를 주입받음
    private final JavaMailSender javaMailSender;

    private final PasswordEncoder passwordEncoder;

    // signup 코드 빌더 별도의 메소드로 분리. 리팩토링하여 컨트롤러 메소드 내 코드 줄이기.
    // 이 안에서는 트랜잭션이 돌아 잘 저장된다.
    private Account saveNewAccount(@Valid SignUpForm signUpForm) {
        Account account = Account.builder()
                .email(signUpForm.getEmail())
                .nickname((signUpForm.getNickname()))
                .password(passwordEncoder.encode(signUpForm.getPassword()))// encoder 적용
                .studyCreatedByWeb(true)
                .studyUpdatedByWeb(true)
                .studyEnrollmentResultByWeb(true)
                .build();
        return accountRepository.save(account);
    }

    // signup 내 메일 보내는 기능 별도 메소드로 분리하여 리팩토링.
    private void sendSignUpConfirmEmail(Account newAccount) {
        // 가입 완료 후 이메일 보내기. mail sender library 사용. 기본설정은 저장되어있음
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(newAccount.getEmail()); // 받는사람 메일
        mailMessage.setSubject("메일 제목입니다. 스터디 올레 회원가입 인증");
        mailMessage.setText("/check-email-token?token=" + newAccount.getEmailCheckToken() +
                "&email=" + newAccount.getEmail());

        javaMailSender.send(mailMessage);
    }

    // 이 코드만 컨트롤러에서 가져다가 쓴다.
    // 여기에서 email checkToken을 저장해줘야 한다. null이 리턴되고있음.
    // 메일인증 오류: saveNewAccount로 저장한 객체가 detatched 객체 (트랜잭션이 끝났음) 이라서, generate한 토큰이 세팅되지 않고있었음.
    // @Transactional 을 붙여서 처리
    @Transactional
    public void processNewAccount(SignUpForm signUpForm) {
        // 회원 저장
        Account newAccount = saveNewAccount(signUpForm);
        // 메일을 인증하는 토큰값 생성 후 세팅.
        newAccount.generateEmailCheckToken();
        // 메일 보내기
        sendSignUpConfirmEmail(newAccount);
    }
}
