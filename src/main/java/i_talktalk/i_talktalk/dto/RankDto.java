package i_talktalk.i_talktalk.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor // (String name, Long point) 생성자 자동 생성
@NoArgsConstructor  // 기본 생성자 자동 생성
@Data
public class RankDto {
    private String name;
    private Long point;

}
