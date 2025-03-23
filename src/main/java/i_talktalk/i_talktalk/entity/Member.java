package i_talktalk.i_talktalk.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Member extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long member_id;

    @Column(nullable = false)
    private String id;

    @Column(nullable = false)
    private String password;

    @Column(unique = true)
    private String name;

    @Column
    private Long point;

    @Column
    private String secret;

    @Column
    private String interest;


    public Member(String id, String password) {
        this.id = id;
        this.password = password;
        this.name="";
        this.point= 0L;
        this.secret = "";
        this.interest = "";
    }
}
