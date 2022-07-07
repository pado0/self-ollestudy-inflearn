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
import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
public class AccountController {

    private final SignUpFormValidator signUpFormValidator;
    private final AccountService accountService;

    private final AccountRepository accountRepository;

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

        // initBinder 덕분에 아래 코드 생략 가능
        // signUpFormValidator.validate(signUpForm, errors);

        // 회원 가입 프로세스 (회원 가입정보 저장, 이메일 보내기)
        // 컨트롤러는 새 회원을 처리하는 서비스만 호출하고, 서비스에서 이메일 보내는처리. 컨트롤러는 알 필요 없다.
        accountService.processNewAccount(signUpForm);

        return "redirect:/";
    }

    // 사용자가 mailMessage.setText("/check-email-token?token=" + newAccount.getEmailCheckToken() +"&email=" + newAccount.getEmail());
    // 위 주소로 입력한 링크가 메일주소, 토큰값과 맞는지 확인하는 컨트롤러
    @GetMapping("/check-email-token")
    public String checkEmailToken(String token, String email, Model model) {

        String view = "account/checked-email";

        // 메일주소 존재하는지 확인
        Account account = accountRepository.findByEmail(email);

        if(account == null){
            model.addAttribute("error", "wrong.email");
            return view;
        }

        // 토큰이 유효한지 확인
        if(!account.getEmailCheckToken().equals(token)){
            model.addAttribute("error", "wrong.token");
            return view;
        }

        // 여기까지 얼리리턴 완료시 정상적으로 인증된 회원임
        account.setEmailVerified(true); // 인증 정상 state로 처리
        account.setJoinedAt(LocalDateTime.now());

        // 뷰에 넘길 회원 수, 닉네임
        model.addAttribute("numberOfUser", accountRepository.count());
        model.addAttribute("nickname", account.getNickname());

        return view;
    }

}
