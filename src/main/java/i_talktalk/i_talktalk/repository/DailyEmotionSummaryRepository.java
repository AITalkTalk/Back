package i_talktalk.i_talktalk.repository;

import i_talktalk.i_talktalk.entity.DailyEmotionSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DailyEmotionSummaryRepository extends JpaRepository<DailyEmotionSummary, Long> {

}
