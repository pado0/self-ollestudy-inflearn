package com.pado.ollestudy.account;

import com.pado.ollestudy.domain.Account;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// 스프링 시큐티리 적용시 사용 가능한 mock mvc 기능들이 있다.
@SpringBootTest // WebEnvironment 설정으로 톰캣 띄워서 테스팅도 가
@AutoConfigureMockMvc
@Transactional
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @MockBean
    JavaMailSender javaMailSender;


    @DisplayName("인증 메일 확인 - 입력값 오류")
    @Test
    void checkEmailToken_with_wrong_input() throws Exception {
        mockMvc.perform(get("/check-email-token")
                        .param("token", "Aasdfasdf")
                        .param("email", "email@emai.com"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("error"))
                .andExpect(view().name("account/checked-email")) // 우리가 정한 뷰 이름대로 나오는지 확인
                .andExpect(unauthenticated()); // 인증오류가 발생해야함.

    }

    @DisplayName("인증 메일 확인 - 입력값이 올바른 경우")
    @Test
    void checkEmailToken_with_correct_input() throws Exception {

        Account account = Account.builder()
                .email("test@gmail.com")
                .password("12345678")
                .nickname("hyo")
                .build();

        Account newAccount = accountRepository.save(account);
        newAccount.generateEmailCheckToken();

        mockMvc.perform(get("/check-email-token")
                        .param("token", newAccount.getEmailCheckToken()) // test에 트랜젝션이 없어서 걸어줘야한다.
                        .param("email", newAccount.getEmail()))
                .andExpect(status().isOk())
                .andExpect(model().attributeDoesNotExist("error"))
                .andExpect(model().attributeExists("nickname"))
                .andExpect(model().attributeExists("numberOfUser"))
                .andExpect(view().name("account/checked-email")) // 우리가 정한 뷰 이름대로 나오는지 확인
                .andExpect(authenticated().withUsername("hyo")); // 인증 완료 상태
    }

    //sign-up 시큐리티 필터 해제시 Status expected:<200> but was:<403>
    @DisplayName("회원가입 화면이 보이는지 테스트")
    @Test
    void signUpForm() throws Exception {
        mockMvc.perform(get("/sign-up"))
                .andDo(print()) // 응답 프린트 가능
                .andExpect(status().isOk())
                .andExpect(view().name("account/sign-up"))
                .andExpect(model().attributeExists("signUpForm")) // 뷰에 이 어트리뷰트가 있는지 확인
                .andExpect(unauthenticated());

    }

    // 리팩토링 전에 검증코드로 검증하는게, 리팩토링 후 안정성 검증 확인에 유용하다
    @DisplayName("회원가입 처리 - 입력값 오류")
    @Test
    void signUpSubmit_with_wrong_input() throws Exception {
        // mockMvc로 post 요청을 만들어 넣어준다
        mockMvc.perform(post("/sign-up")
                        .param("nickname", "hyojin")
                        .param("email", "email...")
                        .param("password", "12345")
                        .with(csrf())) // csrf 인증 오류를 막기 위해 csrf 토큰 넣기
                .andExpect(status().isOk()) // post응답의 결과가 ok인지. // 스프링 시큐리티를 적용하면 기본적으로 csrf가 활성화됨
                .andExpect(view().name("account/sign-up")) // 결과가 sign-up으로 이동하면 성공!
                .andExpect(unauthenticated());
    }

    @DisplayName("회원가입 처리 - 입력값 정상")
    @Test
    void signUpSubmit_with_correct_input() throws Exception {
        mockMvc.perform(post("/sign-up")
                        .param("nickname", "hyojin")
                        .param("email", "bgshhd95@gmail.com")
                        .param("password", "12345678")
                        .with(csrf())) // csrf 인증 오류를 막기 위해 csrf 토큰 넣기
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"))
                .andExpect(authenticated().withUsername("hyojin"));

        Account account = accountRepository.findByEmail("bgshhd95@gmail.com");
        assertNotNull(account); // 가입한 메일 값이 존재하는지 확인
        assertNotEquals(account.getPassword(), "12345678"); // 입력한 패스워드랑 달라졌는지 (인코딩이 되었는지) 확인
        assertNotNull(account.getEmailCheckToken()); // email check token Null이어서 오류가 났다. 이에 테스트 코드를 추가한다.

        // SimpleMailMessage 타입의 센더가 호출 되었는지만 확인
        // 메일 내용까지 확인할 필요는 없음
        then(javaMailSender).should().send(any(SimpleMailMessage.class));

    }

}