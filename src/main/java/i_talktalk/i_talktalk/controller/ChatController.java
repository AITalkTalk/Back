package i_talktalk.i_talktalk.controller;

import i_talktalk.i_talktalk.dto.ChatRequest;
import i_talktalk.i_talktalk.dto.ChatResponse;
import i_talktalk.i_talktalk.dto.Message;
import i_talktalk.i_talktalk.dto.PromptDto;
import i_talktalk.i_talktalk.entity.Record;
import i_talktalk.i_talktalk.repository.RecordRepository;
import i_talktalk.i_talktalk.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ChatController {
    private final ChatService chatService;

    @PostMapping("/chat")
    public String chat(@RequestBody PromptDto promptDto) {
        return chatService.chat(promptDto.getPrompt());
    }


}