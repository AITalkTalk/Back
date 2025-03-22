package i_talktalk.i_talktalk.controller;

import i_talktalk.i_talktalk.dto.SignInDto;
import i_talktalk.i_talktalk.dto.SignUpDto;
import i_talktalk.i_talktalk.entity.Member;
import i_talktalk.i_talktalk.repository.MemberRepository;
import i_talktalk.i_talktalk.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MemberController {
    private final MemberService memberService;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
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
    public String signin(@RequestBody SignInDto signInDto){//서비스단으로 옮겨야 함.
        String id = signInDto.getId();
        String password = signInDto.getPassword();

        return memberService.signIn(id,password);
    }
}
