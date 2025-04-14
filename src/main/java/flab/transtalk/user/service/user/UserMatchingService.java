package flab.transtalk.user.service.user;

import flab.transtalk.common.exception.NotFoundException;
import flab.transtalk.common.exception.message.ExceptionMessages;
import flab.transtalk.user.domain.User;
import flab.transtalk.user.dto.res.UserResponseDto;
import flab.transtalk.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserMatchingService {

    private final UserRepository userRepository;

    public UserResponseDto getUserExcept(Long currentUserId) {
        User user = userRepository.findRandomUserExcept(currentUserId);
        if (user == null){
            throw new NotFoundException(
                    ExceptionMessages.NO_USER_FOUND,
                    currentUserId.toString(),
                    ""
            );
        }
        return UserResponseDto.from(user);
    }
}
