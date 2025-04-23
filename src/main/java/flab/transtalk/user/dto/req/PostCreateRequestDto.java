package flab.transtalk.user.dto.req;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PostCreateRequestDto {
    private String briefContext;
    private MultipartFile imageFile;
    private Long profileId;
}
