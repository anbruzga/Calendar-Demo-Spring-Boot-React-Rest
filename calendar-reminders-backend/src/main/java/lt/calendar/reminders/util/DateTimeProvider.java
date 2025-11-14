package lt.calendar.reminders.util;

import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDateTime;

@Component
public class DateTimeProvider {

    private static Clock staticClock;

    public DateTimeProvider(Clock clock) {
        staticClock = clock;
    }

    public static LocalDateTime now() {
        if (staticClock == null) {
            return LocalDateTime.now();
        }
        return LocalDateTime.now(staticClock);
    }
}
