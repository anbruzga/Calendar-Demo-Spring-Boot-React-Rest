package lt.calendar.reminders.application.reminder;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultDateRangePolicyTest {

    private final Clock clock = Clock.fixed(
            Instant.parse("2025-01-01T00:00:00Z"),
            ZoneOffset.UTC
    );

    private final DateRangePolicy policy = new DefaultDateRangePolicy(clock);

    @Test
    @DisplayName("isWithinAllowedRange should accept today")
    void isWithinAllowedRange_today_isAllowed() {
        LocalDate today = LocalDate.now(clock);

        boolean result = policy.isWithinAllowedRange(today);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("isWithinAllowedRange should accept date one year from today")
    void isWithinAllowedRange_oneYearFromToday_isAllowed() {
        LocalDate oneYearFromToday = LocalDate.now(clock).plusYears(1);

        boolean result = policy.isWithinAllowedRange(oneYearFromToday);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("isWithinAllowedRange should reject date beyond one year from today")
    void isWithinAllowedRange_beyondOneYear_isRejected() {
        LocalDate tooFar = LocalDate.now(clock).plusYears(1).plusDays(1);

        boolean result = policy.isWithinAllowedRange(tooFar);

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("isWithinAllowedRange should reject date before today")
    void isWithinAllowedRange_beforeToday_isRejected() {
        LocalDate yesterday = LocalDate.now(clock).minusDays(1);

        boolean result = policy.isWithinAllowedRange(yesterday);

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("getMinDate should return today based on injected Clock")
    void getMinDate_returnsToday() {
        LocalDate expectedMin = LocalDate.now(clock);

        LocalDate minDate = policy.getMinDate();

        assertThat(minDate).isEqualTo(expectedMin);
    }

    @Test
    @DisplayName("getMaxDate should return one year from today based on injected Clock")
    void getMaxDate_returnsOneYearFromToday() {
        LocalDate expectedMax = LocalDate.now(clock).plusYears(1);

        LocalDate maxDate = policy.getMaxDate();

        assertThat(maxDate).isEqualTo(expectedMax);
    }
}
