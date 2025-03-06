package com.burntoburn.easyshift.exception.schedule;

import com.burntoburn.easyshift.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ScheduleErrorCode implements ErrorCode {
    SCHEDULE_NOT_FOUND(HttpStatus.NOT_FOUND, 1001, "해당 스케줄을 찾을 수 없습니다."),
    INSUFFICIENT_USERS_FOR_ASSIGNMENT(HttpStatus.BAD_REQUEST, 1002, "자동 배정을 수행하기에 근무 가능한 사용자가 부족합니다."),

    SCHEDULE_PAGE_NOT_FOUND(HttpStatus.NOT_FOUND, 2002, "요청한 페이지에 스케줄이 존재하지 않습니다.");


    private final HttpStatus httpStatus;
    private final int code;
    private final String message;
}