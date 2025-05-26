package i_talktalk.i_talktalk.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DailyEmotionSummary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    private String memberId;

    private LocalDate date;

    private String sentiment;

    @Column(columnDefinition = "TEXT")
    private String chat;


}
