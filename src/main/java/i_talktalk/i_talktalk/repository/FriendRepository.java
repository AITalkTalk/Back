package i_talktalk.i_talktalk.repository;

import i_talktalk.i_talktalk.dto.RankDto;
import i_talktalk.i_talktalk.entity.Friend;
import i_talktalk.i_talktalk.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FriendRepository extends JpaRepository<Friend, Long> {
    List<Friend> findAllByMember2AndApprovedIsFalse(Member member2);

    Optional<Friend> findByMember2AndMember1(Member member2, Member member1);

    boolean existsByMember1AndMember2(Member member1, Member member2);   //친구 요청을 이미 한 것인지 확인

    @Query("select f from Friend f where f.approved = true and (f.member1 = :member or f.member2 = :member)")
    List<Friend> findAllApprovedByMember(Member member);

    @Query("""
    select new i_talktalk.i_talktalk.dto.RankDto(m.name, m.point)
    from Member m 
    where m in (
        select f.member1 from Friend f where f.member2 = :member
    ) 
    or m in (
        select f.member2 from Friend f where f.member1 = :member
    )
    or m = :member
    order by m.point desc
    """)
    List<RankDto> findAllApprovedByMemberForRank(Member member);
}
