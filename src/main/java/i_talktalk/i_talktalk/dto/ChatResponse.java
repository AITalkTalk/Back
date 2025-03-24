package i_talktalk.i_talktalk.dto;

import lombok.Data;
import lombok.Getter;

import java.util.List;

@Data
@Getter
public class ChatResponse {

    private List<Choice> choices;

    // constructors, getters and setters

    public static class Choice {

        private int index;
        private Message message;

        public Choice(int index, Message message) {
            this.index = index;
            this.message = message;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public Message getMessage() {
            return message;
        }

        public void setMessage(Message message) {
            this.message = message;
        }
        // constructors, getters and setters
    }
}