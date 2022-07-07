package com.pado.ollestudy.account;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class AccountController {

    @GetMapping("/sign-up")
    public String signupForm(Model model){

        // SignUpForm 객체를 통해 값을 받아와야 함
        //model.addAttribute("signUpForm", new SignUpForm());
        model.addAttribute(new SignUpForm()); // 이름 생략 가능

        return "account/sign-up";
    }


}
