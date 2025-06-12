package flab.transtalk.user.dto.res;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PostResponseDto {
    private Long id;
    private String briefContext;
    private String imageKey;
    private String largeImageUrl;
    private String smallImageUrl;
}
