package com.burntoburn.easyshift.exception.template;

import com.burntoburn.easyshift.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum TemplateErrorCode implements ErrorCode {
    SCHEDULE_TEMPLATE_NOT_FOUND(HttpStatus.NOT_FOUND, 2001, "요청한 스케줄 템플릿을 찾을 수 없습니다."),
    DUPLICATE_SCHEDULE_TEMPLATE(HttpStatus.CONFLICT, 2002, "이미 존재하는 스케줄 템플릿입니다.");

    private final HttpStatus httpStatus;
    private final int code;
    private final String message;
}
