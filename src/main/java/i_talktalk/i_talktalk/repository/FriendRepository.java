package i_talktalk.i_talktalk.repository;

import i_talktalk.i_talktalk.entity.Friend;
import i_talktalk.i_talktalk.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FriendRepository extends JpaRepository<Friend, Long> {
    List<Friend> findAllByMember2AndApprovedIsFalse(Member member2);

    Optional<Friend> findByMember2AndMember1(Member member2, Member member1);

    boolean existsByMember1AndMember2AndApprovedFalse(Member member1, Member member2);   //친구 요청을 이미 한 것인지 확인

}
