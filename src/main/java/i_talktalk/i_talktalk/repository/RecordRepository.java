package i_talktalk.i_talktalk.repository;

import i_talktalk.i_talktalk.entity.Record;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface RecordRepository extends MongoRepository<Record, String> {
    List<Record> findTop4ByOrderByIdDesc();

    List<Record> findTop6ByOrderByIdDesc();

    List<Record> findByMember_IdAndCreatedAtBetween(String loginId, LocalDateTime start, LocalDateTime end);

}
