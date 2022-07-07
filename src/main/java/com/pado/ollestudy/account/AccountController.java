package com.pado.ollestudy.account;

import lombok.RequiredArgsConstructor;
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

        //todo: 회원가입 처리

        return "redirect:/";
    }

}
