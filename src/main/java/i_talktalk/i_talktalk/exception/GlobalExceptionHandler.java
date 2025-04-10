package i_talktalk.i_talktalk.exception;

import i_talktalk.i_talktalk.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//컨트롤러 단위로 매번 try-catch를 쓰는 대신, 모든 예외를 한 곳에서 처리하도록 해주는 중앙 예외 처리기
@RestControllerAdvice   //모든 컨트롤러의 예외를 가로채서 하나의 클래스에서 통합 처리
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(IllegalArgumentException e) {
        return ResponseEntity.badRequest()
                .body(new ApiResponse<>(HttpStatus.BAD_REQUEST, e.getMessage(), null));
    }

    @ExceptionHandler(Exception.class) // 수정해야 할듯
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다.", null));
    }

    @ExceptionHandler(MemberNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleMemberNotFound(MemberNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>(HttpStatus.NOT_FOUND, ex.getMessage(), null));
    }
}
