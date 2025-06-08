package flab.transtalk.user.dto.res;

import flab.transtalk.common.enums.LanguageSelection;
import flab.transtalk.user.domain.Profile;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO for {@link Profile}
 */
@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ProfileResponseDto {
    Long id;
    String name;
    LocalDate birthDate;
    String selfIntroduction;
    LanguageSelection language;
    String imageKey;
    String imageUrl;
    @Builder.Default
    List<PostResponseDto> posts = new ArrayList<>();
}
