package i_talktalk.i_talktalk.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RetrieveMember {
    private String name;
    private String id;
    private Long age;
}
