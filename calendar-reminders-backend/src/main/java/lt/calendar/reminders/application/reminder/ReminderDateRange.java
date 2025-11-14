package lt.calendar.reminders.application.reminder;

import java.time.LocalDate;

public record ReminderDateRange(
        LocalDate minDate,
        LocalDate maxDate
) { }
