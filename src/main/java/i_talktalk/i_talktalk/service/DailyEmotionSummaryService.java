package i_talktalk.i_talktalk.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import i_talktalk.i_talktalk.dto.*;
import i_talktalk.i_talktalk.entity.DailyEmotionSummary;
import i_talktalk.i_talktalk.entity.Member;
import i_talktalk.i_talktalk.entity.Record;
import i_talktalk.i_talktalk.exception.MemberNotFoundException;
import i_talktalk.i_talktalk.repository.DailyEmotionSummaryRepository;
import i_talktalk.i_talktalk.repository.MemberRepository;
import i_talktalk.i_talktalk.repository.RecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DailyEmotionSummaryService {
    private final DailyEmotionSummaryRepository dailyEmotionSummaryRepository;
    private final RecordRepository recordRepository;
    private final MemberRepository memberRepository;

    @Qualifier("openaiRestTemplate")
    @Autowired
    private RestTemplate restTemplate;

    @Value("${openai.model}")
    private String model;

    @Value("${openai.api.url}")
    private String apiUrl;

    public void analyzeAndStore(String Id, LocalDateTime start, LocalDateTime end) throws JsonProcessingException {
        List<Record> records = recordRepository.findByMember_IdAndCreatedAtBetween(Id, start, end);

        if (records.isEmpty()) {
            log.info("데이터 없음!!!");
            return;
        }
        log.info("분석 시작!!!");


        // 사용자 메시지만 추출해서 분석용 JSON 생성
        List<Map<String, String>> userMessagesForAnalysis = records.stream()
                .filter(record -> "user".equalsIgnoreCase(record.getMessage().getRole()))
                .map(record -> Map.of(
                        "id", record.getId(),
                        "message", record.getMessage().getContent()
                ))
                .toList();


        // 프롬프트 메시지 생성
        String system = "You are a helpful assistant that classifies the emotional sentiment of conversations. Please determine whether the following conversation is positive, negative, or neutral."
                +"감정분석은 사용자의 대화만 해주고, 결과는 다음과 같은 형식으로 사용자 메시지만 반환해줘:\n\n" +
                "[\n" +
                "  {\n" +
                "    \"id\": \"record1234\",\n" +
                "    \"message\": \"오늘 너무 힘들었어. 아무것도 하기 싫어.\",\n" +
                "    \"sentiment\": \"NEGATIVE\"\n" +
                "  }\n" +
                "]\n\n";

        ChatRequest request = new ChatRequest(model);
        List<Message> messages = request.getMessages();
        messages.add(new Message("system", system));
        messages.add(new Message("user",
                        "다음은 대화 내용이야:\n" + userMessagesForAnalysis
        ));

        log.info(userMessagesForAnalysis.toString());


        // OpenAI 호출
        ChatResponse response = restTemplate.postForObject(apiUrl, request, ChatResponse.class);

        log.info(response.getChoices().get(0).getMessage().getContent());
        // 응답 결과 파싱
        ObjectMapper mapper = new ObjectMapper();
        List<AnalyzedMessage> analyzedMessages = mapper.readValue(
                response.getChoices().get(0).getMessage().getContent(),
                new TypeReference<List<AnalyzedMessage>>() {}
        );

        // 감정 분석 결과를 Map으로 변환 (id → sentiment)
        Map<String, String> sentimentMap = analyzedMessages.stream()
                .collect(Collectors.toMap(AnalyzedMessage::getId, AnalyzedMessage::getSentiment));

        boolean hasNegative = false;

        // 각 레코드에 감정 저장
        for (Record record : records) {
            String sentiment = sentimentMap.get(record.getId());
            if (sentiment != null) {
                record.setSentiment(sentiment);
                recordRepository.save(record);
                if ("NEGATIVE".equalsIgnoreCase(sentiment)) {
                    hasNegative = true;
                }
            }
        }

        // fullChat 생성 (ai + user 대화 모두 포함, user만 감정 태깅)
        String fullChat = records.stream()
                .map(record -> {
                    String role = record.getMessage().getRole();
                    String content = record.getMessage().getContent();

                    if ("user".equalsIgnoreCase(role)) {
                        String sentiment = sentimentMap.get(record.getId());
                        return "사용자: " + content + (sentiment != null ? " (" + sentiment + ")" : "");
                    } else {
                        return "ai: " + content;
                    }
                })
                .collect(Collectors.joining("\n"));


        Optional<Member> found = memberRepository.findById(Id);
        // 일일 감정 요약 저장
        DailyEmotionSummary summary = new DailyEmotionSummary();
        summary.setName(found.get().getName());//
        summary.setDate(start.toLocalDate());
        summary.setSentiment(hasNegative ? "NEGATIVE" : "POSITIVE");
        summary.setChat(fullChat);

        dailyEmotionSummaryRepository.save(summary);
    }



    public DailyEmotionSummaryDto getDailyEmotionSummary(LocalDate date) {//단건 조회



        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Member currentMember = memberRepository.findById(userDetails.getUsername()).get();


        log.info("1111");
//        DailyEmotionSummary summary = dailyEmotionSummaryRepository.findByNameAndDate(currentMember.getName(),date);



        log.info("조회 조건 → name: {}, date: {}", currentMember.getName(), date);
        DailyEmotionSummary summary = dailyEmotionSummaryRepository
                .findByNameAndDate(currentMember.getName(), date)
                .orElseThrow(() -> new MemberNotFoundException("기록이 없습니다."));



        log.info("2222");
        return new DailyEmotionSummaryDto(summary.getId(), summary.getName(), summary.getDate(), summary.getSentiment(), summary.getChat());

    }
}
