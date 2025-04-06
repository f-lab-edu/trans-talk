package flab.transtalk.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import flab.transtalk.user.controller.UserController;
import flab.transtalk.user.dto.req.UserCreateRequestDto;
import flab.transtalk.user.service.user.UserSignUpService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureWebMvc
@DisplayName("사용자 컨트롤러 테스트")
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserSignUpService userSignUpService;

    @Test
    @DisplayName("가입 시에 사용자 이름이 공백일 경우 예외를 발생시킨다.")
    void validationTest() throws Exception{
        // given
        final String name = "";
        final LocalDate birthDate = LocalDate.now();
        final UserCreateRequestDto request = UserCreateRequestDto.builder()
                .name(name)
                .birthDate(birthDate)
                .build();
        String invalidJson = objectMapper.writeValueAsString(request);
        // when & then
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
            .andExpect(jsonPath("$.message[0]").value("name: 이름은 null 혹은 공백이 될 수 없습니다."))
            .andDo(print());
    }
}
