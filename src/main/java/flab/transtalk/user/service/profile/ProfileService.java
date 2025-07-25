package flab.transtalk.user.service.profile;

import flab.transtalk.common.exception.BadRequestException;
import flab.transtalk.common.exception.NotFoundException;
import flab.transtalk.common.exception.message.ExceptionMessages;
import flab.transtalk.user.domain.Profile;
import flab.transtalk.user.dto.req.ProfileUpdateRequestDto;
import flab.transtalk.user.dto.res.ProfileResponseDto;
import flab.transtalk.user.repository.ProfileRepository;
import flab.transtalk.user.service.image.S3ImageService;
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
    private final S3ImageService s3ImageService;

    @Transactional
    public ProfileResponseDto updateProfile(Long userId, ProfileUpdateRequestDto dto){
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException(
                        ExceptionMessages.PROFILE_NOT_FOUND,
                        userId.toString()
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
    public ProfileResponseDto getProfile(Long userId) {
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException(
                        ExceptionMessages.PROFILE_NOT_FOUND,
                        userId.toString()
                ));

        return profileDtoMapper.toDto(profile);
    }

    @Transactional
    public ProfileResponseDto updateProfileImage(Long userId, MultipartFile imageFile) {
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException(
                        ExceptionMessages.PROFILE_NOT_FOUND,
                        userId.toString()
                ));
        String prevImageKey = profile.getImageKey();
        String generatedImageKey;
        try {
            generatedImageKey = s3ImageService.uploadProfileImageFile(imageFile, userId);
        } catch (IOException e) {
            throw new BadRequestException(ExceptionMessages.IMAGE_UPLOAD_FAILED);
        }
        profile.setImageKey(generatedImageKey);
        if (prevImageKey!=null){
            s3ImageService.deleteImageFile(prevImageKey);
        }

        return profileDtoMapper.toDto(profile);
    }
}
