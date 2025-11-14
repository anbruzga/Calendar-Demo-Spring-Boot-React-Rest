package lt.calendar.reminders.web.reminder.dto;

import java.time.LocalDate;

public record AllowedDateRangeResponse (
        LocalDate minDate,
        LocalDate maxDate
) { }
