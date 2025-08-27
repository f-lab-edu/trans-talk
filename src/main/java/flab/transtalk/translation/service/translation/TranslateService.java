package flab.transtalk.translation.service.translation;

import flab.transtalk.common.enums.LanguageSelection;
import flab.transtalk.common.enums.TranslationProviderSelection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class TranslateService {

    private final Map<String, TranslationProvider> providers;

    public String translate(TranslationProviderSelection providerSelection,
                            String text,
                            LanguageSelection sourceLanguage,
                            LanguageSelection targetLanguage){
        TranslationProvider provider = providers.get(providerSelection.getProviderKey());
        if (provider == null){
            throw new IllegalArgumentException("No provider found for: " + providerSelection.getProviderKey());
        }
        return provider.translate(text, sourceLanguage, targetLanguage);
    }

}
