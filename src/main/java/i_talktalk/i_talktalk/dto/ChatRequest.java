package i_talktalk.i_talktalk.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ChatRequest {

    private String model;
    private List<Message> messages;
    private int max_tokens;
    private double temperature;
//    private int n=1;
//    private double temperature;

    public ChatRequest(String model) {
        this.model = model;
        this.max_tokens=160;
        this.messages = new ArrayList<>();
        this.temperature = 0.5;
    }

    // getters and setters
}