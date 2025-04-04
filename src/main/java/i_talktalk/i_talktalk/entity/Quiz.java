package i_talktalk.i_talktalk.entity;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "quiz")
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Quiz {
    @Id
    private String id;

    private String question;
    private List<String> choices;
    private String answer;
    private String category;
}
