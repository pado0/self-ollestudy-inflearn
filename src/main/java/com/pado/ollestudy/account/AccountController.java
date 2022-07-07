package com.pado.ollestudy.account;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class AccountController {

    @GetMapping("/sign-up")
    public String signupForm(Model model){

        return "account/sign-up";
    }


}
