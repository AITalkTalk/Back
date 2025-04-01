package i_talktalk.i_talktalk.controller;

import i_talktalk.i_talktalk.service.FriendService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "친구 API", description = "친구 요청, 친구 리스트 확인, 친구 수락 관련 API")
public class FriendController {
    private final FriendService friendService;

    @PostMapping("/friends/requests")
    @Operation(summary = "친구 요청 API", description = "친구 이름을 통해 요청")
    public String requestFriend(@RequestParam String name){
        return friendService.requestFriend(name);
    }

    @GetMapping("/friends/requests")
    @Operation(summary = "친구 리스트 확인 API", description = "나의 친구 목록 확인")
    public List<String> showFriendRequests(){
        return friendService.showFriendRequests();
    }

    @PatchMapping("/friends/approve")
    @Operation(summary = "친구 수락 API", description = "친구 이름을 통해 친구 추가 수락")
    public String approveFriend(@RequestParam String name){
        return friendService.approveFriend(name);
    }
}
