package i_talktalk.i_talktalk.controller;

import i_talktalk.i_talktalk.dto.JwtToken;
import i_talktalk.i_talktalk.dto.MemberInfoDto;
import i_talktalk.i_talktalk.dto.SignInDto;
import i_talktalk.i_talktalk.dto.SignUpDto;
import i_talktalk.i_talktalk.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MemberController {
    private final MemberService memberService;
    @GetMapping("/test")
    public String test() {
        return "test";
    }

    @PostMapping("/sign-up")
    public String signup(@RequestBody SignUpDto signUpDto){
        String id = signUpDto.getId();
        String password = signUpDto.getPassword();
        String result = memberService.signUp(id,password);
        log.info("회원가입 결과 :"+result);
        return result;
    }

    @PostMapping("/sign-in")
    public JwtToken signin(@RequestBody SignInDto signInDto){
        String id = signInDto.getId();
        String password = signInDto.getPassword();

        JwtToken jwtToken = memberService.signIn(id, password);
        if(jwtToken == null){
            log.info("인증 실패");
            return null;
        }else{
            log.info("로그인 성공");
            return jwtToken;
        }
    }

    @PostMapping("/changeinfo")
    public String changeInfo(@RequestBody MemberInfoDto memberInfoDto){
        return memberService.changeInfo(memberInfoDto.getName(),memberInfoDto.getSecret(),memberInfoDto.getInterest());
    }
}
