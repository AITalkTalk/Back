package i_talktalk.i_talktalk.dto;

import lombok.Data;

@Data
public class AnalyzedMessage {
    private String id;
    private String message;
    private String sentiment;

}