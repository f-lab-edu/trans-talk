package flab.transtalk.user.dto.res;

import flab.transtalk.common.enums.LanguageSelection;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ProfileResponseDto {
    private Long id;
    private String name;
    private LocalDate birthDate;
    private String selfIntroduction;
    private LanguageSelection language;
    private String imageKey;
    private String largeImageUrl;
    private String smallImageUrl;
    @Builder.Default
    private List<PostResponseDto> posts = new ArrayList<>();
}
