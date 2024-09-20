package com.identity_user.identtity_user.exception;

import com.identity_user.identtity_user.dto.request.ApiResponse;
import com.identity_user.identtity_user.entity.User;
import jakarta.validation.ConstraintViolation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;
import java.util.Objects;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
private  final String MIN_ATTRIBUTE = "min";
@ExceptionHandler(value = Exception.class)
ResponseEntity<ApiResponse> handlingRuntimeException(RuntimeException exception) {
    log.error("Exception: ", exception);
    ApiResponse apiResponse = new ApiResponse();

    apiResponse.setCode(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode());
    apiResponse.setMessage(ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage());

    return ResponseEntity.badRequest().body(apiResponse);
}

    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse> handlingAppException(AppException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        ApiResponse apiResponse = new ApiResponse();

        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(errorCode.getMessage());

        return ResponseEntity.status(errorCode.getHttpStatus()).body(apiResponse);
    }


    @ExceptionHandler(value = AccessDeniedException.class)
    ResponseEntity<ApiResponse> handlingAccessDeniedException() {
        ErrorCode errorCode = ErrorCode.UNAUTHORIZED;
        return  ResponseEntity.status(errorCode.getHttpStatus()).body(ApiResponse.builder().code(errorCode.getCode())
                .message(errorCode.getMessage()).build());
    }


    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse> handlingValidation( MethodArgumentNotValidException exception) {
       String enumkey = exception.getFieldError().getDefaultMessage();
       ErrorCode errorCode = ErrorCode.INVALID_KEY;
        Map<String,Object> attribute = null;
try {
     errorCode = ErrorCode.valueOf(enumkey);
     var constraintViolation = exception.getBindingResult().getAllErrors().getFirst().unwrap(ConstraintViolation.class);
     attribute = constraintViolation.getConstraintDescriptor().getAttributes();
     log.info(attribute.toString());
} catch (IllegalArgumentException e) {

}

       ApiResponse apiResponse = new ApiResponse();
       apiResponse.setCode(errorCode.getCode());
       apiResponse.setMessage(Objects.nonNull(attribute) ? mapAttribute(errorCode.getMessage(),attribute) : errorCode.getMessage());
       return ResponseEntity.badRequest().body(apiResponse);
    }
    private String mapAttribute(String message, Map<String,Object> attribute) {
    String minValue = String.valueOf(attribute.get(MIN_ATTRIBUTE));
    return message.replace("{" + MIN_ATTRIBUTE + "}",minValue);
    }

}
