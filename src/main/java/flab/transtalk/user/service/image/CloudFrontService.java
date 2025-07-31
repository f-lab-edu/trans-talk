package flab.transtalk.user.service.image;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import flab.transtalk.config.CloudFrontConfig;
import flab.transtalk.config.ServiceConfigConstants;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;
import java.security.interfaces.RSAPrivateKey;
import java.time.Duration;
import java.util.Arrays;
import java.util.Base64;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CloudFrontService {
    private final RSAPrivateKey privateKey;
    private final CloudFrontConfig cloudFrontConfig;
    private final S3ImageService s3ImageService;
    @Value("${app.aws.s3.suffix.large}")
    private String LARGE_SUFFIX;
    @Value("${app.aws.s3.suffix.small}")
    private String SMALL_SUFFIX;
    private static final ObjectMapper MAPPER = new ObjectMapper();
    @Value("${app.aws.cloudfront.cookie.use-parent-domain}")
    private boolean useParentCookieDomain;

    public Map<String, String> generateSignedCookies(Duration duration) throws JsonProcessingException, NoSuchAlgorithmException, SignatureException, InvalidKeyException {
        String policy = buildPolicy(duration);

        String encodedPolicy = cfBase64(policy.getBytes(StandardCharsets.UTF_8));
        String encodedSignature = cfBase64(signSha1Rsa(policy.getBytes(StandardCharsets.UTF_8), privateKey));

        return Map.of(
                "CloudFront-Policy", encodedPolicy,
                "CloudFront-Signature", encodedSignature,
                "CloudFront-Key-Pair-Id", cloudFrontConfig.getKeyPairId()
        );
    }

    private String buildPolicy(Duration duration) throws JsonProcessingException {
        long expires = System.currentTimeMillis() + duration.toMillis();

        Map<String,Object> policyMap = Map.of(
                "Statement", cloudFrontConfig.getResourcePaths().stream()
                        .map(path -> Map.of(
                                "Resource",  "https://" + cloudFrontConfig.getDomain() + path,
                                "Condition", Map.of("DateLessThan", Map.of("AWS:EpochTime", expires / 1000))
                        ))
                        .collect(Collectors.toList())
        );

        return MAPPER.writeValueAsString(policyMap);
    }

    private byte[] signSha1Rsa(byte[] message, RSAPrivateKey key) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature sig = Signature.getInstance("SHA1withRSA");
        sig.initSign(key);
        sig.update(message);
        return sig.sign();
    }

    private String cfBase64(byte[] data) {
        return Base64.getEncoder()
                .encodeToString(data)
                .replace('+', '-')
                .replace('/', '~')
                .replace('=', '_');
    }

    public void attachSignedCookies(HttpServletResponse response, Map<String, String> cookies, long ttlSeconds) {
        cookies.forEach((key, value) -> {
            ResponseCookie.ResponseCookieBuilder builder = ResponseCookie.from(key, value)
                    .path("/")
                    .httpOnly(true)
                    .secure(true)
                    .maxAge(ttlSeconds)
                    .sameSite("None");

            if (useParentCookieDomain) {
                builder.domain(extractParentDomain(cloudFrontConfig.getDomain()));
            }

            response.addHeader(HttpHeaders.SET_COOKIE, builder.build().toString());
        });
    }

    public String extractParentDomain(String domain) {
        if (domain == null || !domain.contains(".")) {
            throw new IllegalArgumentException("유효하지 않은 도메인: " + domain);
        }

        String[] parts = domain.split("\\.");

        if (parts.length < 2) {
            throw new IllegalArgumentException("상위 도메인을 추출할 수 없음: " + domain);
        }

        return "." + String.join(".", Arrays.copyOfRange(parts, 1, parts.length));
    }

    public void issueSignedCookie(HttpServletResponse response) throws NoSuchAlgorithmException, SignatureException, InvalidKeyException, JsonProcessingException {
        Duration ttl = Duration.ofHours(ServiceConfigConstants.SIGNED_COOKIE_DURATION_HOUR);
        Map<String, String> cookies = generateSignedCookies(ttl);
        attachSignedCookies(response, cookies, ttl.toSeconds());
    }

    public String getLargeImageUrl(String imageKey){
        return getImageUrl(imageKey, LARGE_SUFFIX);
    }

    public String getSmallImageUrl(String imageKey){
        return getImageUrl(imageKey, SMALL_SUFFIX);
    }

    public String getImageUrl(String imageKey, String suffix) {
        if (imageKey == null) {
            return null;
        }

        String fullKey = s3ImageService.getImageKeyWithSuffix(imageKey, suffix);
        return UriComponentsBuilder.newInstance()
                .scheme("https")
                .host(cloudFrontConfig.getDomain())
                .path(fullKey.startsWith("/") ? fullKey : "/" + fullKey)
                .build()
                .toUriString();
    }
}
