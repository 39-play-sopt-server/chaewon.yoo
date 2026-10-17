package org.sopt.global.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.sopt.global.response.ApiResponse;
import org.sopt.global.code.ErrorCode;
import org.sopt.global.code.GlobalErrorCode;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException e) {
        ErrorCode code = e.getErrorCode();
        return failure(code);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUnexpectedException(Exception e) {
        log.error("요청 처리 중 오류가 발생했습니다.", e);
        return failure(GlobalErrorCode.SERVER_ERROR);
    }

    private ResponseEntity<ApiResponse<Void>> failure(ErrorCode code) {
        return ResponseEntity.status(code.getStatus())
                .contentType(MediaType.APPLICATION_JSON)
                .body(ApiResponse.fail(code));
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception e, Object body, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ErrorCode code;
        switch (status.value()) {
            case 400:
                code = GlobalErrorCode.INVALID_REQUEST;
                break;
            case 404:
                code = GlobalErrorCode.PATH_NOT_FOUND;
                break;
            case 405:
                code = GlobalErrorCode.METHOD_NOT_ALLOWED;
                break;
            case 406:
                code = GlobalErrorCode.NOT_ACCEPTABLE;
                break;
            case 415:
                code = GlobalErrorCode.UNSUPPORTED_MEDIA_TYPE;
                break;
            default:
                code = status.is5xxServerError()
                        ? GlobalErrorCode.SERVER_ERROR : GlobalErrorCode.INVALID_REQUEST;
        }
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.putAll(headers);
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        return super.handleExceptionInternal(e, ApiResponse.fail(code), responseHeaders, status, request);
    }
}
