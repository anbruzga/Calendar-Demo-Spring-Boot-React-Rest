package lt.calendar.reminders.domain.holiday;

import java.time.LocalDate;

public record PublicHoliday(
        LocalDate date,
        String localName,   // Lithuanian
        String englishName,
        String countryCode,
        String type,
        boolean global
) { }