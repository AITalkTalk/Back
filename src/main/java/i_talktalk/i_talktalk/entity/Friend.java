package i_talktalk.i_talktalk.entity;

import jakarta.persistence.*;

@Entity
public class Friend {
    @Id
    @GeneratedValue
    private Long friend_id;

    @ManyToOne
    @JoinColumn(name = "member1")
    private Member member1;

    @ManyToOne
    @JoinColumn(name = "member2")
    private Member member2;

    @Column
    private boolean approved;

}
