package i_talktalk.i_talktalk.service;

import i_talktalk.i_talktalk.dto.CustomUserDetails;
import i_talktalk.i_talktalk.entity.Member;
import i_talktalk.i_talktalk.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    //이 함수로 username에 해당하는 사용자를 찾아서 인증객체를 만듬.
    //비밀번호 비교는 시큐리티가 내부적으로 getPassword를 통해서 비교함. 그래서 customuserdetails에 아이디, 비밀번호는 무조건 있어야 하고 getter를 설정해야 하는 이유임.
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return memberRepository.findById(username)
                .map(this::createUserDetails)
                .orElseThrow(() -> new UsernameNotFoundException("해당하는 회원을 찾을 수 없습니다."));
    }

    // 해당하는 User 의 데이터가 존재한다면 UserDetails 객체로 만들어서 return
    private UserDetails createUserDetails(Member member) {
        return CustomUserDetails.builder()
                .username(member.getId())//필수
                .member_id(member.getMemberId())//내가 추가한 것
                .name(member.getName())//내가 추가한 것
                .password(passwordEncoder.encode(member.getPassword()))//필수
//                .roles(Arrays.asList(member.getRoles().toArray(new String[0]))) // 배열을 리스트로 변환
                .build();
    }

}