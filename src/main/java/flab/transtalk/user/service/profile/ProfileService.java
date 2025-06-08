package flab.transtalk.user.service.profile;

import flab.transtalk.common.exception.BadRequestException;
import flab.transtalk.common.exception.NotFoundException;
import flab.transtalk.common.exception.message.ExceptionMessages;
import flab.transtalk.user.domain.Profile;
import flab.transtalk.user.dto.req.ProfileUpdateRequestDto;
import flab.transtalk.user.dto.res.ProfileResponseDto;
import flab.transtalk.user.repository.ProfileRepository;
import flab.transtalk.user.service.image.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final ProfileDtoMapper profileDtoMapper;
    private final S3Service s3Service;

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

    @Transactional
    public ProfileResponseDto updateProfileImage(Long profileId, MultipartFile imageFile) {
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new NotFoundException(
                        ExceptionMessages.PROFILE_NOT_FOUND,
                        profileId.toString()
                ));
        Long userId = profile.getUser().getId();
        String prevImageKey = profile.getImageKey();
        String generatedImageKey;
        try {
            generatedImageKey = s3Service.uploadProfileImageFile(imageFile, userId);
        } catch (IOException e) {
            throw new BadRequestException(ExceptionMessages.IMAGE_UPLOAD_FAILED);
        }
        profile.setImageKey(generatedImageKey);
        if (prevImageKey!=null){
            s3Service.deleteImageFile(prevImageKey);
        }

        return profileDtoMapper.toDto(profile);
    }
}
