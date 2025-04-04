package flab.transtalk.user.dto.res;

import flab.transtalk.user.domain.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * DTO for {@link flab.transtalk.user.domain.User}
 */
@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserResponseDto implements Serializable {
    Long id;
    String name;
    LocalDate birthDate;

    public static UserResponseDto from(User dto) {
        return UserResponseDto.builder()
                .id(dto.getId())
                .name(dto.getName())
                .birthDate(dto.getBirthDate())
                .build();
    }
}