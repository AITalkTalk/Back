package i_talktalk.i_talktalk.repository;

import i_talktalk.i_talktalk.entity.Friend;
import i_talktalk.i_talktalk.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FriendRepository extends JpaRepository<Friend, Long> {
    List<Friend> findAllByMember2AndApprovedIsFalse(Member member2);

}
