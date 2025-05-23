package flab.transtalk.common.exception;


import lombok.Getter;

import java.util.List;

@Getter
public class NotFoundException extends RuntimeException {
    private String input;
    private String output;
    public NotFoundException(String message, String input){
        super(message);
        this.input = input;
    }
    public NotFoundException(String message, String input, String output){
        super(message);
        this.input = input;
        this.output = output;
    }
}
