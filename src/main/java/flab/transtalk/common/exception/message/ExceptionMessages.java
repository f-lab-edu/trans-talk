package flab.transtalk.common.exception.message;

public class ExceptionMessages {
    // NotFoundException
    public static final String NO_USER_FOUND = "발견된 사용자가 없습니다.";
    public static final String USER_NOT_FOUND_IN_PARTICIPANTS = "존재하지 않는(또는 중복된) 참가자가 포함되어 있습니다.";
    public static final String USER_NOT_FOUND = "존재하지 않는 사용자입니다.";
    public static final String PROFILE_NOT_FOUND = "존재하지 않는 프로필입니다.";
    public static final String POST_NOT_FOUND = "존재하지 않는 포스트입니다.";
    public static final String IMAGE_NOT_FOUND = "존재하지 않는 이미지입니다.";


    // BadRequestException
    public static final String CHAT_ROOM_REQUIRES_AT_LEAST_TWO_USERS = "채팅방에는 최소 2명이상 존재해야 합니다.";
    public static final String IMAGE_UPLOAD_FAILED = "이미지 업로드 중 오류가 발생했습니다.";

}
