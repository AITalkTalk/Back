package i_talktalk.i_talktalk.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import i_talktalk.i_talktalk.dto.ApiResponse;
import i_talktalk.i_talktalk.dto.JwtToken;
import i_talktalk.i_talktalk.dto.SignInDto;
import i_talktalk.i_talktalk.dto.SignUpDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("회원 관련 로직 테스트")
class MemberControllerTest {

    @Autowired
    MockMvc mvc;//MockMvc는 테스트를 위한 HTTP 요청을 애플리케이션의 서블릿 컨텍스트 위에서 수행.
    // 컨트롤러, 시큐리티 필터, 서비스, 리포지토리까지 전부 진짜로 실행되고, 전체 흐름이 실제처럼 동작
    //앞으로 Valid 통해서 입력이 빈 요청이나 잘못된 형식도 거르는 테스트 만들 예정

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    // 유틸 메서드: JSON 요청 바디 생성
    private String toJson(Object obj) throws JsonProcessingException {
        return objectMapper.writeValueAsString(obj);
    }

    @Test
    @DisplayName("회원가입 및 로그인 테스트(올바른 요청)")
    void signUpAndInSuccess() throws Exception {
        SignUpDto signUpDto= new SignUpDto("qwer","1234");
        mockMvc.perform(post("/sign-up")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(signUpDto)))
                .andExpect(status().isCreated());

        SignInDto signInDto1= new SignInDto("qwer","1234");
        MvcResult result = mockMvc.perform(post("/sign-in")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(signInDto1)))
                .andExpect(status().isOk()).andReturn();
        String content = result.getResponse().getContentAsString();
        ApiResponse<JwtToken> apiResponse = objectMapper.readValue(
                content,
                new TypeReference<ApiResponse<JwtToken>>() {}
        );

        String grantType = apiResponse.getData().getGrantType();
        Assertions.assertThat(grantType.equals("Bearer")).isTrue();

    }


    @Test
    @DisplayName("회원가입 및 로그인 테스트(잘못된 요청)")
    void signUpAndInFail() throws Exception {
        SignUpDto signUpDto= new SignUpDto("qwer","1234");
        mockMvc.perform(post("/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(signUpDto)))
                .andExpect(status().isCreated());

        SignInDto signInDto1= new SignInDto("wrongid","1234");
        MvcResult result = mockMvc.perform(post("/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(signInDto1)))
                .andExpect(status().isUnauthorized()).andReturn();

    }

}