package flab.transtalk.user.dto.req;

import flab.transtalk.common.enums.LanguageSelection;
import flab.transtalk.user.domain.Profile;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDate;

@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ProfileCreateRequestDto {
    private String name;
    @Builder.Default
    private LocalDate birthDate = LocalDate.now();
    @Builder.Default
    private String selfIntroduction = "";
    @Builder.Default
    private LanguageSelection language = LanguageSelection.KOR;

    public Profile toEntity(){
        return Profile.builder()
                .name(this.name)
                .birthDate(this.birthDate)
                .selfIntroduction(this.selfIntroduction)
                .language(this.language)
                .build();
    }
}
