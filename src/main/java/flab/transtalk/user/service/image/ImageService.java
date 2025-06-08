package flab.transtalk.user.service.post;

import flab.transtalk.common.exception.BadRequestException;
import flab.transtalk.common.exception.NotFoundException;
import flab.transtalk.common.exception.message.ExceptionMessages;
import flab.transtalk.config.ServiceConfigConstants;
import lombok.RequiredArgsConstructor;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageService {
    // AwsConfig에서 등록한 Bean 사용
    private final S3Client s3Client;
    private final S3Presigner presigner;

    @Value("${app.aws.s3.bucket}")
    private String bucket;

    private static final Tika tika = new Tika();

    public static final Map<String, String> SUPPORTED_IMAGE_TYPES = Map.of(
            "image/jpeg", ".jpg",
            "image/png", ".png"
    );

    // image key 생성 및 이미지 업로드
    public String uploadImageFile(MultipartFile file) throws IOException {
        String detectedContentType = tika.detect(file.getInputStream());
        String extension = SUPPORTED_IMAGE_TYPES.get(detectedContentType);
        if (extension == null){
            throw new BadRequestException(
                    String.format(ExceptionMessages.UNSUPPORTED_IMAGE_FORMAT, detectedContentType)
            );
        }
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss"));
        // image key 구성: post image folder 이름 + UUID + LocalDate + 확장자
        String generatedImageKey = ServiceConfigConstants.S3_POST_IMAGE_FOLDER_NAME + UUID.randomUUID() + "-" + timestamp + extension;

        PutObjectRequest req = PutObjectRequest.builder()
                .bucket(bucket)
                .key(generatedImageKey)
                .contentType(detectedContentType)
                .acl(ObjectCannedACL.PRIVATE)
                .build();

        s3Client.putObject(req, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        return generatedImageKey;
    }

    // s3 단일 image 삭제
    public void deleteImageFile(String imageKey) {
        try {
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(imageKey)
                    .build());
        } catch (S3Exception e){
            if ("NotFound".equals(e.awsErrorDetails().errorCode()) || "NoSuchKey".equals(e.awsErrorDetails().errorCode())) {
                throw new NotFoundException(
                        ExceptionMessages.IMAGE_NOT_FOUND,
                        imageKey
                );
            } else {
                throw e;
            }
        }
    }

    public String generatePresignedUrl(String imageKey, Duration ttl){
        try {
            s3Client.headObject(HeadObjectRequest.builder()
                    .bucket(bucket)
                    .key(imageKey)
                    .build());
        } catch (S3Exception e) {
            if ("NotFound".equals(e.awsErrorDetails().errorCode()) || "NoSuchKey".equals(e.awsErrorDetails().errorCode())) {
                throw new NotFoundException(
                        ExceptionMessages.IMAGE_NOT_FOUND,
                        imageKey
                );
            }
            throw e;
        }
        PresignedGetObjectRequest pre = presigner.presignGetObject(b -> b
                .signatureDuration(ttl)
                .getObjectRequest(o -> o.bucket(bucket).key(imageKey)));
        return pre.url().toString();
    }
}
