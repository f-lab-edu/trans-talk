package flab.transtalk.user.dto.res;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PostResponseDto {
    Long id;
    String briefContext;
    String imageKey;
    String imageUrl;
}
