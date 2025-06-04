package i_talktalk.i_talktalk.controller;

import i_talktalk.i_talktalk.dto.*;
import i_talktalk.i_talktalk.entity.Member;
import i_talktalk.i_talktalk.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "회원 API", description = "회원가입, 로그인, 정보 변경 관련 API")
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/test")
    public ResponseEntity<ApiResponse<String>> test() {
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK, "테스트 응답", "test"));
    }

    @PostMapping("/sign-up")
    @Operation(summary = "회원가입 API", description = "아이디와 비밀번호를 받아 회원가입을 수행")
    public ResponseEntity<ApiResponse<Void>> signup(@RequestBody SignUpDto signUpDto) {
        String result = memberService.signUp(signUpDto);
        log.info("회원가입 결과: " + result);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse<>(HttpStatus.CREATED, result, null));
    }

    @PostMapping("/sign-in")
    @Operation(summary = "로그인 API", description = "아이디와 비밀번호를 받아 JWT 토큰을 발급")
    public ResponseEntity<ApiResponse<JwtToken>> signin(@RequestBody SignInDto signInDto) {
        JwtToken jwtToken = memberService.signIn(signInDto.getId(), signInDto.getPassword());
        if (jwtToken == null) {
            log.info("인증 실패");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(HttpStatus.UNAUTHORIZED, "아이디 또는 비밀번호가 일치하지 않습니다.", null));
        } else {
            log.info("로그인 성공");
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK, "로그인 성공", jwtToken));
        }
    }

    @PostMapping("/changeinfo")
    @Operation(summary = "회원 정보 변경 API", description = "이름, 나이, 비밀정보, 관심사 등을 수정")
    @SecurityRequirement(name = "JWT")
    public ResponseEntity<ApiResponse<Void>> changeInfo(@RequestBody MemberInfoDto memberInfoDto) {
        String result = memberService.changeInfo(
                memberInfoDto.getName(),
                memberInfoDto.getAge(),
                memberInfoDto.getSecret(),
                memberInfoDto.getInterest()
        );
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK, "회원 정보 수정 완료", null));
    }

    @GetMapping("/info")
    @Operation(summary = "회원 정보 조회 API", description = "회원 정보를 조회")
    @SecurityRequirement(name = "JWT")
    public ResponseEntity<ApiResponse<MemberInfoDto>> getInfo(){
        MemberInfoDto info = memberService.getInfo();
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK,"회원 정보 조회 완료",info));
    }

    @PostMapping("/secret")
    @Operation(summary = "회원 비밀 키 인증 API", description = "비밀키 인증")
    @SecurityRequirement(name = "JWT")
    public ResponseEntity<ApiResponse<Void>> checkSecret(@RequestParam String secret){
        String result = memberService.checkSecret(secret);
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK, result, null));

    }

}
