package i_talktalk.i_talktalk.service;

import i_talktalk.i_talktalk.entity.Member;
import i_talktalk.i_talktalk.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

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
    public String signIn(String id, String password){
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(id, password);
        Authentication authentication;
        try{
            //loadUserByUsername 호출
            authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        }catch (BadCredentialsException e){
            e.printStackTrace();
            return "로그인 실패2";
        }
        if(!authentication.isAuthenticated()){
            return "로그인 실패1";
        }
        return "로그인 성공";
    }
}
