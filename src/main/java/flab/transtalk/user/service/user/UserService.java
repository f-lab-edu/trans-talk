package flab.transtalk.user.service.user;

import flab.transtalk.common.exception.NotFoundException;
import flab.transtalk.common.exception.message.ExceptionMessages;
import flab.transtalk.user.domain.User;
import flab.transtalk.user.dto.req.UserCreateRequestDto;
import flab.transtalk.user.dto.res.UserResponseDto;
import flab.transtalk.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserSignUpService userSignUpService;
    private final UserMatchingService userMatchingService;

    public UserResponseDto signUp(UserCreateRequestDto dto){
        return userSignUpService.signUp(dto);
    }

    public UserResponseDto getUserExcept(Long currentUserId) {
        return userMatchingService.getUserExcept(currentUserId);
    }

    public UserResponseDto getUser(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()){
            throw new NotFoundException(ExceptionMessages.USER_NOT_FOUND);
        }
        return UserResponseDto.from(user.get());
    }
}
