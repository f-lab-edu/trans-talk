package flab.transtalk.user.controller;

import com.nimbusds.jose.JOSEException;
import flab.transtalk.auth.security.principal.CustomUserDetails;
import flab.transtalk.user.dto.req.ProfileUpdateRequestDto;
import flab.transtalk.user.dto.res.ProfileResponseDto;
import flab.transtalk.user.service.image.CloudFrontService;
import flab.transtalk.user.service.profile.ProfileService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;
    private final CloudFrontService cloudFrontService;

    @PutMapping("/image")
    public ResponseEntity<ProfileResponseDto> updateProfile(
            HttpServletResponse response,
            @AuthenticationPrincipal CustomUserDetails principal,
            @RequestParam("imageFile") MultipartFile imageFile) throws JOSEException {
        cloudFrontService.issueSignedCookie(response);
        return ResponseEntity.status(HttpStatus.OK).body(profileService.updateProfileImage(principal.getUserId(), imageFile));
    }

    @PutMapping("/")
    public ResponseEntity<ProfileResponseDto> updateProfile(
            HttpServletResponse response,
            @AuthenticationPrincipal CustomUserDetails principal,
            @RequestBody ProfileUpdateRequestDto dto) throws JOSEException {
        cloudFrontService.issueSignedCookie(response);
        return ResponseEntity.status(HttpStatus.OK).body(profileService.updateProfile(principal.getUserId(), dto));
    }

    @GetMapping("/")
    public ResponseEntity<ProfileResponseDto> getProfile(
            HttpServletResponse response,
            @AuthenticationPrincipal CustomUserDetails principal) throws JOSEException {
        cloudFrontService.issueSignedCookie(response);
        return ResponseEntity.status(HttpStatus.OK).body(profileService.getProfile(principal.getUserId()));
    }
}
