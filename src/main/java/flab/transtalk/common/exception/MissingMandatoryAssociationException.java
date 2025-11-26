package flab.transtalk.common.exception;

public class MissingMandatoryAssociationException extends RuntimeException {
    public MissingMandatoryAssociationException(String message){
        super(message);
    }
}
