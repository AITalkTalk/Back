package i_talktalk.i_talktalk.controller;

import i_talktalk.i_talktalk.dto.ApiResponse;
import i_talktalk.i_talktalk.service.FriendService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "친구 API", description = "친구 요청, 친구 리스트 확인, 친구 수락 관련 API")
public class FriendController {
    private final FriendService friendService;

    @PostMapping("/friends/requests")
    @Operation(summary = "친구 요청 API", description = "친구 이름을 통해 요청")
    public ResponseEntity<ApiResponse<Void>> requestFriend(@RequestParam String name) {
        friendService.requestFriend(name);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse<>(HttpStatus.CREATED, "친구 요청이 완료되었습니다.", null));
    }

    @GetMapping("/friends/requests")
    @Operation(summary = "친구 요청 확인 API", description = "나의 친구 요청 목록 확인")
    public ResponseEntity<ApiResponse<List<String>>> showFriendRequests() {
        List<String> friends = friendService.showFriendRequests();
        return ResponseEntity
                .ok(new ApiResponse<>(HttpStatus.OK, "친구 요청 목록 조회 성공", friends));
    }

    @PatchMapping("/friends/approve")
    @Operation(summary = "친구 수락 API", description = "친구 이름을 통해 친구 추가 수락")
    public ResponseEntity<ApiResponse<Void>> approveFriend(@RequestParam String name) {
        friendService.approveFriend(name);
        return ResponseEntity
                .ok(new ApiResponse<>(HttpStatus.OK, "친구 요청을 수락했습니다.", null));
    }

    @GetMapping("/friends")
    @Operation(summary = "친구 목록 확인 API", description = "나의 친구 목록 확인")
    public ResponseEntity<ApiResponse<List<String>>> showFriends() {
        List<String> friends = friendService.showFriends();
        return ResponseEntity
                .ok(new ApiResponse<>(HttpStatus.OK, "친구 목록 조회 성공", friends));
    }

    @DeleteMapping("/friends")
    @Operation(summary = "친구 삭제 API", description = "파라미터로 전달받은 친구 이름을 통해 삭제")
    public ResponseEntity<ApiResponse<Void>> deleteFriend(@RequestParam String name) {
        friendService.deleteFriend(name);
        return ResponseEntity
                .ok(new ApiResponse<>(HttpStatus.OK, "친구를 삭제했습니다.", null));
    }
}
