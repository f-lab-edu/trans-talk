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
public class ProfileRequestDto {
    @Builder.Default
    String selfIntroduction = "";
    @Builder.Default
    LanguageSelection language = LanguageSelection.KOR;

    public Profile toEntity(){
        return Profile.builder()
                .selfIntroduction(this.selfIntroduction)
                .language(this.language)
                .build();
    }
}
