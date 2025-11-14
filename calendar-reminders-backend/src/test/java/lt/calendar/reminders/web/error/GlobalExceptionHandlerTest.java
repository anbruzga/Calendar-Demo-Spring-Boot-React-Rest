package lt.calendar.reminders.web.error;

import jakarta.servlet.http.HttpServletRequest;
import lt.calendar.reminders.application.exception.BusinessRuleViolationException;
import lt.calendar.reminders.application.exception.ReminderNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final Clock clock = Clock.fixed(
            Instant.parse("2025-01-01T00:00:00Z"),
            ZoneOffset.UTC
    );

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler(clock);

    private MockHttpServletRequest requestWithPath(String path) {
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setRequestURI(path);
        return req;
    }

    @Test
    @DisplayName("handleNotFound should return 404 with error message and path")
    void handleNotFound_returns404AndMessage() {
        ReminderNotFoundException ex = new ReminderNotFoundException("Reminder not found with id: 42");
        HttpServletRequest request = requestWithPath("/reminders/42");

        ResponseEntity<ApiErrorResponse> response = handler.handleNotFound(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        ApiErrorResponse body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getStatus()).isEqualTo(404);
        assertThat(body.getError()).isEqualTo("Not Found");
        assertThat(body.getMessage()).isEqualTo("Reminder not found with id: 42");
        assertThat(body.getPath()).isEqualTo("/reminders/42");
    }

    @Test
    @DisplayName("handleBusinessRuleViolation should return 400 with error message and path")
    void handleBusinessRuleViolation_returns400AndMessage() {
        BusinessRuleViolationException ex = new BusinessRuleViolationException("Invalid reminder date");
        HttpServletRequest request = requestWithPath("/reminders");

        ResponseEntity<ApiErrorResponse> response = handler.handleBusinessRuleViolation(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        ApiErrorResponse body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getStatus()).isEqualTo(400);
        assertThat(body.getError()).isEqualTo("Bad Request");
        assertThat(body.getMessage()).isEqualTo("Invalid reminder date");
        assertThat(body.getPath()).isEqualTo("/reminders");
    }

    @Test
    @DisplayName("handleValidationErrors should return 400 with field error details")
    void handleValidationErrors_returns400WithFieldErrors() {
        // Build a BindingResult with a fake field error
        Object target = new Object();
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(target, "reminderRequest");

        bindingResult.addError(new FieldError(
                "reminderRequest",
                "text",
                "must not be blank"
        ));

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);

        HttpServletRequest request = requestWithPath("/reminders");

        ResponseEntity<ApiErrorResponse> response = handler.handleValidationErrors(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        ApiErrorResponse body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getStatus()).isEqualTo(400);
        assertThat(body.getError()).isEqualTo("Bad Request");
        assertThat(body.getMessage()).isEqualTo("Validation failed");
        assertThat(body.getPath()).isEqualTo("/reminders");

        Map<String, String> fieldErrors = body.getFieldErrors();
        assertThat(fieldErrors)
                .isNotNull()
                .containsEntry("text", "must not be blank");
    }

    @Test
    @DisplayName("handleUnexpected should return 500 with generic message and path")
    void handleUnexpected_returns500AndGenericMessage() {
        Exception ex = new RuntimeException("RUNTIME EXCEPTION MSG");
        MockHttpServletRequest request = requestWithPath("/some-endpoint");

        ResponseEntity<ApiErrorResponse> response = handler.handleUnexpected(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

        ApiErrorResponse body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getStatus()).isEqualTo(500);
        assertThat(body.getError()).isEqualTo("Internal Server Error");
        assertThat(body.getMessage()).isEqualTo("Unexpected error occurred");
        assertThat(body.getPath()).isEqualTo("/some-endpoint");
    }

    @Test
    @DisplayName("handleTypeMismatch should return 400 with detailed message and path")
    void handleTypeMismatch_returns400WithMessageAndPath() {
        MethodArgumentTypeMismatchException ex =
                new MethodArgumentTypeMismatchException(
                        "foo",               // rejected value
                        Integer.class,       // required type
                        "year",              // parameter name
                        null,                // MethodParameter (not needed for our handler)
                        new IllegalArgumentException("For input string: \"foo\"")
                );

        HttpServletRequest request = requestWithPath("/holidays");

        ResponseEntity<ApiErrorResponse> response = handler.handleTypeMismatch(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        ApiErrorResponse body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getStatus()).isEqualTo(400);
        assertThat(body.getError()).isEqualTo("Bad Request");
        assertThat(body.getMessage()).isEqualTo("Invalid value 'foo' for parameter 'year'");
        assertThat(body.getPath()).isEqualTo("/holidays");
    }
}