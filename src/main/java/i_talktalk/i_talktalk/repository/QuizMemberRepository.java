package i_talktalk.i_talktalk.repository;

import i_talktalk.i_talktalk.entity.QuizMember;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface QuizMemberRepository extends MongoRepository<QuizMember, String> {
    public List<QuizMember> findAllByMemberId(Long memberId);
}
