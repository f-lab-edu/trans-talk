package flab.transtalk.user.service.user;


import flab.transtalk.common.enums.LanguageSelection;
import flab.transtalk.user.domain.Profile;
import flab.transtalk.user.domain.User;
import flab.transtalk.user.dto.res.UserResponseDto;
import flab.transtalk.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
@DisplayName("사용자 매칭 테스트")
public class UserMatchingServiceTest {

    @InjectMocks
    private UserMatchingService userMatchingService;

    @Mock
    private UserRepository userRepository;

    @Test
    @DisplayName("매칭 정상 조회 테스트")
    void  testGetUserExcept_1(){
        // given
        Long currentUserId = 1L;
        User user = User.builder()
                .id(2L)
                .name("사용자2")
                .birthDate(LocalDate.of(1999, 1, 1))
                .profile(Profile.builder()
                        .id(2L)
                        .language(LanguageSelection.KOR)
                        .selfIntroduction("")
                        .build())
                .build();
        BDDMockito.given(userRepository.findAllExcept(eq(currentUserId), any(Pageable.class))).willReturn(List.of(user));

        // when
        List<UserResponseDto> result = userMatchingService.getUsersExcept(currentUserId);

        // then
        assertThat(result.get(0).getId()).isEqualTo(2L);
        assertThat(result.get(0).getName()).isEqualTo("사용자2");
    }
}
