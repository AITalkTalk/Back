package i_talktalk.i_talktalk.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import i_talktalk.i_talktalk.dto.ChatRequest;
import i_talktalk.i_talktalk.dto.ChatResponse;
import i_talktalk.i_talktalk.dto.CustomUserDetails;
import i_talktalk.i_talktalk.dto.Message;
import i_talktalk.i_talktalk.entity.Member;
import i_talktalk.i_talktalk.entity.Quiz;
import i_talktalk.i_talktalk.entity.QuizMember;
import i_talktalk.i_talktalk.repository.MemberRepository;
import i_talktalk.i_talktalk.repository.QuizMemberRepository;
import i_talktalk.i_talktalk.repository.QuizRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class QuizService {
    private final QuizRepository quizRepository;
    private final QuizMemberRepository quizMemberRepository;
    private final MemberRepository memberRepository;

    @Qualifier("openaiRestTemplate")
    @Autowired
    private RestTemplate restTemplate;

    @Value("${openai.model}")
    private String model;

    @Value("${openai.api.url}")
    private String apiUrl;

    public String createQuiz(){ //아직 문제 난이도에 대한 고려는 안함. 나중에 나이도 반영해서 출제해야 함

        String system ="너는 어린이를 위한 퀴즈 문제를 출제하는 AI야.  \n" +
                "다음 조건을 만족하는 문제 10개를 JSON 형식으로 생성해줘.  \n" +
                "\n" +
                "### 조건:\n" +
                "1. 문제는 아이가 이해할 수 있도록 쉽고 명확해야 해.\n" +
                "2. 문제는 다양한 분야를 포함하고 한글이야.\n" +
                "3. 각 문제는 4개의 선택지(보기)를 가져야 하고, 답은 하나만 있어야 해.\n" +
                "4. JSON 형식으로 출력해야 해.\n" +
                "\n" +
                "다음은 문제 예시야. \n" +
                "[\n" +
                "  {\n" +
                "    \"question\": \"5 × 6은 뭘까?\",\n" +
                "    \"choices\": [\"28\", \"30\", \"32\", \"36\"],\n" +
                "    \"answer\": \"30\",\n" +
                "    \"category\": \"Math\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"question\": \"남한의 수도는 어디일까?\",\n" +
                "    \"choices\": [\"서울\", \"부산\", \"도쿄\", \"베이징\"],\n" +
                "    \"answer\": \"서울\",\n" +
                "    \"category\": \"Geography\"\n" +
                "  }\n" +
                "]";

        // create a request
        ChatRequest request = new ChatRequest(model);
        List<Message> messages = request.getMessages();
        messages.add(new Message("system", system));

        // call the API
        ChatResponse response = restTemplate.postForObject(apiUrl, request, ChatResponse.class);
        if (response == null || response.getChoices() == null || response.getChoices().isEmpty()) {
            return "No response";
        }

        String responseJson = response.getChoices().get(0).getMessage().getContent();
        responseJson = responseJson.replace("```json", "").replace("```", "").trim();
        ObjectMapper objectMapper = new ObjectMapper();
        List<Quiz> quizList=null;
        try {
            // JSON을 List<Quiz> 객체로 변환
            quizList = objectMapper.readValue(responseJson, new TypeReference<List<Quiz>>() {});
            quizRepository.saveAll(quizList);


        } catch (Exception e) {
            e.printStackTrace();
        }


        return responseJson;
    }

    public Quiz getNotSolvedQuiz(){
        //현재 로그인한 사용자 불러오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Member currentMember = memberRepository.findById(userDetails.getUsername()).get();

        //현재 사용자가 풀지 않은 문제 반환하기
        List<QuizMember> solvedList = quizMemberRepository.findAllByMemberId(currentMember.getMember_id());
        log.info("풀은 문제 수:"+ solvedList.size());
        Set<String> solvedQuizIds = solvedList.stream()
                .map(QuizMember::getQuizId)
                .collect(Collectors.toSet());
        List<Quiz> unsolved = quizRepository.findAllByIdNotIn(solvedQuizIds);
        if(unsolved.isEmpty()){// 모든 문제를 풀었을 경우
            return null;
        }

        // 하나 랜덤 선택
        Quiz recommended = unsolved.get(ThreadLocalRandom.current().nextInt(unsolved.size()));
        return recommended;
    }

    public QuizMember solve(String quizId){
        //현재 로그인한 사용자 불러오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Member currentMember = memberRepository.findById(userDetails.getUsername()).get();


        QuizMember saved = quizMemberRepository.save(new QuizMember(currentMember.getMember_id(), quizId));
        return saved;
    }
}
