package lt.calendar.reminders.web.error;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import lt.calendar.reminders.application.exception.BusinessRuleViolationException;
import lt.calendar.reminders.application.exception.ReminderNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private final Clock clock;

    public GlobalExceptionHandler(Clock clock) {
        this.clock = clock;
    }

    // 400 - invalid method args
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex, HttpServletRequest request) {

        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(fe ->
                fieldErrors.put(fe.getField(), fe.getDefaultMessage())
        );

        HttpStatus status = HttpStatus.BAD_REQUEST;

        log.debug("Validation failed for request {}: fieldErrors={}", request.getRequestURI(), fieldErrors);

        ApiErrorResponse body = ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now(clock))
                .status(status.value())
                .error(status.getReasonPhrase())
                .message("Validation failed")
                .path(request.getRequestURI())
                .fieldErrors(fieldErrors)
                .build();

        return ResponseEntity.status(status).body(body);
    }

    // 400 - date out of allowed range
    @ExceptionHandler(BusinessRuleViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleBusinessRuleViolation(BusinessRuleViolationException ex, HttpServletRequest request) {

        HttpStatus status = HttpStatus.BAD_REQUEST;

        log.warn("Business rule violation at {}: {}", request.getRequestURI(), ex.getMessage());

        ApiErrorResponse body = ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now(clock))
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(status).body(body);
    }

    // 404 - reminder not found when updating/deleting
    @ExceptionHandler(ReminderNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(ReminderNotFoundException ex, HttpServletRequest request) {

        HttpStatus status = HttpStatus.NOT_FOUND;

        log.info("Reminder not found at {}: {}", request.getRequestURI(), ex.getMessage());

        ApiErrorResponse body = ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now(clock))
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(status).body(body);
    }

    // fallback for any unexpected error - 500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpected(Exception ex, HttpServletRequest request) {

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        log.error("Unexpected error at {}: {}", request.getRequestURI(), ex.getMessage(), ex);

        ApiErrorResponse body = ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now(clock))
                .status(status.value())
                .error(status.getReasonPhrase())
                .message("Unexpected error occurred")
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(status).body(body);
    }

    // 500 - when for example year is foobar
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {

        HttpStatus status = HttpStatus.BAD_REQUEST;

        String paramName = ex.getName();
        Object value = ex.getValue();

        String message = String.format("Invalid value '%s' for parameter '%s'", value, paramName);

        log.warn("Type mismatch at {}: param='{}', value='{}', message={}",
                request.getRequestURI(), paramName, value, ex.getMessage());

        ApiErrorResponse body = ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now(clock))
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(status).body(body);
    }
}