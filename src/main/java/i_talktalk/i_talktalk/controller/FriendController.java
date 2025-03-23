package i_talktalk.i_talktalk.controller;

import i_talktalk.i_talktalk.service.FriendService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class FriendController {
    private final FriendService friendService;

    @PostMapping("/requestfriend")
    public String requestFriend(@RequestParam String name){
        return friendService.requestFriend(name);
    }
}
