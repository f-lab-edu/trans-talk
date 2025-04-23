package flab.transtalk.user.dto.req;

import flab.transtalk.common.enums.LanguageSelection;
import flab.transtalk.user.domain.Profile;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ProfileUpdateRequestDto {
    Long id;
    String selfIntroduction;
    LanguageSelection language;

    public Profile toEntity(){
        return Profile.builder()
                .id(id)
                .selfIntroduction(selfIntroduction)
                .language(language)
                .build();
    }
}
