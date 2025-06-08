package flab.transtalk.user.service.post;

import flab.transtalk.user.domain.Post;
import flab.transtalk.user.dto.res.PostResponseDto;
import flab.transtalk.user.service.image.CloudFrontService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostDtoMapper {
    private final CloudFrontService cloudFrontService;

    public PostResponseDto toDto(Post post){
        return PostResponseDto.builder()
                .id(post.getId())
                .briefContext(post.getBriefContext())
                .imageKey(post.getImageKey())
                .imageUrl(cloudFrontService.getImageUrl(post.getImageKey()))
                .build();
    }
}
