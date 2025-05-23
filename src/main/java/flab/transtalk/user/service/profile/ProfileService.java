package flab.transtalk.user.service.profile;

import flab.transtalk.common.exception.NotFoundException;
import flab.transtalk.common.exception.message.ExceptionMessages;
import flab.transtalk.user.domain.Profile;
import flab.transtalk.user.dto.req.ProfileUpdateRequestDto;
import flab.transtalk.user.dto.res.PostResponseDto;
import flab.transtalk.user.dto.res.ProfileResponseDto;
import flab.transtalk.user.repository.ProfileRepository;
import flab.transtalk.user.service.post.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final PostService postService;

    public ProfileResponseDto updateProfile(Long profileId, ProfileUpdateRequestDto dto){
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new NotFoundException(
                        ExceptionMessages.PROFILE_NOT_FOUND,
                        profileId.toString()
                ));

        if (dto.getSelfIntroduction() != null){
            profile.setSelfIntroduction(dto.getSelfIntroduction());
        }
        if (dto.getLanguage() != null){
            profile.setLanguage(dto.getLanguage());
        }

        Profile updated = profileRepository.save(profile);
        return ProfileResponseDto.from(updated);
    }

    @Transactional
    public ProfileResponseDto getProfile(Long profileId) {
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new NotFoundException(
                        ExceptionMessages.PROFILE_NOT_FOUND,
                        profileId.toString()
                ));

        ProfileResponseDto res = ProfileResponseDto.from(profile);
        List<PostResponseDto> posts = postService.getPosts(profile.getPosts());
        res.getPosts().addAll(posts);

        return res;
    }

}
