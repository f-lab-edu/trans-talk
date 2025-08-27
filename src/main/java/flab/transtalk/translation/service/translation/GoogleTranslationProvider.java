package flab.transtalk.translation.service.translation;

import com.google.auth.oauth2.GoogleCredentials;
import flab.transtalk.common.enums.LanguageSelection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component("google")
@RequiredArgsConstructor
@Slf4j
public class GoogleTranslationProvider implements TranslationProvider {

    private final RestTemplate restTemplate = new RestTemplate();

    private final GoogleCredentials googleCredentials;

    @Value("${translation.google.project-id}")
    private String projectId;

    @Override
    public String translate(String text, LanguageSelection sourceLang, LanguageSelection targetLang) {
        String url = "https://translation.googleapis.com/v3/projects/" + projectId + ":translateText";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(getAccessToken()); // "Authorization: Bearer {token}"
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = Map.of(
                "sourceLanguageCode", sourceLang.getCode(),
                "targetLanguageCode", targetLang.getCode(),
                "contents", Collections.singletonList(text)
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    request,
                    Map.class
            );

            Map<String, Object> body = response.getBody();
            if (body == null || !body.containsKey("translations")) {
                throw new RuntimeException("Google 번역 API 응답이 유효하지 않은 형식입니다.\n응답 내용: " + body);
            }

            @SuppressWarnings("unchecked")
            List<Map<String, String>> translations = (List<Map<String, String>>) body.get("translations");
            return translations.get(0).get("translatedText");

        } catch (Exception e) {
            throw new IllegalStateException("Google 번역 API 호출 실패", e);
        }
    }

    public String getAccessToken() {
        try {
            synchronized (googleCredentials) {
                googleCredentials.refreshIfExpired();
                return googleCredentials.getAccessToken().getTokenValue();
            }
        } catch (Exception e) {
            throw new IllegalStateException("Google access token 발급 실패", e);
        }
    }
}
