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

        String system = "너는 어린아이들을 상담하는 AI야. 이름은 assistant야. " +
                "너의 목표는 아이들이 편안하게 이야기할 수 있도록 유도하는 거야. " +
                "아이들이 자연스럽게 대화를 이어갈 수 있도록, 직접적인 질문보다 부드러운 방식으로 접근해야 해. " +
                "특히 다음 두 가지 정보를 반드시 알아내야 해:\n" +
                "1. 오늘 하루는 어땠는지\n" +
                "2. 오늘 학교에서 어떤 일이 있었는지\n\n" +
                "하지만 아이들에게 너무 직접적으로 묻지 말고, 먼저 그들이 관심 가질만한 주제로 대화를 시작한 후, " +
                "자연스럽게 질문을 이어가야 해. 또한, 아이들의 감정과 반응을 잘 반영해서 공감해줘야 해. \n\n" +
                "이전 대화 내용도 참고해서 아이가 한 말을 기억하고, 적절한 반응을 해 줘. 그리고 알아내야 할 내용을 전부 알게되면 자연스럽게 대화를 마무리해.";

        // create a request
        ChatRequest request = new ChatRequest(model);
        List<Message> messages = request.getMessages();

        if(!records.isEmpty()){
            system+=" 다음 내용은 최근의 대화 내용이야. 맥락을 반영해서 대답해 줘.";
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
