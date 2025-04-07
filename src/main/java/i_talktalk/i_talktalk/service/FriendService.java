package i_talktalk.i_talktalk.service;

import i_talktalk.i_talktalk.dto.CustomUserDetails;
import i_talktalk.i_talktalk.entity.Friend;
import i_talktalk.i_talktalk.entity.Member;
import i_talktalk.i_talktalk.repository.FriendRepository;
import i_talktalk.i_talktalk.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class FriendService {
    private final FriendRepository friendRepository;
    private final MemberRepository memberRepository;


    //이미 친구 요청을 보냈다면 안보내는 기능 추가하기!
    public String requestFriend(String name) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Member currentMember = memberRepository.findById(userDetails.getUsername()).get();

        Optional<Member> found = memberRepository.findByName(name);
        if (!found.isPresent()) {
            throw new IllegalArgumentException("해당 이름의 유저가 없습니다.");
        }

        // 본인한테 요청 방지
        if (found.get().getId().equals(currentMember.getId())) {
            throw new IllegalArgumentException("자기 자신에게는 친구 요청을 보낼 수 없습니다.");
        }

        // 이미 요청 보냈는지 확인 (승인되지 않은 요청 포함)
        boolean alreadyRequested = friendRepository.existsByMember1AndMember2(currentMember, found.get()) || friendRepository.existsByMember1AndMember2(found.get(), currentMember);//approvefalse 제거 필요
        if (alreadyRequested) {
            throw new IllegalArgumentException("이미 친구 요청을 보낸 사용자입니다.");
        }

        friendRepository.save(new Friend(currentMember, found.get()));
        return found.get().getName() + "님에게 친구 요청을 보냈습니다.";
    }

    public List<String> showFriendRequests() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Member currentMember = memberRepository.findById(userDetails.getUsername()).get();

        List<Friend> found = friendRepository.findAllByMember2AndApprovedIsFalse(currentMember);
        if(found.isEmpty()){
            log.info("친구 요청이 없습니다.");
            LinkedList<String> friendRequests = new LinkedList<>();
            String s = "친구 요청이 없습니다";
            friendRequests.add(s);
            return friendRequests;
        }

        List<String> friendRequests = found.stream().map(friend -> friend.getMember1().getName()).toList();
        return friendRequests;
    }

    public String approveFriend(String name) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Member currentMember = memberRepository.findById(userDetails.getUsername()).get();
        Optional<Member> requestedMember = memberRepository.findByName(name);
        if(!requestedMember.isPresent()){
            return "해당 유저는 친구 요청을 보내지 않았습니다.";
        }

        Optional<Friend> foundFriend = friendRepository.findByMember2AndMember1(currentMember, requestedMember.get());
        if(!foundFriend.isPresent()){
            return "친구 요청이 없습니다.";
        }
        foundFriend.get().setApproved(true);
        return "친구 수락 완료!";
    }

//    public String deleteFriend(String name) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
//        Member currentMember = memberRepository.findById(userDetails.getUsername()).get();
//
//        Optional<Member> friend = memberRepository.findByName(name);
//
//        if(!friend.isPresent()){
//            return "없는 회원입니다.";
//        }
//
//        Optional<Friend> foundFriend = friendRepository.findByMember2AndMember1(currentMember, friend.get());
//        if(!foundFriend.isPresent()){
//            foundFriend = friendRepository.findByMember2AndMember1(friend.get(), currentMember);
//            if(foundFriend.isPresent()){
//                friendRepository.delete(foundFriend.get());
//                return "친구 삭제 완료";
//            }else{
//                return "친구가 아닙니다.";
//            }
//        }else{
//            friendRepository.delete(foundFriend.get());
//            return "친구 삭제 완료";
//        }
//    }
}
