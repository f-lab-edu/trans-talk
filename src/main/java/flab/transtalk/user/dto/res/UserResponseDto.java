package flab.transtalk.user.dto.res;

import flab.transtalk.user.domain.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

/**
 * DTO for {@link flab.transtalk.user.domain.User}
 */
@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserResponseDto {
    Long id;
    String email;
    ProfileResponseDto profile;

    public static UserResponseDto from(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .profile(
                        (user!=null?
                                ProfileResponseDto.builder()
                                    .id(user.getProfile().getId())
                                    .name(user.getProfile().getName())
                                    .birthDate(user.getProfile().getBirthDate())
                                    .selfIntroduction(user.getProfile().getSelfIntroduction())
                                    .language(user.getProfile().getLanguage())
                                    .build() :
                                null
                        )
                )
                .build();
    }
}