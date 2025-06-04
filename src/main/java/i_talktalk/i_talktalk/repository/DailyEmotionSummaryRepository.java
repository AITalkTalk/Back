package i_talktalk.i_talktalk.repository;

import i_talktalk.i_talktalk.entity.DailyEmotionSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DailyEmotionSummaryRepository extends JpaRepository<DailyEmotionSummary, Long> {
    Optional<DailyEmotionSummary> findByNameAndDate(String name, LocalDate date);
    List<DailyEmotionSummary> findAllByNameAndDateBetween(String name, LocalDate startDate, LocalDate endDate);

}
