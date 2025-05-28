package i_talktalk.i_talktalk.service;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
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
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    private static final int CACHE_SIZE = 5;
    private static final Duration TTL = Duration.ofMinutes(1);

    @Qualifier("openaiRestTemplate")
    @Autowired
    private RestTemplate restTemplate;

    @Value("${openai.model}")
    private String model;

    @Value("${openai.api.url}")
    private String apiUrl;

    public String createQuiz(){ //아직 문제 난이도에 대한 고려는 안함. 나중에 나이도 반영해서 출제해야 함

        String system ="너는 어린이를 위한 퀴즈 문제를 출제하는 AI야.  \n" +
                "다음 조건을 만족하는 문제 50개를 JSON 형식으로 생성해줘.  \n" +
                "\n" +
                "### 조건:\n" +
                "1. 문제는 아이가 이해할 수 있도록 쉽고 명확해야 해.\n" +
                "2. 문제는 사칙연산 문제이고 한글이야.\n" +
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
                "    \"question\": \"3+8 은 뭘까?\",\n" +
                "    \"choices\": [\"11\", \"12\", \"13\", \"14\"],\n" +
                "    \"answer\": \"11\",\n" +
                "    \"category\": \"Math\"\n" +
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
        List<QuizMember> solvedList = quizMemberRepository.findAllByMemberId(currentMember.getMemberId());
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

    public Quiz getNextQuiz() throws JsonProcessingException {

        Long userId = getCurrentUserId();
        String redisKey = "quiz:user:" + userId;
        String solvedKey = "quiz:solved:" + userId;
        String backupKey = "quiz:solved:backup:" + userId;

        // Redis에 캐시된 퀴즈가 없으면 새로 로드
        if (Boolean.FALSE.equals(redisTemplate.hasKey(redisKey)) || redisTemplate.opsForList().size(redisKey) == 0) {
            log.info("문제 5개 로드!");
            handleSolvedQuizzes(userId, backupKey); // TTL 만료되었거나 마지막 문제까지 풀었을 때 처리
            loadQuizzesToRedis(userId, redisKey);
        }

        // 퀴즈 하나 꺼내기
        String quizJson = redisTemplate.opsForList().leftPop(redisKey);
        if (quizJson == null) return null;

        return objectMapper.readValue(quizJson, Quiz.class);
    }


    public void loadQuizzesToRedis(Long userId, String redisKey) throws JsonProcessingException {
        Set<String> solvedIds = quizMemberRepository.findAllByMemberId(userId)
                .stream()
                .map(QuizMember::getQuizId)
                .collect(Collectors.toSet());

        List<Quiz> unsolvedQuizzes = quizRepository.findAllByIdNotIn(solvedIds);
        List<Quiz> cacheList = unsolvedQuizzes.stream()
                .limit(CACHE_SIZE)
                .map(q -> new Quiz(q.getId(), q.getQuestion(), q.getChoices(), q.getAnswer(), q.getCategory()))
                .collect(Collectors.toList());

        for (Quiz quiz : cacheList) {
            String json = objectMapper.writeValueAsString(quiz);
            redisTemplate.opsForList().rightPush(redisKey, json);
        }

        redisTemplate.expire(redisKey, TTL);
    }

    public void handleSolvedQuizzes(Long userId, String backupKey) throws JsonProcessingException {
        List<String> solvedJsonList = redisTemplate.opsForList().range(backupKey, 0, -1);


        Optional<Member> found = memberRepository.findByMemberId(userId);
        Member foundMember = found.get();


        if (solvedJsonList == null || solvedJsonList.isEmpty()) return;

        List<QuizMember> records = solvedJsonList.stream()
                .map(json -> {
                    try {
                        Quiz quiz = objectMapper.readValue(json, Quiz.class);
                        foundMember.setPoint(foundMember.getPoint()+1);
                        return new QuizMember(userId, quiz.getId());
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());

        quizMemberRepository.saveAll(records);
        redisTemplate.delete(backupKey); // 꼭 삭제해야 메모리 누수 방지
    }



    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return memberRepository.findById(userDetails.getUsername()).orElseThrow().getMemberId();
    }




    public QuizMember solve(String quizId){
        //현재 로그인한 사용자 불러오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Member currentMember = memberRepository.findById(userDetails.getUsername()).get();


        QuizMember saved = quizMemberRepository.save(new QuizMember(currentMember.getMemberId(), quizId));
        return saved;
    }

    public void solve2(String quizId) throws JsonProcessingException {

        //현재 로그인한 사용자 불러오기
        Long userId = getCurrentUserId();

        Quiz quiz = quizRepository.findById(quizId).orElseThrow();
        String quizJson = objectMapper.writeValueAsString(quiz);

        String solvedKey = "quiz:solved:" + userId;
        String backupKey = "quiz:solved:backup:" + userId;

        // Redis에 저장
        redisTemplate.opsForList().rightPush(solvedKey, quizJson);
        redisTemplate.opsForList().rightPush(backupKey, quizJson);
        redisTemplate.expire(solvedKey, TTL);

    }


}
