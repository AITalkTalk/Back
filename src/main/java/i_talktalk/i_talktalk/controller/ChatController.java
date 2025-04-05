package i_talktalk.i_talktalk.controller;

import i_talktalk.i_talktalk.dto.PromptDto;
import i_talktalk.i_talktalk.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

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