package flab.transtalk.translation.service.translation;

import flab.transtalk.common.enums.LanguageSelection;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;


@Component("openai")
@RequiredArgsConstructor
public class OpenaiTranslationProvider implements TranslationProvider {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${translation.openai.api-key}")
    private String apiKey;

    @Value("${translation.openai.prompt-id}")
    private String promptId; // ex: pmpt_6928xxxx...

    @Value("${translation.openai.prompt-version}")
    private String promptVersion; // ex: 3

    @Value("${translation.openai.model-id}")
    private String modelId; // ex: gpt-5-nano-2025-08-07

    @Override
    public String translate(String text, LanguageSelection sourceLang, LanguageSelection targetLang) {
        String url = "https://api.openai.com/v1/responses";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Prompt Variables 매핑
        Map<String, Object> variables = Map.of(
                "sourcelang", sourceLang.getCode(),
                "targetlang", targetLang.getCode(),
                "content", text
        );

        // Prompt 객체 생성
        Map<String, Object> promptObject = Map.of(
                "id", promptId,
                "version", promptVersion,
                "variables", variables
        );

        // 요청 body
        Map<String, Object> requestBody = Map.of(
                "model", modelId,
                "prompt", promptObject
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
            if (body == null || !body.containsKey("output")) {
                throw new IllegalStateException("OpenAI 응답이 잘못되었습니다.");
            }

            // output -> 배열 형태이며 message 아이템 포함
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> outputList = (List<Map<String, Object>>) body.get("output");

            // 두 번째 output 블록
            var messageBlock = outputList.get(1);

            // content.text 부분 추출
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> contentList = (List<Map<String, Object>>) messageBlock.get("content");

            return contentList.get(0).get("text").toString().trim();

        } catch (Exception e) {
            throw new IllegalStateException("OpenAI 번역 API 호출 실패", e);
        }
    }
}
