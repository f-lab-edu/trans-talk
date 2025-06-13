package flab.transtalk.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ServiceConfigConstants {
    public static final long SIGNED_COOKIE_DURATION_HOUR = 6;
    public static final int MATCHING_USERS_MAX_NUMBER = 4;
    public static final int PROFILE_SMALL_IMAGE_WIDTH = 640;
    public static final int PROFILE_SMALL_IMAGE_HEIGHT = 640;
    public static final int POST_LARGE_IMAGE_WIDTH = 1080;
    public static final int POST_LARGE_IMAGE_HEIGHT = 1080;
    public static final int POST_SMALL_IMAGE_WIDTH = 640;
    public static final int POST_SMALL_IMAGE_HEIGHT = 640;
}
