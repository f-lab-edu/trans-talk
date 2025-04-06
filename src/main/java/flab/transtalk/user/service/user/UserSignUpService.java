package flab.transtalk.user.service.user;

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

    public UserResponseDto signUp(UserCreateRequestDto dto){
        User user = userRepository.save(
                dto.toEntity()
        );
        UserResponseDto resDto = UserResponseDto.from(user);
        return resDto;
    }
}
