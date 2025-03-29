package flab.transtalk.common.dto.res;

import lombok.Value;

@Value
public class ApiErrorResponse {
    private final String code;
    private final Object message;
}
