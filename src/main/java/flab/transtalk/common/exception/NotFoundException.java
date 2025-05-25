package flab.transtalk.common.exception;


import lombok.Getter;

import java.util.List;

@Getter
public class NotFoundException extends RuntimeException {
    private final String input;
    private final String output;
    public NotFoundException(String message, String input){
        super(message);
        this.input = input;
        this.output = null;
    }
    public NotFoundException(String message, String input, String output){
        super(message);
        this.input = input;
        this.output = output;
    }
}
