package flab.transtalk.user.dto.res;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PostResponseDto {
    Long id;
    String briefContext;
    String imagePresignedUrl;
}
