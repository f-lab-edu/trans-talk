package flab.transtalk.user.service.user;


import flab.transtalk.common.enums.LanguageSelection;
import flab.transtalk.common.exception.NotFoundException;
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

import java.time.LocalDate;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
                .birthDate(LocalDate.of(1999,01,01))
                .build();
        Profile profile = Profile.builder()
                .id(2L)
                .language(LanguageSelection.KOR)
                .selfIntroduction("")
                .build();
        user.setProfile(profile);
        BDDMockito.given(userRepository.findRandomUserExcept(currentUserId)).willReturn(user);

        // when
        UserResponseDto result = userMatchingService.getUserExcept(currentUserId);

        // then
        assertThat(result.getId()).isEqualTo(2L);
        assertThat(result.getName()).isEqualTo("사용자2");
    }

    @Test
    @DisplayName("매칭 사용자 없음 예외 발생 테스트")
    void  testGetUserExcept_2(){
        // given
        Long currentUserId = 1L;
        BDDMockito.given(userRepository.findRandomUserExcept(currentUserId)).willReturn(null);

        // when & then
        assertThatThrownBy(()->userMatchingService.getUserExcept(currentUserId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("발견된 사용자가 없습니다.");
    }
}
