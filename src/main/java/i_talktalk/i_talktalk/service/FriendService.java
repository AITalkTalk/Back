package i_talktalk.i_talktalk.service;

import i_talktalk.i_talktalk.dto.CustomUserDetails;
import i_talktalk.i_talktalk.entity.Friend;
import i_talktalk.i_talktalk.entity.Member;
import i_talktalk.i_talktalk.repository.FriendRepository;
import i_talktalk.i_talktalk.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FriendService {
    private final FriendRepository friendRepository;
    private final MemberRepository memberRepository;


    //이미 친구 요청을 보냈다면 안보내는 기능 추가하기!
    public String requestFriend(String name) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Member currentMember = memberRepository.findById(userDetails.getUsername()).get();

        Optional<Member> found = memberRepository.findByName(name);
        if(!found.isPresent()){
            return "해당 이름의 유저가 없습니다.";
        }
        friendRepository.save(new Friend(currentMember, found.get()));
        return found.get().getName()+"님에게 친구 요청을 보냈습니다.";
    }
}
