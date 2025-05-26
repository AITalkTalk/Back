package i_talktalk.i_talktalk.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class DailyEmotionSummaryDto {
    public Long id;

    private String name;//사용자 닉네임

    private LocalDate date;

    private String sentiment;

    @Column(columnDefinition = "TEXT")
    private String chat;
}
