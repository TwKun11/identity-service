package com.identity_user.identtity_user.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999,"Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    USERNAME_INVALID(1003, "Username must be at least 3 characters",HttpStatus.BAD_REQUEST),
    USER_EXISTED(1001, "User existed",HttpStatus.BAD_REQUEST),
    INVALID_KEY(1005,"USER MESSAGE KEY",HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1006, "User not existed",HttpStatus.NOT_FOUND),
    USER_UNAUTHENTICATED(1007, "User unauthenticated",HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1009, "you are not permit",HttpStatus.UNAUTHORIZED),
    INVALID_DOB(1002, "Your age must at least {min}",HttpStatus.BAD_REQUEST),

    ;

    ErrorCode(int code, String message,HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus =httpStatus;
    }

    private int code;
    private String message;
    private HttpStatus httpStatus;

}
