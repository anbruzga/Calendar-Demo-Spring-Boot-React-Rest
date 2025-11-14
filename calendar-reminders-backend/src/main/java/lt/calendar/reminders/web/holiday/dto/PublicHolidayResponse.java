package lt.calendar.reminders.web.holiday.dto;

import java.time.LocalDate;

public record PublicHolidayResponse(
        LocalDate date,
        String localName,
        String englishName,
        String countryCode,
        String type,
        boolean global
) {
}
