package i_talktalk.i_talktalk.service;

import i_talktalk.i_talktalk.dto.CustomUserDetails;
import i_talktalk.i_talktalk.dto.JwtToken;
import i_talktalk.i_talktalk.entity.Member;
import i_talktalk.i_talktalk.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {
    private final MemberRepository memberRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;
    @Transactional
    public String signUp(String id, String password){
        Optional<Member> user = memberRepository.findById(id);
        if(user.isPresent()){
            return "already exist"; //상태코드 반환하도록 수정해야함.
        }
        Member member = new Member(id, password);
        memberRepository.save(member);
        return "sign up";
    }

    @Transactional
    public JwtToken signIn(String id, String password){
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(id, password);
        Authentication authentication;
        try{
            //loadUserByUsername 호출
            authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        }catch (BadCredentialsException e){
            e.printStackTrace();
            return null;
        }
        if(!authentication.isAuthenticated()){
            return null;
        }
        JwtToken jwtToken = jwtTokenProvider.generateToken(authentication);
        return jwtToken;
    }

    @Transactional
    public String changeInfo(String name, Long age, String secret, String interest){
        Optional<Member> found = memberRepository.findByName(name);
        if(found.isPresent()){
            return "이미 있는 사용자 이름입니다.";
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Member currentMember = memberRepository.findById(userDetails.getUsername()).get();
        log.info("사용자 아이디: "+currentMember.getId());
        currentMember.setName(name);
        currentMember.setAge(age);
        currentMember.setSecret(secret);
        currentMember.setInterest(interest);
        return "회원정보 수정 완료!";
    }
}
