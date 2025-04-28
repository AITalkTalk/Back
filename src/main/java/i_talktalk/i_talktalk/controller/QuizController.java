package i_talktalk.i_talktalk.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import i_talktalk.i_talktalk.entity.Quiz;
import i_talktalk.i_talktalk.service.QuizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;

@RestController
@RequiredArgsConstructor
@Tag(name = "퀴즈 API", description = "퀴즈 생성, 조회 API")
public class QuizController {

    private final QuizService quizService;

    //퀴즈를 생성하는 코드 (사용자가 호출하는거 아님)
    @PostMapping("/quiz/create")
    @Operation(summary = "퀴즈 생성 API", description = "프롬프트를 통해 퀴즈 뭉치 생성")
    public String createQuiz() {
        return quizService.createQuiz();
    }

    //사용자가 풀지 않은 문제중 하나 출제하는 함수
    @GetMapping("/quiz")
    @Operation(summary = "퀴즈 조회 API", description = "사용자가 풀지 않은 문제 조회")
    public ResponseEntity<Quiz> getQuiz() throws JsonProcessingException {
//        Quiz quiz = quizService.getNotSolvedQuiz();
        Quiz quiz = quizService.getNextQuiz();
        if(quiz==null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(quiz);
    }

    //정답처리를 하는 함수 (이후에 출제 안됨.)
    @PatchMapping("/quiz/solve")
    @Operation(summary = "퀴즈 정답 처리 API", description = "퀴즈를 맞히면 풀이 처리")
    public ResponseEntity<Void> solveQuiz(@RequestParam String quizId) throws JsonProcessingException {
//        quizService.solve(quizId);
        quizService.solve2(quizId);
        return ResponseEntity.ok().build();
    }




}
