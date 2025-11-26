package flab.transtalk.translation.service.translation;

import flab.transtalk.common.enums.LanguageSelection;

public interface TranslationProvider {
    String translate(String text, LanguageSelection sourceLang, LanguageSelection targetLang);
}
