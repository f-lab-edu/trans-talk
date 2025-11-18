package flab.transtalk.translation.dto.req;

import flab.transtalk.common.enums.TranslationProviderSelection;
import lombok.Getter;

@Getter
public class ChatMessageRequestDto {
    private Long chatRoomId;
    private String content;
    private TranslationProviderSelection providerSelection = TranslationProviderSelection.GOOGLE;
}
