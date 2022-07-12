package com.pado.ollestudy.main;

import com.pado.ollestudy.domain.Account;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    // 첫페이지 요청을 처리하는 핸들러
    @GetMapping("/")
    // 어노테이션을 통해 익명사용자의 경우 null로, 인증사용자의 경우 account 프로퍼티를 반환
    // @CurrentUser 어노테이션으로 개발자가 원하는 대로 principal을 조회, 널체크
    // ?? Account account에 현재 유저가 들어가는 이유?
    // CurrentUser 어노테이션의 authprincipal 덕분
    // 이것과 UserAccount의 관계?
    // UserAccount를 통해 User로 등록. (extends current user)
    // 로그인 login() 함수 실행시 new로 userAccount 생성하여 시큐리티 관리 user화 됨
    // 그래서 currentUser로 꺼내쓸 수 있음
    public String home(@CurrentUser Account account, Model model) {
        if (account != null) {
            model.addAttribute(account);
        }

        return "index";
    }
}
