package flab.transtalk.user.service.image;

import flab.transtalk.common.exception.BadRequestException;
import flab.transtalk.common.exception.NotFoundException;
import flab.transtalk.common.exception.message.ExceptionMessages;
import flab.transtalk.config.ServiceConfigConstants;
import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {
    private static final Tika tika = new Tika();
    private final S3Client s3Client;
    @Value("${app.aws.s3.bucket}")
    private String bucket;
    @Value("${app.aws.s3.profiles-resource-path}")
    private String profileResourcePath;
    @Value("${app.aws.s3.post-resource-path}")
    private String postResourcePath;
    @Value("${app.aws.s3.suffix.large}")
    private String LARGE_SUFFIX;
    @Value("${app.aws.s3.suffix.small}")
    private String SMALL_SUFFIX;


    public static final Map<String, String> SUPPORTED_IMAGE_TYPES = Map.of(
            "image/jpeg", ".jpg",
            "image/png", ".png"
    );

    public void uploadImageFile(MultipartFile file, String imageKey, String contentType) throws IOException {
        PutObjectRequest req = PutObjectRequest.builder()
                .bucket(bucket)
                .key(imageKey)
                .contentType(contentType)
                .acl(ObjectCannedACL.PRIVATE)
                .build();

        s3Client.putObject(req, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
    }

    public void uploadImageFileBytes(byte[] fileBytes, String imageKey, String contentType) {
        PutObjectRequest req = PutObjectRequest.builder()
                .bucket(bucket)
                .key(imageKey)
                .contentType(contentType)
                .acl(ObjectCannedACL.PRIVATE)
                .build();

        s3Client.putObject(req, RequestBody.fromBytes(fileBytes));
    }

    public BufferedImage cropToSquare(MultipartFile file) throws IOException {
        BufferedImage originalImage = ImageIO.read(file.getInputStream());
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        int squareSize = Math.min(width, height);
        int x = (width - squareSize) / 2;
        int y = (height - squareSize) / 2;

        BufferedImage croppedImage = originalImage.getSubimage(x, y, squareSize, squareSize);
        return croppedImage;
    }

    public byte[] resizeImage(BufferedImage croppedImage, String extension, int width, int height) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Thumbnails.of(croppedImage)
                .size(width, height)
                .outputFormat(extension)
                .toOutputStream(outputStream);
        return outputStream.toByteArray();
    }

    public String uploadProfileImageFile(MultipartFile file, Long userId) throws IOException {
        String detectedContentType = tika.detect(file.getInputStream());
        String extension = SUPPORTED_IMAGE_TYPES.get(detectedContentType);
        if (extension == null){
            throw new BadRequestException(
                    String.format(ExceptionMessages.UNSUPPORTED_IMAGE_FORMAT, detectedContentType)
            );
        }
        String generatedImageKey = generateImageKey(file, profileResourcePath, userId, extension);

        String largeImageKey = getImageKeyWithSuffix(generatedImageKey, LARGE_SUFFIX);
        uploadImageFile(file, largeImageKey, detectedContentType);

        String smallImageKey = getImageKeyWithSuffix(generatedImageKey, SMALL_SUFFIX);
        byte[] smallImageBytes = resizeImage(cropToSquare(file), extension.substring(1), ServiceConfigConstants.PROFILE_SMALL_IMAGE_WIDTH, ServiceConfigConstants.PROFILE_SMALL_IMAGE_HEIGHT);
        uploadImageFileBytes(smallImageBytes, smallImageKey, detectedContentType);

        return generatedImageKey;
    }

    public String uploadPostImageFile(MultipartFile file, Long profileId) throws IOException {
        String detectedContentType = tika.detect(file.getInputStream());
        String extension = SUPPORTED_IMAGE_TYPES.get(detectedContentType);
        if (extension == null){
            throw new BadRequestException(
                    String.format(ExceptionMessages.UNSUPPORTED_IMAGE_FORMAT, detectedContentType)
            );
        }
        String generatedImageKey = generateImageKey(file, postResourcePath, profileId, extension);
        BufferedImage croppedImage = cropToSquare(file);

        String largeImageKey = getImageKeyWithSuffix(generatedImageKey, LARGE_SUFFIX);
        byte[] largeImageBytes = resizeImage(croppedImage, extension.substring(1), ServiceConfigConstants.POST_LARGE_IMAGE_WIDTH, ServiceConfigConstants.POST_LARGE_IMAGE_HEIGHT);
        uploadImageFileBytes(largeImageBytes, largeImageKey, detectedContentType);

        String smallImageKey = getImageKeyWithSuffix(generatedImageKey, SMALL_SUFFIX);
        byte[] smallImageBytes = resizeImage(croppedImage, extension.substring(1), ServiceConfigConstants.POST_SMALL_IMAGE_WIDTH, ServiceConfigConstants.POST_SMALL_IMAGE_HEIGHT);
        uploadImageFileBytes(smallImageBytes, smallImageKey, detectedContentType);

        return generatedImageKey;
    }

    public void deleteImageFile(String imageKey) {
        deleteImageFileByDetailImageKey(getImageKeyWithSuffix(imageKey, LARGE_SUFFIX));
        deleteImageFileByDetailImageKey(getImageKeyWithSuffix(imageKey, SMALL_SUFFIX));
    }

    public void deleteImageFileByDetailImageKey(String imageKey) {
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

    public String generateImageKey(MultipartFile file, String resourcePath, Long id, String extension) throws IOException {
        return resourcePath + "/" + id.toString() + "/" + UUID.randomUUID() + extension;
    }
    public String getImageKeyWithSuffix(String imageKey, String suffix){
        if (imageKey == null || imageKey.isEmpty()) {
            throw new IllegalArgumentException("imageKey must not be null or empty");
        }

        int lastDotIndex = imageKey.lastIndexOf(".");
        if (lastDotIndex == -1 || lastDotIndex == imageKey.length() - 1 || !SUPPORTED_IMAGE_TYPES.values().contains(imageKey.substring(lastDotIndex+1))) {
            throw new IllegalArgumentException("Invalid imageKey format: missing or malformed file extension.");
        }
        String base = imageKey.substring(0, lastDotIndex);
        String ext = imageKey.substring(lastDotIndex);
        return base + suffix + ext;
    }
}
