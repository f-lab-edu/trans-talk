package flab.transtalk.config;

import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Configuration
public class GoogleConfig {
    @Value(("${translation.google.credentials-path}"))
    private String credentialsPath;

    @Bean
    public GoogleCredentials googleCredentials(){
        try (InputStream inputStream = new FileInputStream(credentialsPath)) {
            return GoogleCredentials
                    .fromStream(inputStream)
                    .createScoped("https://www.googleapis.com/auth/cloud-platform");
        } catch (IOException e) {
            throw new IllegalStateException("Google 서비스 계정 키 로딩 실패", e);
        }
    }
}
