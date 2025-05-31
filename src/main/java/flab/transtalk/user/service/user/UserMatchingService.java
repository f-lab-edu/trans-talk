package flab.transtalk.user.service.user;

import flab.transtalk.user.domain.User;
import flab.transtalk.user.dto.res.UserResponseDto;
import flab.transtalk.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import flab.transtalk.config.ServiceConfigConstants;

@Service
@RequiredArgsConstructor
public class UserMatchingService {

    private final UserRepository userRepository;
    private static final Random RANDOM = new Random();
    public List<UserResponseDto> getUsersExcept(Long currentUserId) {
        long total = userRepository.countByIdNot(currentUserId);
        if (total==0l){
            return List.of();
        }
        int maxOffset = (int) Math.max(total - ServiceConfigConstants.MATCHING_USERS_MAX_NUMBER, 0);
        int randomOffset = RANDOM.nextInt(maxOffset + 1);
        PageRequest pageRequest = PageRequest.of(randomOffset, ServiceConfigConstants.MATCHING_USERS_MAX_NUMBER);
        List<User> users = userRepository.findAllExcept(currentUserId, pageRequest);
        if (users==null){
            return List.of();
        }
        return users.stream().map(user->UserResponseDto.from(user)).collect(Collectors.toList());
    }
}