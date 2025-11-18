package lt.calendar.reminders.domain.holiday;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record PublicHoliday(
        LocalDate date,
        String localName,   // Lithuanian
        String englishName,
        String countryCode,
        String type,
        boolean global
) { }