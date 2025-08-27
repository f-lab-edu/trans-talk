package flab.transtalk.translation.service.translation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class TranslateService {

    private final Map<String, TranslationProvider> providers;

}
