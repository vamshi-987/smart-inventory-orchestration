package com.vamshi.stockflow_backend.common.exception;

import com.vamshi.stockflow_backend.inventory.exception.StockConflictException;
import com.vamshi.stockflow_backend.order.exception.OutOfServiceAreaException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Instant;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @Value("${app.include-stack-trace:false}")
    private boolean includeStackTrace;

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ErrorResponse> handleAppException(
            AppException exception,
            HttpServletRequest request) {

        return buildResponse(
                exception.getStatus(),
                exception.getErrorCode().name(),
                exception.getMessage(),
                request,
                exception
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException exception,
            HttpServletRequest request) {

        List<FieldErrorResponse> fieldErrors = exception
                .getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::toFieldError)
                .toList();

        ErrorResponse body = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .code(ErrorCode.VALIDATION_ERROR.name())
                .message("Request validation failed")
                .path(request.getRequestURI())
                .trace(stackTrace(exception))
                .fieldErrors(fieldErrors)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

        @ExceptionHandler(HttpMessageNotReadableException.class)
        public ResponseEntity<ErrorResponse> handleUnreadableMessage(
                        HttpMessageNotReadableException exception,
                        HttpServletRequest request) {

                ErrorResponse body = ErrorResponse.builder()
                                .timestamp(Instant.now())
                                .status(HttpStatus.BAD_REQUEST.value())
                                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                                .code(ErrorCode.VALIDATION_ERROR.name())
                                .message("Request body is invalid or malformed")
                                .path(request.getRequestURI())
                                .trace(stackTrace(exception))
                                .fieldErrors(List.of())
                                .build();

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
        }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatusException(
            ResponseStatusException exception,
            HttpServletRequest request) {

        HttpStatus status = HttpStatus.valueOf(exception.getStatusCode().value());

        return buildResponse(
                status,
                status.name(),
                exception.getReason(),
                request,
                exception
        );
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFound(
            EntityNotFoundException exception,
            HttpServletRequest request) {

        return buildResponse(
                HttpStatus.NOT_FOUND,
                ErrorCode.RESOURCE_NOT_FOUND.name(),
                exception.getMessage(),
                request,
                exception
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(
            IllegalArgumentException exception,
            HttpServletRequest request) {

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                ErrorCode.BAD_REQUEST.name(),
                exception.getMessage(),
                request,
                exception
        );
    }

    @ExceptionHandler(StockConflictException.class)
    public ResponseEntity<ErrorResponse> handleStockConflict(
            StockConflictException exception,
            HttpServletRequest request) {

        return buildResponse(
                HttpStatus.CONFLICT,
                ErrorCode.STOCK_CONFLICT.name(),
                exception.getMessage(),
                request,
                exception
        );
    }

    @ExceptionHandler(OutOfServiceAreaException.class)
    public ResponseEntity<ErrorResponse> handleOutOfServiceArea(
            OutOfServiceAreaException exception,
            HttpServletRequest request) {

        return buildResponse(
                HttpStatus.valueOf(422),
                ErrorCode.OUT_OF_SERVICE_AREA.name(),
                exception.getMessage(),
                request,
                exception
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedException(
            Exception exception,
            HttpServletRequest request) {

        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ErrorCode.INTERNAL_SERVER_ERROR.name(),
                "An unexpected error occurred",
                request,
                exception
        );
    }

    private ResponseEntity<ErrorResponse> buildResponse(
            HttpStatus status,
            String code,
            String message,
            HttpServletRequest request,
            Exception exception) {

        ErrorResponse body = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .code(code)
                .message(message)
                .path(request.getRequestURI())
                .trace(stackTrace(exception))
                .build();

        return ResponseEntity.status(status).body(body);
    }

    private FieldErrorResponse toFieldError(FieldError fieldError) {
        return new FieldErrorResponse(
                fieldError.getField(),
                fieldError.getDefaultMessage()
        );
    }

    private String stackTrace(Exception exception) {
        if (!includeStackTrace) {
            return null;
        }
        StringWriter stringWriter = new StringWriter();
        exception.printStackTrace(new PrintWriter(stringWriter));
        return stringWriter.toString();
    }
}