package flab.transtalk.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.List;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "app.aws.cloudfront")
public class CloudFrontConfig {
    private String domain;
    private String keyPairId;
    private String privateKeyPath;
    private List<String> resourcePaths;

    @Bean
    public RSAPrivateKey cloudFrontPrivateKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        File pemFile = new File(privateKeyPath);
        if (!pemFile.exists()) {
            throw new FileNotFoundException("Private key file not found");
        }
        String key = Files.readString(pemFile.toPath())
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");
        byte[] decoded = Base64.getDecoder().decode(key);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decoded);
        return (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(keySpec);
    }
}
