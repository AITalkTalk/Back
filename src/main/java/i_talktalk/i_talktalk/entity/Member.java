package i_talktalk.i_talktalk.entity;

import i_talktalk.i_talktalk.dto.SignUpDto;
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
    private Long age;

    @Column
    private Long point;

    @Column
    private String secret;

    @Column
    private String interest;


    public Member(String id, String password) {
        this.id = id;
        this.password = password;
        this.name=id;//null이면 unique 조건때문에 오류나서 일단 id로 초기화
        this.point= 0L;
        this.secret = "";
        this.interest = "";
    }

    public Member(SignUpDto signUpDto) {
        this.id = signUpDto.getId();
        this.password = signUpDto.getPassword();
        this.name= signUpDto.getName();
        this.age = signUpDto.getAge();
        this.point= 0L;
        this.secret = signUpDto.getSecret();
        this.interest = signUpDto.getInterest();
    }
}
