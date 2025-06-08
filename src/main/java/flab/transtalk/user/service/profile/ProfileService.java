package flab.transtalk.user.service.profile;

import flab.transtalk.common.exception.NotFoundException;
import flab.transtalk.common.exception.message.ExceptionMessages;
import flab.transtalk.user.domain.Profile;
import flab.transtalk.user.dto.req.ProfileUpdateRequestDto;
import flab.transtalk.user.dto.res.ProfileResponseDto;
import flab.transtalk.user.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final ProfileDtoMapper profileDtoMapper;

    @Transactional
    public ProfileResponseDto updateProfile(Long profileId, ProfileUpdateRequestDto dto){
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new NotFoundException(
                        ExceptionMessages.PROFILE_NOT_FOUND,
                        profileId.toString()
                ));

        if (dto.getName() != null){
            profile.setName(dto.getName());
        }
        if (dto.getBirthDate() != null){
            profile.setBirthDate(dto.getBirthDate());
        }
        if (dto.getSelfIntroduction() != null){
            profile.setSelfIntroduction(dto.getSelfIntroduction());
        }
        if (dto.getLanguage() != null){
            profile.setLanguage(dto.getLanguage());
        }

        return profileDtoMapper.toDto(profile);
    }

    @Transactional(readOnly = true)
    public ProfileResponseDto getProfile(Long profileId) {
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new NotFoundException(
                        ExceptionMessages.PROFILE_NOT_FOUND,
                        profileId.toString()
                ));

        return profileDtoMapper.toDto(profile);
    }
}
