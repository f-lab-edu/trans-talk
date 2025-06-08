package flab.transtalk.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ServiceConfigConstants {
    public static final long SIGNED_COOKIE_DURATION_HOUR = 6;
    public static final int MATCHING_USERS_MAX_NUMBER = 4;
}
