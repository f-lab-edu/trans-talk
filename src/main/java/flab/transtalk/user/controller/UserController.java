package flab.transtalk.user.controller;

import flab.transtalk.user.dto.req.UserCreateRequestDto;
import flab.transtalk.user.dto.res.UserResponseDto;
import flab.transtalk.user.service.user.UserSignUpService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserSignUpService userSignUpService;

    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(@RequestBody @Valid UserCreateRequestDto dto){
        return ResponseEntity.ok(userSignUpService.signUp(dto));
    }
}
