package flab.transtalk.user.service.user;

import flab.transtalk.common.exception.NotFoundException;
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
            throw new NotFoundException("발견된 사용자가 없습니다.");
        }
        return UserResponseDto.from(user);
    }
}
