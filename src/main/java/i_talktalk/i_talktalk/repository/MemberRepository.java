package i_talktalk.i_talktalk.repository;

import i_talktalk.i_talktalk.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    public Optional<Member> findById(String id);

    public Optional<Member> findByName(String name);

}
