package flab.transtalk.user.dto.res;

import flab.transtalk.common.enums.LanguageSelection;
import flab.transtalk.user.domain.Profile;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO for {@link Profile}
 */
@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ProfileResponseDto {
    Long id;
    String selfIntroduction;
    LanguageSelection language;
    @Builder.Default
    List<PostResponseDto> posts = new ArrayList<>();

    public static ProfileResponseDto from(Profile entity){
        return ProfileResponseDto.builder()
                .id(entity.getId())
                .selfIntroduction(entity.getSelfIntroduction())
                .language(entity.getLanguage())
                .build();
    }
}