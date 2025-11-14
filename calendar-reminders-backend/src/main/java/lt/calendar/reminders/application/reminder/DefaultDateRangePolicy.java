package lt.calendar.reminders.application.reminder;

import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDate;

@Component
public class DefaultDateRangePolicy implements DateRangePolicy {

    private final Clock clock;

    public DefaultDateRangePolicy(Clock clock) {
        this.clock = clock;
    }

    private LocalDate today() {
        return LocalDate.now(clock);
    }

    @Override
    public ReminderDateRange getCurrentRange() {
        LocalDate today = today();
        return new ReminderDateRange(today, today.plusYears(1));
    }
}
