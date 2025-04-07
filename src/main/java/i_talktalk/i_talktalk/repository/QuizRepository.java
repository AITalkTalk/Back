package i_talktalk.i_talktalk.repository;

import i_talktalk.i_talktalk.entity.Quiz;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Set;

public interface QuizRepository extends MongoRepository<Quiz, String> {
    public List<Quiz> findAllByIdNotIn(Set<String> id);
}
