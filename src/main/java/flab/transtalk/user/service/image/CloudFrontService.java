package flab.transtalk.user.service.image;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSASSASigner;
import flab.transtalk.config.CloudFrontConfig;
import flab.transtalk.config.ServiceConfigConstants;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.security.interfaces.RSAPrivateKey;
import java.time.Duration;
import java.util.Base64;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudFrontService {
    private final RSAPrivateKey privateKey;
    private final CloudFrontConfig cloudFrontConfig;

    public Map<String, String> generateSignedCookies(Duration duration) throws JOSEException {
        long expires = System.currentTimeMillis() + duration.toMillis();

        StringBuilder statementBuilder = new StringBuilder();
        for (String path : cloudFrontConfig.getResourcePaths()) {
            statementBuilder.append(String.format("""
                {
                  "Resource": "https://%s/%s",
                  "Condition": {
                    "DateLessThan": {"AWS:EpochTime": %d}
                  }
                },
                """, cloudFrontConfig.getDomain(), path, expires / 1000));
        }
        String policy = String.format("""
            {
              "Statement": [
                %s
              ]
            }
            """, statementBuilder.toString().replaceAll(",$", ""));

        JWSSigner signer = new RSASSASigner(privateKey);
        JWSObject jws = new JWSObject(
                new JWSHeader.Builder(JWSAlgorithm.RS256).build(),
                new Payload(policy)
        );
        jws.sign(signer);
        String signature = Base64.getUrlEncoder().withoutPadding().encodeToString(jws.serialize().getBytes());

        return Map.of(
                "CloudFront-Policy", Base64.getUrlEncoder().withoutPadding().encodeToString(policy.getBytes()),
                "CloudFront-Signature", signature,
                "CloudFront-Key-Pair-Id", cloudFrontConfig.getKeyPairId()
        );
    }

    public void attachSignedCookies(HttpServletResponse response, Map<String, String> cookies, long ttlSeconds) {
        cookies.forEach((key, value) -> {
            ResponseCookie cookie = ResponseCookie.from(key, value)
                    .path("/")
                    .httpOnly(true)
                    .secure(true)
                    .maxAge(ttlSeconds)
                    .sameSite("Lax")
                    .build();
            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        });
    }

    public void issueSignedCookie(HttpServletResponse response) throws JOSEException {
        Duration ttl = Duration.ofHours(ServiceConfigConstants.SIGNED_COOKIE_DURATION_HOUR);
        Map<String, String> cookies = generateSignedCookies(ttl);
        attachSignedCookies(response, cookies, ttl.toSeconds());
    }

    public String getImageUrl(String imageKey){
        if (imageKey==null){
            return null;
        }
        return String.format("https://%s/%s",cloudFrontConfig.getDomain(), imageKey);
    }
}
