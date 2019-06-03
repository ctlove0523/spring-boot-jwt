package murraco.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserLoginResponseDTO {
    private String accessToken;
    private Integer expireTime;
}
