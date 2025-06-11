package flab.transtalk.user.service.profile;

import flab.transtalk.user.domain.Post;
import flab.transtalk.user.domain.Profile;
import flab.transtalk.user.dto.res.ProfileResponseDto;
import flab.transtalk.user.service.image.CloudFrontService;
import flab.transtalk.user.service.post.PostDtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProfileDtoMapper {
    private final CloudFrontService cloudFrontService;
    private final PostDtoMapper postDtoMapper;

    public ProfileResponseDto toDto(Profile profile){
        return ProfileResponseDto.builder()
                .id(profile.getId())
                .name(profile.getName())
                .birthDate(profile.getBirthDate())
                .selfIntroduction(profile.getSelfIntroduction())
                .language(profile.getLanguage())
                .imageKey(profile.getImageKey())
                .largeImageUrl(cloudFrontService.getLargeImageUrl(profile.getImageKey()))
                .smallImageUrl(cloudFrontService.getSmallImageUrl(profile.getImageKey()))
                .posts(profile.getPosts()!=null?
                        profile.getPosts().stream().map((Post post)->
                                postDtoMapper.toDto(post)
                        ).toList() :
                        List.of()
                ).build();
    }
}
