package lt.calendar.reminders.application.reminder;

import java.time.LocalDate;

public interface DateRangePolicy {

    ReminderDateRange getCurrentRange();


    default boolean isWithinAllowedRange(LocalDate date) {
        ReminderDateRange range = getCurrentRange();
        return !date.isBefore(range.minDate()) && !date.isAfter(range.maxDate());
    }

    default LocalDate getMinDate() {
        return getCurrentRange().minDate();
    }

    // todo get max and min still can theoretically have race conditions on 23:59 if used directly
    default LocalDate getMaxDate() {
        return getCurrentRange().maxDate();
    }
}
