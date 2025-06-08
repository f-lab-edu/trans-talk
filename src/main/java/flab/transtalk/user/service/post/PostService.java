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
import flab.transtalk.user.service.image.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostDtoMapper postDtoMapper;
    private final ProfileRepository profileRepository;
    private final S3Service s3Service;

    public PostResponseDto createPost(PostCreateRequestDto dto) {
        Profile profile = profileRepository.findById(dto.getProfileId())
                .orElseThrow(() -> new NotFoundException(
                        ExceptionMessages.PROFILE_NOT_FOUND,
                        dto.getProfileId().toString()
                ));
        String generatedImageKey;
        try {
            generatedImageKey = s3Service.uploadPostImageFile(dto.getImageFile(), profile.getId());
        } catch (IOException e) {
            throw new BadRequestException(ExceptionMessages.IMAGE_UPLOAD_FAILED);
        }
        Post post = Post.builder()
                .briefContext(dto.getBriefContext())
                .imageKey(generatedImageKey)
                .build();
        post.setProfile(profile);

        Post saved = postRepository.save(post);
        return postDtoMapper.toDto(saved);
    }

    public PostResponseDto getPost(Long postId){
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException(
                        ExceptionMessages.POST_NOT_FOUND,
                        postId.toString()
                ));
        return postDtoMapper.toDto(post);
    }

    public void deletePost(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() ->
                new NotFoundException(
                        ExceptionMessages.POST_NOT_FOUND,
                        postId.toString()
                )
        );
        s3Service.deleteImageFile(post.getImageKey());
        postRepository.delete(post);
    }
}
