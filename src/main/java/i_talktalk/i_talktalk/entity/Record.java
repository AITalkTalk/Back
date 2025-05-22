package i_talktalk.i_talktalk.entity;

import i_talktalk.i_talktalk.dto.Message;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "records")
@Getter
@Setter
public class Record {
    @Id
    private String id;

    private Message message;

    private String sentiment;

    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    public Record(Message message, Member member) {
        this.message = message;
        this.member = member;
        this.sentiment=null;
        this.createdAt=LocalDateTime.now();
    }
}