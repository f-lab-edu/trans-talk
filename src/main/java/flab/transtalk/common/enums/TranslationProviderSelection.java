package flab.transtalk.common.enums;

public enum TranslationProviderSelection {
    GOOGLE("google");

    private final String providerKey;

    private TranslationProviderSelection(String providerKey){
        this.providerKey = providerKey;
    }

    public String getProviderKey(){
        return this.providerKey;
    }
}
