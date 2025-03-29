package flab.transtalk.user.dto.req;

import flab.transtalk.user.domain.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Builder;
import lombok.Value;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * DTO for {@link flab.transtalk.user.domain.User}
 */
@Value
@Builder
public class UserCreateRequestDto implements Serializable {
    @NotBlank(message = "이름은 null 혹은 공백이 될 수 없습니다.")
    String name;

    @PastOrPresent(message = "생일은 과거 혹은 현재 날짜만이 유효합니다.")
    LocalDate birthDate;

    public User toEntity(){
        return User.builder()
                .name(getName())
                .birthDate(getBirthDate())
                .build();
    }
}