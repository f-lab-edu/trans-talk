package flab.transtalk.translation.service.translation;

import flab.transtalk.common.enums.LanguageSelection;
import flab.transtalk.common.enums.TranslationProviderSelection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class TranslateService {

    @Value("${translation.default-provider:GOOGLE}")
    private TranslationProviderSelection defaultProvider;

    private final Map<String, TranslationProvider> providers;

    public String translate(TranslationProviderSelection providerSelection,
                            String text,
                            LanguageSelection sourceLanguage,
                            LanguageSelection targetLanguage){
        if (providerSelection == null){
            providerSelection = defaultProvider;
        }

        String primaryKey = providerSelection.getProviderKey();

        List<Map.Entry<String, TranslationProvider>> orderedProviders = new ArrayList<>();

        TranslationProvider primary = providers.get(primaryKey);
        if (primary != null) {
            orderedProviders.add(Map.entry(primaryKey, primary));
        } else {
            throw new IllegalArgumentException("No provider found for: " + primaryKey);
        }

        for (Map.Entry<String, TranslationProvider> entry : providers.entrySet()) {
            if (!entry.getKey().equals(primaryKey)) {
                orderedProviders.add(entry);
            }
        }

        if (orderedProviders.isEmpty()) {
            throw new IllegalStateException("등록된 번역 Provider가 없습니다.");
        }

        for (Map.Entry<String, TranslationProvider> entry : orderedProviders) {
            String key = entry.getKey();
            TranslationProvider provider = entry.getValue();

            try {
                return provider.translate(text, sourceLanguage, targetLanguage);
            } catch (RuntimeException ex) {
                log.warn("Translation provider '{}' failed: {}", key, ex.getMessage(), ex);
            }
        }

        throw new IllegalStateException("모든 번역 provider 호출에 실패했습니다.");
    }

}
