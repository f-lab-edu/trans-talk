package flab.transtalk.user.dto.res;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class UserListResponseDto {
    private List<UserResponseDto> users;
}
