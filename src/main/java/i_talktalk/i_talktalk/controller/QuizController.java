package i_talktalk.i_talktalk.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import i_talktalk.i_talktalk.entity.Quiz;
import i_talktalk.i_talktalk.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;

@RestController
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;

    //퀴즈를 생성하는 코드 (사용자가 호출하는거 아님)
    @PostMapping("/quiz/create")
    public String createQuiz() {
        return quizService.createQuiz();
    }

    //사용자가 풀지 않은 문제중 하나 출제하는 함수
    @GetMapping("/quiz")
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
    public ResponseEntity<Void> solveQuiz(@RequestParam String quizId) throws JsonProcessingException {
//        quizService.solve(quizId);
        quizService.solve2(quizId);
        return ResponseEntity.ok().build();
    }




}
