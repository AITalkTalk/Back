package i_talktalk.i_talktalk.service;

import i_talktalk.i_talktalk.dto.ChatRequest;
import i_talktalk.i_talktalk.dto.ChatResponse;
import i_talktalk.i_talktalk.dto.CustomUserDetails;
import i_talktalk.i_talktalk.dto.Message;
import i_talktalk.i_talktalk.entity.Member;
import i_talktalk.i_talktalk.entity.Record;
import i_talktalk.i_talktalk.repository.MemberRepository;
import i_talktalk.i_talktalk.repository.RecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ChatService {
    private final RecordRepository recordRepository;
    private final MemberRepository memberRepository;

    @Qualifier("openaiRestTemplate")
    @Autowired
    private RestTemplate restTemplate;

    @Value("${openai.model}")
    private String model;

    @Value("${openai.api.url}")
    private String apiUrl;


    public String chat(String prompt){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Member currentMember = memberRepository.findById(userDetails.getUsername()).get();
        List<Record> records = recordRepository.findTop6ByOrderByIdDesc();

        String interest = currentMember.getInterest();

//        String system = "너는 어린아이들을 상담하는 assistant야. " +
//                "너의 최우선 임무는, 부드럽고 자연스러운 대화를 통해 아이의 관심사(" + interest + ")를 반드시 알아내는 거야. " +
//                "아이들은 낯을 가릴 수 있기 때문에, 직접적인 질문은 피하고, 친근하고 재미있는 주제로 대화를 시작한 뒤 천천히 유도해야 해.\n\n" +
//                "대화 흐름은 다음과 같아:\n" +
//                "1. 가볍고 친근한 주제로 이야기를 시작한다. (예: 좋아하는 색깔, 음식, 놀이나 게임 등)\n" +
//                "2. 아이의 답변에 공감하고 반응하며 자연스럽게 관심사를 묻는다.\n" +
//                "3. 관심사(" + interest + ")와 관련된 정보를 알아내기 전까지는 대화를 끝내지 않는다.\n" +
//                "4. 관심사를 알아낸 경우, 아이가 긍정적인 기분으로 대화를 마무리할 수 있도록 따뜻하게 인사하며 종료한다.\n\n" +
//                "주의사항:\n" +
//                "- 대화 도중 아이가 말한 내용을 반드시 기억하고 적절히 반응해야 해.\n" +
//                "- 아이의 감정 상태에 민감하게 반응하며, 항상 공감하는 표현을 사용해야 해.\n" +
//                "- 답변에 이모지를 사용하지 말고 1~2문장으로 간결하게 대답해줘.\n" +
//                "- 대화를 억지로 끌거나 어색하게 만들지 말고, 자연스럽게 이어가야 해.\n\n";

        String system = "You are an assistant who talks with young children. " +
                "Your top priority is to gently and naturally discover the child's interests (" + interest + ") through conversation. " +
                "Since children can be shy, you must avoid direct questions. Instead, start with fun and friendly topics, and gradually guide the conversation.\n\n" +
                "The conversation flow should follow this structure:\n" +
                "1. Start with a light and friendly topic. (e.g., favorite color, food, games, or activities)\n" +
//                "2. Respond with empathy and continue naturally to discover the child's interests.\n" +
                "2. Do not end the conversation until you have clearly identified the child's interest (" + interest + ").\n" +
                "3. Once the interest is discovered, end the conversation naturally.\n\n" +
                "Important notes:\n" +
//                "- Always remember and refer to what the child said earlier in the conversation.\n" +
//                "- Be sensitive to the child’s emotions and always use empathetic language.\n" +
                "- Do not use emojis. Keep your responses short (2–3 sentences) and always in **Korean**.\n" +
                "- Do not force the conversation or make it awkward. Keep it smooth and natural.\n";


        // create a request
        ChatRequest request = new ChatRequest(model);
        List<Message> messages = request.getMessages();

        if(!records.isEmpty()){
            system += " 참고: 이전 대화 내용은 user와 assistant 역할에 따라 따로 제공된다. 이를 자연스럽게 이어받아야 한다.";
        }
        messages.add(new Message("system", system));

        for(int i= records.size()-1;i>=0;i--) {
            Record record = records.get(i);
            messages.add(record.getMessage());
            log.info(record.getMessage().getContent());
        }
        messages.add(new Message("user", prompt));

        // call the API
        ChatResponse response = restTemplate.postForObject(apiUrl, request, ChatResponse.class);

        if (response == null || response.getChoices() == null || response.getChoices().isEmpty()) {
            return "No response";
        }
        recordRepository.save(new Record(new Message("user", prompt),currentMember));//질문 저장
        recordRepository.save(new Record(response.getChoices().get(0).getMessage(),currentMember));//답변 저장
        return response.getChoices().get(0).getMessage().getContent();
    }
}
