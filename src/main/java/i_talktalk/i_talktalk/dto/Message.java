package i_talktalk.i_talktalk.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Message {

    private String role;
    private String content;

    // constructor, getters and setters
}