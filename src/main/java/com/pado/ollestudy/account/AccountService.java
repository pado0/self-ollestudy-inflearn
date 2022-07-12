package com.pado.ollestudy.account;

import com.pado.ollestudy.domain.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService implements UserDetailsService {
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
    public Account processNewAccount(SignUpForm signUpForm) {
        // 회원 저장
        Account newAccount = saveNewAccount(signUpForm);
        // 메일을 인증하는 토큰값 생성 후 세팅.
        newAccount.generateEmailCheckToken();
        // 메일 보내기
        sendSignUpConfirmEmail(newAccount);

        return newAccount;
    }

    @Transactional
    public void resendSignUpConfirmEmail(Account account) {
        account.generateEmailCheckToken(); // 변경감지 토큰 갱신
        sendSignUpConfirmEmail(account); // 메일전송
    }

    public void login(Account account) {

        // auth manager 내부에서 사용해야하는 토큰임. 정석적으로 하려면 사용자 입력 pw 기반으로 AuthticationManager을 통해 로그인 처리를 해야함.
        // auth manager가 하는 일을 그대로 한다고 생각. 이렇게 하는 이유는, 우리가 인코딩한 Pw밖에 접근을 못하는 상태이기 때문.
        // 사용자가 입력한 평문 pw에 접근할 수 없음. 접근하려면 db에 담아두어야하는게 그렇게 안할거임. 이메일 체크 api에서도 평문 pw가 필요한데, db에서 읽어올 수 없으니 그냥 아래처럼 코딩한다.
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                new UserAccount(account), // principal 설정. principal은 시스템을 사용하려하는 사용자를 의미
                account.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_USER")));

        // 컨텍스트 홀더를 통해 로그인 처리
        SecurityContext context = SecurityContextHolder.getContext(); // 컨텍스트 홀더가 컨텍스트를 들고있음.
        context.setAuthentication(token); // 이 컨텍스트에 인증을 세팅해줌. 정석적인 방법은 아니지만 지금 상황에서 최선
    }

    @Override
    public UserDetails loadUserByUsername(String emailOrNickname) throws UsernameNotFoundException {
        Account account = accountRepository.findByEmail(emailOrNickname);
        if (account == null) {
            account = accountRepository.findByNickname(emailOrNickname);
        }
        if (account == null) {
            throw new UsernameNotFoundException(emailOrNickname);
        }

        return new UserAccount(account); // 우리가 만든 principal을 리턴
    }
}