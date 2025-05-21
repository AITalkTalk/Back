package i_talktalk.i_talktalk.dto;

import lombok.Data;

@Data
public class MemberInfoDto {
    private String name;
    private Long age;
    private Long point;
    private String secret;
    private String interest;
}
