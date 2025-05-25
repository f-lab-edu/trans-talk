package flab.transtalk.user.controller;

import flab.transtalk.user.dto.req.ProfileUpdateRequestDto;
import flab.transtalk.user.dto.res.ProfileResponseDto;
import flab.transtalk.user.service.profile.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @PutMapping("/{profileId}")
    public ResponseEntity<ProfileResponseDto> updateProfile(
            @PathVariable Long profileId,
            @RequestBody ProfileUpdateRequestDto dto){
        return ResponseEntity.status(HttpStatus.OK).body(profileService.updateProfile(profileId, dto));
    }

    @GetMapping("/{profileId}")
    public ResponseEntity<ProfileResponseDto> getProfile(@PathVariable Long profileId){
        return ResponseEntity.status(HttpStatus.OK).body(profileService.getProfile(profileId));
    }

}
