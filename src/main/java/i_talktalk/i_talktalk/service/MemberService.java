package i_talktalk.i_talktalk.service;

import i_talktalk.i_talktalk.dto.JwtToken;
import i_talktalk.i_talktalk.entity.Member;
import i_talktalk.i_talktalk.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
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
}
