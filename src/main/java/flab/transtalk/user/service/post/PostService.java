package flab.transtalk.user.service.post;

import flab.transtalk.common.exception.NotFoundException;
import flab.transtalk.common.exception.message.ExceptionMessages;
import flab.transtalk.config.ServiceConfigConstants;
import flab.transtalk.user.domain.Post;
import flab.transtalk.user.domain.Profile;
import flab.transtalk.user.dto.req.PostCreateRequestDto;
import flab.transtalk.user.dto.res.PostResponseDto;
import flab.transtalk.user.repository.PostRepository;
import flab.transtalk.user.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final ProfileRepository profileRepository;
    private final ImageService imageService;

    // Post 생성 (+ 이미지 업로드)
    public PostResponseDto createPost(PostCreateRequestDto dto) throws IOException {
        Profile profile = profileRepository.findById(dto.getProfileId())
                .orElseThrow(() -> new NotFoundException(
                        ExceptionMessages.PROFILE_NOT_FOUND,
                        dto.getProfileId().toString()
                ));
        String generatedImageKey = imageService.uploadImageFile(dto.getImageFile());


        Post post = Post.builder()
                .briefContext(dto.getBriefContext())
                .imageKey(generatedImageKey)
                .build();
        post.setProfile(profile);

        Post saved = postRepository.save(post);
        String presignedUrl = imageService.generatePresignedUrl(
                generatedImageKey,
                Duration.ofHours(ServiceConfigConstants.PRESIGNED_URL_DURATION_HOUR)
        );
        return PostResponseDto.builder()
                .id(saved.getId())
                .briefContext(saved.getBriefContext())
                .imagePresignedUrl(presignedUrl)      // presignedURL 반환
                .build();
    }

    // Post 조회
    public PostResponseDto getPost(Long postId){
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException(
                        ExceptionMessages.POST_NOT_FOUND,
                        postId.toString()
                ));
        String presignedUrl = imageService.generatePresignedUrl(
                post.getImageKey(),
                Duration.ofHours(ServiceConfigConstants.PRESIGNED_URL_DURATION_HOUR)
        );
        return PostResponseDto.builder()
                .id(post.getId())
                .briefContext(post.getBriefContext())
                .imagePresignedUrl(presignedUrl)
                .build();
    }

    // Post 삭제
    public void deletePost(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() ->
                new NotFoundException(
                        ExceptionMessages.POST_NOT_FOUND,
                        postId.toString()
                )
        );
        imageService.deleteImageFile(post.getImageKey());
        postRepository.delete(post);
    }

    public List<PostResponseDto> getPosts(List<Post> posts) {
        if (posts == null){
            return Collections.emptyList();
        }
        return posts.stream()
                .map(post -> PostResponseDto.builder()
                        .id(post.getId())
                        .briefContext(post.getBriefContext())
                        .imagePresignedUrl(
                                imageService.generatePresignedUrl(
                                        post.getImageKey(),
                                        Duration.ofHours(ServiceConfigConstants.PRESIGNED_URL_DURATION_HOUR)
                                )
                        )
                        .build())
                .collect(Collectors.toList());
    }
}
