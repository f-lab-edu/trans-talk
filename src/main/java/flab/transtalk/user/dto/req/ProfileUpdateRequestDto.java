package flab.transtalk.user.dto.req;

import flab.transtalk.common.enums.LanguageSelection;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ProfileUpdateRequestDto {
    private String name;
    private LocalDate birthDate;
    private String selfIntroduction;
    private LanguageSelection language;
}
