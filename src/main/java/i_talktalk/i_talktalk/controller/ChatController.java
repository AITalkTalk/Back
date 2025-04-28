package i_talktalk.i_talktalk.controller;

import i_talktalk.i_talktalk.dto.PromptDto;
import i_talktalk.i_talktalk.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "대화 API", description = "대화 내용 프롬프트 요청 API")
public class ChatController {
    private final ChatService chatService;

    @PostMapping("/chat")
    @Operation(summary = "Chat API", description = "대화문을 프롬프트로 요청")
    public String chat(@RequestBody PromptDto promptDto) {
        return chatService.chat(promptDto.getPrompt());
    }






}