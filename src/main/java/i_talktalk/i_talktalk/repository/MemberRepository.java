package i_talktalk.i_talktalk.repository;

import i_talktalk.i_talktalk.dto.RetrieveMember;
import i_talktalk.i_talktalk.entity.Member;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    public Optional<Member> findById(String id);

    public Optional<Member> findByName(String name);

    public Optional<Member> findByMemberId(Long member_id);

    @Query("SELECT new i_talktalk.i_talktalk.dto.RetrieveMember(m.name, m.id, m.age) FROM Member m WHERE m.name = :name")
    List<RetrieveMember> findAllByName(@Param("name") String name);

}
