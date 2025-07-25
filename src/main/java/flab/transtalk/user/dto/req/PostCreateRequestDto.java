package flab.transtalk.user.dto.req;

import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Builder
public class PostCreateRequestDto {
    private String briefContext;
    private MultipartFile imageFile;
    private Long userId;
}
