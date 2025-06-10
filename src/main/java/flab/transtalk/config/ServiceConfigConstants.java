package flab.transtalk.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ServiceConfigConstants {
    public static final String S3_POST_IMAGE_FOLDER_NAME = "posts/";
    public static final long PRESIGNED_URL_DURATION_HOUR = 6;
    public static final int MATCHING_USERS_MAX_NUMBER = 4;
    public static final int MAX_REMAINING_MATCH_REQUESTS = 6;
    public static final int MATCH_REQUEST_RECHARGE_INTERVAL_HOURS = 1;
}
