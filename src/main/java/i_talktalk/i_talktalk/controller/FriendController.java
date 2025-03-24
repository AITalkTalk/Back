package i_talktalk.i_talktalk.controller;

import i_talktalk.i_talktalk.service.FriendService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class FriendController {
    private final FriendService friendService;

    @PostMapping("/friends/requests")
    public String requestFriend(@RequestParam String name){
        return friendService.requestFriend(name);
    }

    @GetMapping("/friends/requests")
    public List<String> showFriendRequests(){
        return friendService.showFriendRequests();
    }

    @PatchMapping("/friends/approve")
    public String approveFriend(@RequestParam String name){
        return friendService.approveFriend(name);
    }
}
