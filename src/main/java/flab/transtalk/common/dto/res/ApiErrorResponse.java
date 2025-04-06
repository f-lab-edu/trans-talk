package flab.transtalk.common.dto.res;

import lombok.Value;

import java.util.List;

@Value
public class ApiErrorResponse {
    private final String code;
    private final List<String> message;
}
