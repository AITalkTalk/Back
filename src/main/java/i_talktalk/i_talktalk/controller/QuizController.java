package i_talktalk.i_talktalk.controller;

import i_talktalk.i_talktalk.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;

    @GetMapping("/quiz/create")
    public String createQuiz() {
        return quizService.createQuiz();
    }


    //답안 제출 로직


}
