package i_talktalk.i_talktalk.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class JwtToken {
    private String grantType; //Bearer
    private String accessToken;
    private String refreshToken;
}
