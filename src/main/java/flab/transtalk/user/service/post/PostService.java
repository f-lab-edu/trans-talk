package flab.transtalk.user.service.post;

import flab.transtalk.common.exception.BadRequestException;
import flab.transtalk.common.exception.NotFoundException;
import flab.transtalk.common.exception.message.ExceptionMessages;
import flab.transtalk.user.domain.Post;
import flab.transtalk.user.domain.Profile;
import flab.transtalk.user.dto.req.PostCreateRequestDto;
import flab.transtalk.user.dto.res.PostResponseDto;
import flab.transtalk.user.repository.PostRepository;
import flab.transtalk.user.repository.ProfileRepository;
import flab.transtalk.user.service.image.CloudFrontService;
import flab.transtalk.user.service.image.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final ProfileRepository profileRepository;
    private final S3Service imageService;
    private final CloudFrontService cloudFrontService;

    // Post 생성 (+ 이미지 업로드)
    public PostResponseDto createPost(PostCreateRequestDto dto) {
        Profile profile = profileRepository.findById(dto.getProfileId())
                .orElseThrow(() -> new NotFoundException(
                        ExceptionMessages.PROFILE_NOT_FOUND,
                        dto.getProfileId().toString()
                ));
        String generatedImageKey;
        try {
            generatedImageKey = imageService.uploadPostImageFile(dto.getImageFile(), profile.getId());
        } catch (IOException e) {
            throw new BadRequestException(ExceptionMessages.IMAGE_UPLOAD_FAILED);
        }
        Post post = Post.builder()
                .briefContext(dto.getBriefContext())
                .imageKey(generatedImageKey)
                .build();
        post.setProfile(profile);

        Post saved = postRepository.save(post);
        String imageUrl = cloudFrontService.getImageUrl(generatedImageKey);
        return PostResponseDto.builder()
                .id(saved.getId())
                .briefContext(saved.getBriefContext())
                .imageUrl(imageUrl)
                .build();
    }

    // Post 조회
    public PostResponseDto getPost(Long postId){
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException(
                        ExceptionMessages.POST_NOT_FOUND,
                        postId.toString()
                ));
        return PostResponseDto.builder()
                .id(post.getId())
                .briefContext(post.getBriefContext())
                .imageUrl(cloudFrontService.getImageUrl(post.getImageKey()))
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
                        .imageUrl(cloudFrontService.getImageUrl(post.getImageKey()))
                        .build())
                .collect(Collectors.toList());
    }
}
