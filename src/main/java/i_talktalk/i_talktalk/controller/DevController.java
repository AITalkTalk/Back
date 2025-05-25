package i_talktalk.i_talktalk.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import i_talktalk.i_talktalk.service.SentimentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/dev")
public class DevController {
    private final SentimentService sentimentService;

    @PostMapping("/analyze/today") //테스트용 api임
    public ResponseEntity<String> runTestAnalysis() throws JsonProcessingException {
        sentimentService.runDailySentimentAnalysis();
        return ResponseEntity.ok("분석 실행됨");
    }
}
