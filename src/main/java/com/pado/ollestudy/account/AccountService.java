package com.pado.ollestudy.account;

import com.pado.ollestudy.domain.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.validation.Valid;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;

    // 우선 consoleMailSender를 주입받음
    private final JavaMailSender javaMailSender;

    // signup 코드 빌더 별도의 메소드로 분리. 리팩토링하여 컨트롤러 메소드 내 코드 줄이기.
    private Account saveNewAccount(@Valid SignUpForm signUpForm) {
        Account account = Account.builder()
                .email(signUpForm.getEmail())
                .nickname((signUpForm.getNickname()))
                .password(signUpForm.getPassword()) // todo: encoding 해야함
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
    public void processNewAccount(SignUpForm signUpForm) {
        // 회원 저장
        Account newAccount = saveNewAccount(signUpForm);
        // 메일을 인증하는 토큰값 생성
        newAccount.generateEmailCheckToken();
        // 메일 보내기
        sendSignUpConfirmEmail(newAccount);
    }
}
