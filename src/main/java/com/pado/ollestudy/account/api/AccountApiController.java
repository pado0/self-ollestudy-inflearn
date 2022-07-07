package com.pado.ollestudy.account.api;

import com.pado.ollestudy.account.AccountService;
import com.pado.ollestudy.account.SignUpForm;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AccountApiController {

    private final AccountService accountService;

    // Rest controller의 exception 잡기
    @ExceptionHandler
    public ResponseEntity handleException(MethodArgumentNotValidException e){
        final List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        return new ResponseEntity<>(fieldErrors, HttpStatus.BAD_REQUEST);
    }

    // getmapping은 회원가입시 만들어줄 필요가 없음
    // requestBody에 유효하지 않은 데이터가 포함될 경우 MethodArgumentNotV-Exception 발생
    // 이 Exception을 컨트롤러에 선언된 @ExceptionHandler handleException()이 잡아먹음
    // HandleException에서 리턴한 응답이 반환
   @PostMapping("/sign-up")
    public ResponseEntity signUpApiubmit(@Valid @RequestBody SignUpForm signUpForm){
        //rest api 오류처리 확인
        accountService.processNewAccount(signUpForm);

        // todo: 여기 DTO로 반환값 다시 처리해주기? Rest와 뷰 컨트롤러의 공존방법?
        return new ResponseEntity<>(signUpForm, HttpStatus.CREATED);
    }
}
