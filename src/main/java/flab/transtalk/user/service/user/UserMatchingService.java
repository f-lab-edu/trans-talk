package flab.transtalk.user.service.user;

import flab.transtalk.common.exception.BadRequestException;
import flab.transtalk.common.exception.NotFoundException;
import flab.transtalk.common.exception.message.ExceptionMessages;
import flab.transtalk.user.domain.User;
import flab.transtalk.user.domain.UserMatchStatus;
import flab.transtalk.user.dto.res.UserResponseDto;
import flab.transtalk.user.repository.UserMatchStatusRepository;
import flab.transtalk.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

import flab.transtalk.config.ServiceConfigConstants;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserMatchingService {

    private final UserRepository userRepository;
    private final UserMatchStatusRepository statusRepository;
    private static final Random RANDOM = new Random();

    @Transactional
    public List<UserResponseDto> getMatchResult(Long userId) {
        rechargeMatchRequestIfIntervalPassed(userId);
        consumeMatchRequest(userId);

        return getUsersExcept(userId);
    }

    @Transactional(readOnly = true)
    public List<UserResponseDto> getUsersExcept(Long currentUserId){
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
        return users.stream().map(UserResponseDto::from).toList();
    }

    @Transactional
    public void rechargeMatchRequestIfIntervalPassed(Long userId) {
        UserMatchStatus status = statusRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException(
                        ExceptionMessages.USER_MATCH_STATUS_NOT_FOUND,
                        "(userId) "+userId
                ));

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastRequested = status.getLastMatchRequestedAt();

        int hoursElapsed = (int) (
                Duration.between(lastRequested, now).toHours()
                / ServiceConfigConstants.MATCH_REQUEST_RECHARGE_INTERVAL_HOURS
        );
        if (hoursElapsed <= 0) return;

        int current = status.getRemainingMatchRequests();
        int max = ServiceConfigConstants.MAX_REMAINING_MATCH_REQUESTS;

        int refill = Math.min(hoursElapsed, max - current);
        if (refill <= 0) return;

        int updatedCount = current + refill;
        status.setRemainingMatchRequests(updatedCount);

        if (updatedCount >= max) {
            status.setLastMatchRequestedAt(now);
        } else {
            status.setLastMatchRequestedAt(lastRequested.plusHours(refill));
        }
    }

    @Transactional
    public void consumeMatchRequest(Long userId) {
        rechargeMatchRequestIfIntervalPassed(userId);

        UserMatchStatus status = statusRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException(
                        ExceptionMessages.USER_MATCH_STATUS_NOT_FOUND,
                        "(userId) "+userId
                ));

        int current = status.getRemainingMatchRequests();

        if (current <= 0) {
            throw new BadRequestException(
                    ExceptionMessages.MATCH_ATTEMPT_EXHAUSTED
            );
        }

        status.setRemainingMatchRequests(Math.max(current - 1, 0));
        if (current == ServiceConfigConstants.MAX_REMAINING_MATCH_REQUESTS) {
            status.setLastMatchRequestedAt(LocalDateTime.now());
        }
    }
}
