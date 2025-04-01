package i_talktalk.i_talktalk.controller;

import i_talktalk.i_talktalk.dto.JwtToken;
import i_talktalk.i_talktalk.dto.MemberInfoDto;
import i_talktalk.i_talktalk.dto.SignInDto;
import i_talktalk.i_talktalk.dto.SignUpDto;
import i_talktalk.i_talktalk.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "회원 API", description = "회원가입, 로그인, 정보 변경 관련 API")
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/test")
    public String test() {
        return "test";
    }

    @PostMapping("/sign-up")
    @Operation(summary = "회원가입", description = "아이디와 비밀번호를 받아 회원가입을 수행")
    public String signup(@RequestBody SignUpDto signUpDto) {
        String result = memberService.signUp(signUpDto.getId(), signUpDto.getPassword());
        log.info("회원가입 결과 :" + result);
        return result;
    }

    @PostMapping("/sign-in")
    @Operation(summary = "로그인", description = "아이디와 비밀번호를 받아 JWT 토큰을 발급")
    public JwtToken signin(@RequestBody SignInDto signInDto) {
        JwtToken jwtToken = memberService.signIn(signInDto.getId(), signInDto.getPassword());
        if (jwtToken == null) {
            log.info("인증 실패");
            return null;
        } else {
            log.info("로그인 성공");
            return jwtToken;
        }
    }

    @PostMapping("/changeinfo")
    @Operation(summary = "회원 정보 변경", description = "이름, 나이, 비밀정보, 관심사 등을 수정")
    @SecurityRequirement(name = "JWT") // Swagger에 인증 필요 표시
    public String changeInfo(@RequestBody MemberInfoDto memberInfoDto) {
        return memberService.changeInfo(
                memberInfoDto.getName(),
                memberInfoDto.getAge(),
                memberInfoDto.getSecret(),
                memberInfoDto.getInterest()
        );
    }
}
