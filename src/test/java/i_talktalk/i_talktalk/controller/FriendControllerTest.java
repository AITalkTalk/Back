package i_talktalk.i_talktalk.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import i_talktalk.i_talktalk.dto.ApiResponse;
import i_talktalk.i_talktalk.dto.JwtToken;
import i_talktalk.i_talktalk.dto.SignInDto;
import i_talktalk.i_talktalk.dto.SignUpDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("친구 추가 관련 로직 테스트")
class FriendControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    // 유틸 메서드: JSON 요청 바디 생성
    private String toJson(Object obj) throws JsonProcessingException {
        return objectMapper.writeValueAsString(obj);
    }

    public String signInAndUp(String id, String password) throws Exception {
        SignUpDto signUpDto= new SignUpDto(id, password);
        mockMvc.perform(post("/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(signUpDto)))
                .andExpect(status().isCreated());

        SignInDto signInDto1= new SignInDto(id,password);
        MvcResult result = mockMvc.perform(post("/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(signInDto1)))
                .andExpect(status().isOk()).andReturn();
        String content = result.getResponse().getContentAsString();
        ApiResponse<JwtToken> apiResponse = objectMapper.readValue(
                content,
                new TypeReference<ApiResponse<JwtToken>>() {}
        );

        return apiResponse.getData().getAccessToken();
    }
    @Test
    void requestFriendAndApprove() throws Exception {
        String userId1="asdf";
        String userPW1="1234";
        String userId2="qwer";
        String userPW2="1234";
        String token1 =  signInAndUp(userId1,userPW1);
        String token2 =  signInAndUp(userId2,userPW2);

        mockMvc.perform(post("/friends/requests")
                        .queryParam("name",userId2)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/friends/requests")
                .header("Authorization", "Bearer "+token1)
                        .queryParam("name",userId2)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        MvcResult mvcResult = mockMvc.perform(get("/friends/requests")
                        .header("Authorization", "Bearer " + token2)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
        ApiResponse<List<String>> apiResponse = objectMapper.readValue(contentAsString,
                new TypeReference<ApiResponse<List<String>>>() {});
        Assertions.assertEquals(apiResponse.getData().size(),1);
        assertEquals(apiResponse.getData().get(0),"asdf");


        mockMvc.perform(patch("/friends/approve")
                        .header("Authorization", "Bearer "+token2)
                        .queryParam("name",userId1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mvcResult = mockMvc.perform(get("/friends/requests")
                        .header("Authorization", "Bearer " + token2)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        contentAsString = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);

        apiResponse = objectMapper.readValue(contentAsString,
                new TypeReference<ApiResponse<List<String>>>() {});
        Assertions.assertEquals(apiResponse.getData().size(),1);
        assertEquals(apiResponse.getData().get(0),"친구 요청이 없습니다");
    }

    @Test
    void showFriendRequests() {
    }

    @Test
    void approveFriend() {
    }

    @Test
    void deleteFriend() {
    }
}