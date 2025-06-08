package flab.transtalk.user.dto.req;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Value
@Builder
public class PostCreateRequestDto {
    String briefContext;
    MultipartFile imageFile;
    Long profileId;
}
