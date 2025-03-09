package com.burntoburn.easyshift.exception.shift;

import com.burntoburn.easyshift.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ShiftErrorCode implements ErrorCode {
    SHIFT_NOT_FOUND(HttpStatus.NOT_FOUND, 1001, "해당 시프트을 찾을 수 없습니다."),
    SHIFT_NOT_FOUND_IN_PERIOD(HttpStatus.NOT_FOUND, 1002, "해당 기간에 대한 시프트를 찾을 수 없습니다."),
    ;

    private final HttpStatus httpStatus;
    private final int code;
    private final String message;
}