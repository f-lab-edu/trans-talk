package flab.transtalk.user.service.user;

import flab.transtalk.user.domain.Profile;
import flab.transtalk.user.domain.User;
import flab.transtalk.user.domain.UserMatchStatus;
import flab.transtalk.user.dto.req.ProfileCreateRequestDto;
import flab.transtalk.user.dto.req.UserCreateRequestDto;
import flab.transtalk.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserSignUpService {

    private final UserRepository userRepository;

    public Long signUp(UserCreateRequestDto reqUserDto, ProfileCreateRequestDto reqProfileDto){
        User user = reqUserDto.toEntity();
        Profile profile = reqProfileDto.toEntity();
        UserMatchStatus status = UserMatchStatus.builder().user(user).build();

        user.setProfile(profile);
        user.setUserMatchStatus(status);
        User savedUser = userRepository.save(user);

        return savedUser.getId();
    }
}
