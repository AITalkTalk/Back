package i_talktalk.i_talktalk.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SignUpDto {
    private String id;
    private String password;
    private String name;
    private Long age;
    private String secret;
    private String interest;

}
