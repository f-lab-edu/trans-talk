package flab.transtalk.user.dto.req;

import flab.transtalk.common.enums.LanguageSelection;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ProfileUpdateRequestDto {
    String selfIntroduction;
    LanguageSelection language;
}
