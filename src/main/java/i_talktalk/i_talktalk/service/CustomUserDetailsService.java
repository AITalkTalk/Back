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

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return memberRepository.findById(username)
                .map(this::createUserDetails)
                .orElseThrow(() -> new UsernameNotFoundException("해당하는 회원을 찾을 수 없습니다."));
    }

    // 해당하는 User 의 데이터가 존재한다면 UserDetails 객체로 만들어서 return
    private UserDetails createUserDetails(Member member) {
        return CustomUserDetails.builder()
                .member_id(member.getMember_id())
                .username(member.getId())
                .password(passwordEncoder.encode(member.getPassword()))
//                .roles(Arrays.asList(member.getRoles().toArray(new String[0]))) // 배열을 리스트로 변환
                .build();
    }

}