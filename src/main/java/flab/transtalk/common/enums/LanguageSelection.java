package flab.transtalk.common.enums;

public enum LanguageSelection {
    KOR("ko"),
    ENG("en"),
    JPN("ja");


    private final String code;

    private LanguageSelection(String code){
        this.code = code;
    };

    public String getCode(){
        return this.code;
    }
}
