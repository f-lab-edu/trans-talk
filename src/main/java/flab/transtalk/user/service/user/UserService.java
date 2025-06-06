package flab.transtalk.user.service.user;

import flab.transtalk.common.exception.NotFoundException;
import flab.transtalk.common.exception.message.ExceptionMessages;
import flab.transtalk.user.domain.User;
import flab.transtalk.user.dto.req.ProfileCreateRequestDto;
import flab.transtalk.user.dto.req.UserCreateRequestDto;
import flab.transtalk.user.dto.res.UserResponseDto;
import flab.transtalk.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserSignUpService userSignUpService;
    private final UserMatchingService userMatchingService;

    public Long signUp(UserCreateRequestDto reqUserDto, ProfileCreateRequestDto reqProfileDto){
        return userSignUpService.signUp(reqUserDto, reqProfileDto);
    }

    public List<UserResponseDto> getUsersExcept(Long currentUserId) {
        return userMatchingService.getUsersExcept(currentUserId);
    }

    public UserResponseDto getUser(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()){
            throw new NotFoundException(
                    ExceptionMessages.USER_NOT_FOUND,
                    userId.toString()
            );
        }
        return UserResponseDto.from(user.get());
    }
}
