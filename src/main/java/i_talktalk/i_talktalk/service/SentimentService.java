package i_talktalk.i_talktalk.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import i_talktalk.i_talktalk.entity.Member;
import i_talktalk.i_talktalk.repository.DailyEmotionSummaryRepository;
import i_talktalk.i_talktalk.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SentimentService {
    private final MemberRepository memberRepository;
    private final DailyEmotionSummaryService dailyEmotionSummaryService;

    @Scheduled(cron = "0 0 0 * * *") // 매일 00:00:00
    @Transactional
    public void runDailySentimentAnalysis() throws JsonProcessingException {
        LocalDate targetDate = LocalDate.now().minusDays(1);
        LocalDateTime start = targetDate.atStartOfDay();
        LocalDateTime end = targetDate.plusDays(1).atStartOfDay();


        List<Member> members = memberRepository.findAll();

        for (Member member : members) {
            dailyEmotionSummaryService.analyzeAndStore(member.getId(), start, end);
        }


    }
}
