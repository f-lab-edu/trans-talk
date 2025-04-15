package flab.transtalk.common.exception;


import lombok.Getter;

import java.util.List;

@Getter
public class NotFoundException extends RuntimeException {
    private String inputIds;
    private String foundIds;
    public NotFoundException(String message, String inputIds, String foundIds){
        super(message);
        this.inputIds = inputIds;
        this.foundIds = foundIds;
    }
}
