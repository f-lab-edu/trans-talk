package flab.transtalk.user.service.user;

import flab.transtalk.common.enums.LanguageSelection;
import flab.transtalk.user.domain.Profile;
import flab.transtalk.user.domain.User;
import flab.transtalk.user.dto.req.UserCreateRequestDto;
import flab.transtalk.user.dto.res.UserResponseDto;
import flab.transtalk.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserSignUpService {

    private final UserRepository userRepository;

    public UserResponseDto signUp(UserCreateRequestDto reqDto){
        User user = reqDto.toEntity();

        Profile profile = Profile.builder()
                .language(LanguageSelection.KOR)
                .selfIntroduction("")
                .build();

        user.setProfile(profile);
        User savedUser = userRepository.save(user);

        UserResponseDto resDto = UserResponseDto.from(savedUser);
        return resDto;
    }
}
