package flab.transtalk.user.controller;

import com.nimbusds.jose.JOSEException;
import flab.transtalk.user.dto.req.PostCreateRequestDto;
import flab.transtalk.user.dto.res.PostResponseDto;
import flab.transtalk.user.service.image.CloudFrontService;
import flab.transtalk.user.service.post.PostService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final CloudFrontService cloudFrontService;

    @PostMapping(consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PostResponseDto> createPost(
                HttpServletResponse response,
                @RequestParam("briefContext") String briefContext,
                @RequestParam("imageFile") MultipartFile imageFile,
                @RequestParam("profileId") Long profileId) throws JOSEException {

        PostCreateRequestDto dto = PostCreateRequestDto.builder()
                .briefContext(briefContext)
                .imageFile(imageFile)
                .profileId(profileId)
                .build();
        cloudFrontService.issueSignedCookie(response);
        return ResponseEntity.status(HttpStatus.CREATED).body(postService. createPost(dto));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostResponseDto> get(@PathVariable Long postId){
        return ResponseEntity.status(HttpStatus.OK).body(postService.getPost(postId));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId){
        postService.deletePost(postId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
