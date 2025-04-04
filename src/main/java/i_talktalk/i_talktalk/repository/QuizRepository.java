package i_talktalk.i_talktalk.repository;

import i_talktalk.i_talktalk.entity.Quiz;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface QuizRepository extends MongoRepository<Quiz, String> {

}
