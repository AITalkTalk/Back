package i_talktalk.i_talktalk.controller;

import i_talktalk.i_talktalk.dto.ApiResponse;
import i_talktalk.i_talktalk.dto.DailyEmotionSummaryDto;
import i_talktalk.i_talktalk.service.DailyEmotionSummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
public class DailyEmotionSummaryController {
    private final DailyEmotionSummaryService dailyEmotionSummaryService;

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<DailyEmotionSummaryDto>> getDailyEmotionSummary(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        DailyEmotionSummaryDto dto = dailyEmotionSummaryService.getDailyEmotionSummary(date);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK,"감정 분석 조회 성공",dto));
    }
}
