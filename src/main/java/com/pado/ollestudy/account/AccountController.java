package com.pado.ollestudy.account;

import com.pado.ollestudy.domain.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class AccountController {

    private final SignUpFormValidator signUpFormValidator;
    private final AccountRepository accountRepository;

    // 우선 consoleMailSender를 주입받음
    private final JavaMailSender javaMailSender;

    // validator를 initbinder에 추가해놓으면
    // signUpForm을 받을 때 Bean validator 303 검증도 하고, InitBinder내 SignUpFormValidator도 자동 수행됨
    @InitBinder("signUpForm")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(signUpFormValidator);
    }

    @GetMapping("/sign-up")
    public String signupForm(Model model){

        // SignUpForm 객체를 통해 값을 받아와야 함
        //model.addAttribute("signUpForm", new SignUpForm());
        model.addAttribute(new SignUpForm()); // 이름 생략 가능

        return "account/sign-up";
    }

    // 여러 폼데이터 값을 객체로 바인딩해주려면 @ModelAttribute
    // Errors 객체로 검증 에러를 받아준다.
    @PostMapping("/sign-up")
    public String signupSubmit(@Valid @ModelAttribute SignUpForm signUpForm, Errors errors) {
        if (errors.hasErrors()) {
            return "account/sign-up"; // 실패시 다시 폼을 보여줌
        }

        // initBinder 덕분에 생략 가능
        // signUpFormValidator.validate(signUpForm, errors);

        Account account = Account.builder()
                .email(signUpForm.getEmail())
                .nickname((signUpForm.getNickname()))
                .password(signUpForm.getPassword()) // todo: encoding 해야함
                .studyCreatedByWeb(true)
                .studyUpdatedByWeb(true)
                .studyEnrollmentResultByWeb(true)
                .build();

        // 회원 저장
        Account newAccount = accountRepository.save(account);

        // 메일을 인증하는 토큰값 생성
        newAccount.generateEmailCheckToken();

        // 가입 완료 후 이메일 보내기. mail sender library 사용. 기본설정은 저장되어있음
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(newAccount.getEmail()); // 받는사람 메일
        mailMessage.setSubject("메일 제목입니다. 스터디 올레 회원가입 인증");
        mailMessage.setText("/check-email-token?token=" + newAccount.getEmailCheckToken() +
                "&email=" + newAccount.getEmail());

        javaMailSender.send(mailMessage);

        return "redirect:/";
    }

}
