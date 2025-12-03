package flab.transtalk.common.enums;

public enum TranslationProviderSelection {
    OPENAI("openai"),
    GOOGLE("google");

    private final String providerKey;

    private TranslationProviderSelection(String providerKey){
        this.providerKey = providerKey;
    }

    public String getProviderKey(){
        return this.providerKey;
    }
}
