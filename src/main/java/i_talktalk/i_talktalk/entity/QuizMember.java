package i_talktalk.i_talktalk.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "quiz_member")
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class QuizMember {

    @Id
    private String id;

    private String memberId;

    private String quizId;
}
