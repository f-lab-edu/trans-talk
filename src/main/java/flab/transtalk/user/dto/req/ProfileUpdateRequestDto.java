package flab.transtalk.user.dto.req;

import flab.transtalk.common.enums.LanguageSelection;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ProfileUpdateRequestDto {
    String name;
    LocalDate birthDate;
    String selfIntroduction;
    LanguageSelection language;
}
