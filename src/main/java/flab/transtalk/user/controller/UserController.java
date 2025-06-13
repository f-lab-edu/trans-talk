package flab.transtalk.user.controller;

import flab.transtalk.auth.security.principal.CustomUserDetails;
import flab.transtalk.user.dto.res.UserListResponseDto;
import flab.transtalk.user.dto.res.UserMatchStatusResponseDto;
import flab.transtalk.user.dto.res.UserResponseDto;
import flab.transtalk.user.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDto> getUser(@PathVariable Long userId){
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUser(userId));
    }

    // 주어진 사용자 제외, 무작위 사용자 호출
    @GetMapping("/matching/{currentUserId}")
    public ResponseEntity<UserListResponseDto> getMatchingUsers(@PathVariable Long currentUserId){
        return ResponseEntity.status(HttpStatus.OK).body(
                UserListResponseDto.builder()
                        .users(userService.getMatchResult(currentUserId))
                        .build()
        );
    }

    @GetMapping("/matching/state")
    public ResponseEntity<UserMatchStatusResponseDto> getMatchStatus(
            @AuthenticationPrincipal CustomUserDetails principal
    ){
        return ResponseEntity.status(HttpStatus.OK).body(userService.getMatchStatus(principal.getUserId()));
    }
}
