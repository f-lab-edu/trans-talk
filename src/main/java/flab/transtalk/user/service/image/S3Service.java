package flab.transtalk.user.service.image;

import flab.transtalk.common.exception.BadRequestException;
import flab.transtalk.common.exception.NotFoundException;
import flab.transtalk.common.exception.message.ExceptionMessages;
import lombok.RequiredArgsConstructor;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {
    private final S3Client s3Client;

    @Value("${app.aws.s3.bucket}")
    private String bucket;
    @Value("${app.aws.s3.post-resource-path}")
    private String postResourcePath;

    private static final Tika tika = new Tika();

    public static final Map<String, String> SUPPORTED_IMAGE_TYPES = Map.of(
            "image/jpeg", ".jpg",
            "image/png", ".png"
    );

    public String uploadPostImageFile(MultipartFile file, Long profileId) throws IOException {
        String detectedContentType = tika.detect(file.getInputStream());
        String extension = SUPPORTED_IMAGE_TYPES.get(detectedContentType);
        if (extension == null){
            throw new BadRequestException(
                    String.format(ExceptionMessages.UNSUPPORTED_IMAGE_FORMAT, detectedContentType)
            );
        }
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss"));
        String generatedImageKey = generateImageKey(postResourcePath, profileId, timestamp, extension);

        PutObjectRequest req = PutObjectRequest.builder()
                .bucket(bucket)
                .key(generatedImageKey)
                .contentType(detectedContentType)
                .acl(ObjectCannedACL.PRIVATE)
                .build();

        s3Client.putObject(req, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        return generatedImageKey;
    }
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

    public String generateImageKey(String resourcePath, Long id, String timestamp, String extension){
        return resourcePath + "/" + id.toString() + "/" + UUID.randomUUID() + "-" + timestamp + extension;
    }
}
