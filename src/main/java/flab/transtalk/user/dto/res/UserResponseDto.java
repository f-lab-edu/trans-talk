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
public class UserResponseDto {
    Long id;
    String name;
    LocalDate birthDate;
    ProfileResponseDto profile;

    public static UserResponseDto from(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .name(user.getName())
                .birthDate(user.getBirthDate())
                .profile(
                        (user!=null?
                                ProfileResponseDto.builder()
                                    .id(user.getProfile().getId())
                                    .selfIntroduction(user.getProfile().getSelfIntroduction())
                                    .language(user.getProfile().getLanguage())
                                    .build() :
                                null
                        )
                )
                .build();
    }
}