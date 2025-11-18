package lt.calendar.reminders.infrastructure.holiday.nager;

import lt.calendar.reminders.domain.holiday.HolidayProviderPort;
import lt.calendar.reminders.domain.holiday.PublicHoliday;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/*
 *   Offline/static holiday provider
 *   Activated when calendar.holidays.nager.use-static-mock=true
 */
@Component
@ConditionalOnProperty(
        prefix = "calendar.holidays.nager",
        name = "use-static-mock",
        havingValue = "true"
)
public class OfflineHolidayProvider implements HolidayProviderPort {

    private static final Map<Integer, List<PublicHoliday>> LT_HOLIDAYS_2025 = Map.of(
            2025, List.of(
                    PublicHoliday.builder()
                            .date(LocalDate.of(2025, 1, 1))
                            .localName("Naujieji metai")
                            .englishName("New Year's Day")
                            .countryCode("LT")
                            .type("Public")
                            .global(true)
                            .build(),
                    PublicHoliday.builder()
                            .date(LocalDate.of(2025, 2, 16))
                            .localName("Lietuvos valstybės atkūrimo diena")
                            .englishName("The Day of Restoration of the State of Lithuania")
                            .countryCode("LT")
                            .type("Public")
                            .global(true)
                            .build(),
                    PublicHoliday.builder()
                            .date(LocalDate.of(2025, 3, 11))
                            .localName("Lietuvos nepriklausomybės atkūrimo diena")
                            .englishName("Day of Restoration of Independence of Lithuania")
                            .countryCode("LT")
                            .type("Public")
                            .global(true)
                            .build(),
                    PublicHoliday.builder()
                            .date(LocalDate.of(2025, 4, 20))
                            .localName("Velykos")
                            .englishName("Easter Sunday")
                            .countryCode("LT")
                            .type("Public")
                            .global(true)
                            .build(),
                    PublicHoliday.builder()
                            .date(LocalDate.of(2025, 4, 21))
                            .localName("Antroji Velykų diena")
                            .englishName("Easter Monday")
                            .countryCode("LT")
                            .type("Public")
                            .global(true)
                            .build(),
                    PublicHoliday.builder()
                            .date(LocalDate.of(2025, 5, 1))
                            .localName("Tarptautinė darbo diena")
                            .englishName("International Working Day")
                            .countryCode("LT")
                            .type("Public")
                            .global(true)
                            .build(),
                    PublicHoliday.builder()
                            .date(LocalDate.of(2025, 6, 24))
                            .localName("Joninės, Rasos")
                            .englishName("St. John's Day")
                            .countryCode("LT")
                            .type("Public")
                            .global(true)
                            .build(),
                    PublicHoliday.builder()
                            .date(LocalDate.of(2025, 7, 6))
                            .localName("Valstybės diena")
                            .englishName("Statehood Day")
                            .countryCode("LT")
                            .type("Public")
                            .global(true)
                            .build(),
                    PublicHoliday.builder()
                            .date(LocalDate.of(2025, 8, 15))
                            .localName("Žolinė")
                            .englishName("Assumption Day")
                            .countryCode("LT")
                            .type("Public")
                            .global(true)
                            .build(),
                    PublicHoliday.builder()
                            .date(LocalDate.of(2025, 11, 1))
                            .localName("Visų šventųjų diena")
                            .englishName("All Saints' Day")
                            .countryCode("LT")
                            .type("Public")
                            .global(true)
                            .build(),
                    PublicHoliday.builder()
                            .date(LocalDate.of(2025, 11, 2))
                            .localName("Vėlinės")
                            .englishName("All Souls' Day")
                            .countryCode("LT")
                            .type("Public")
                            .global(true)
                            .build(),
                    PublicHoliday.builder()
                            .date(LocalDate.of(2025, 12, 24))
                            .localName("Šv. Kūčios")
                            .englishName("Christmas Eve")
                            .countryCode("LT")
                            .type("Public")
                            .global(true)
                            .build(),
                    PublicHoliday.builder()
                            .date(LocalDate.of(2025, 12, 25))
                            .localName("Šv. Kalėdos")
                            .englishName("Christmas Day")
                            .countryCode("LT")
                            .type("Public")
                            .global(true)
                            .build(),
                    PublicHoliday.builder()
                            .date(LocalDate.of(2025, 12, 26))
                            .localName("Šv. Kalėdos")
                            .englishName("St. Stephen's Day")
                            .countryCode("LT")
                            .type("Public")
                            .global(true)
                            .build()
            )
    );

    private static final Map<Integer, List<PublicHoliday>> LT_HOLIDAYS_2026 = Map.of(
            2026, List.of(
                    PublicHoliday.builder()
                            .date(LocalDate.of(2026, 1, 1))
                            .localName("Naujieji metai")
                            .englishName("New Year's Day")
                            .countryCode("LT")
                            .type("Public")
                            .global(true)
                            .build(),
                    PublicHoliday.builder()
                            .date(LocalDate.of(2026, 2, 16))
                            .localName("Lietuvos valstybės atkūrimo diena")
                            .englishName("The Day of Restoration of the State of Lithuania")
                            .countryCode("LT")
                            .type("Public")
                            .global(true)
                            .build(),
                    PublicHoliday.builder()
                            .date(LocalDate.of(2026, 3, 11))
                            .localName("Lietuvos nepriklausomybės atkūrimo diena")
                            .englishName("Day of Restoration of Independence of Lithuania")
                            .countryCode("LT")
                            .type("Public")
                            .global(true)
                            .build(),
                    PublicHoliday.builder()
                            .date(LocalDate.of(2026, 4, 5))
                            .localName("Velykos")
                            .englishName("Easter Sunday")
                            .countryCode("LT")
                            .type("Public")
                            .global(true)
                            .build(),
                    PublicHoliday.builder()
                            .date(LocalDate.of(2026, 4, 6))
                            .localName("Antroji Velykų diena")
                            .englishName("Easter Monday")
                            .countryCode("LT")
                            .type("Public")
                            .global(true)
                            .build(),
                    PublicHoliday.builder()
                            .date(LocalDate.of(2026, 5, 1))
                            .localName("Tarptautinė darbo diena")
                            .englishName("International Working Day")
                            .countryCode("LT")
                            .type("Public")
                            .global(true)
                            .build(),
                    PublicHoliday.builder()
                            .date(LocalDate.of(2026, 6, 24))
                            .localName("Joninės, Rasos")
                            .englishName("St. John's Day")
                            .countryCode("LT")
                            .type("Public")
                            .global(true)
                            .build(),
                    PublicHoliday.builder()
                            .date(LocalDate.of(2026, 7, 6))
                            .localName("Valstybės diena")
                            .englishName("Statehood Day")
                            .countryCode("LT")
                            .type("Public")
                            .global(true)
                            .build(),
                    PublicHoliday.builder()
                            .date(LocalDate.of(2026, 8, 15))
                            .localName("Žolinė")
                            .englishName("Assumption Day")
                            .countryCode("LT")
                            .type("Public")
                            .global(true)
                            .build(),
                    PublicHoliday.builder()
                            .date(LocalDate.of(2026, 11, 1))
                            .localName("Visų šventųjų diena")
                            .englishName("All Saints' Day")
                            .countryCode("LT")
                            .type("Public")
                            .global(true)
                            .build(),
                    PublicHoliday.builder()
                            .date(LocalDate.of(2026, 11, 2))
                            .localName("Vėlinės")
                            .englishName("All Souls' Day")
                            .countryCode("LT")
                            .type("Public")
                            .global(true)
                            .build(),
                    PublicHoliday.builder()
                            .date(LocalDate.of(2026, 12, 24))
                            .localName("Šv. Kūčios")
                            .englishName("Christmas Eve")
                            .countryCode("LT")
                            .type("Public")
                            .global(true)
                            .build(),
                    PublicHoliday.builder()
                            .date(LocalDate.of(2026, 12, 25))
                            .localName("Šv. Kalėdos")
                            .englishName("Christmas Day")
                            .countryCode("LT")
                            .type("Public")
                            .global(true)
                            .build(),
                    PublicHoliday.builder()
                            .date(LocalDate.of(2026, 12, 26))
                            .localName("Šv. Kalėdos")
                            .englishName("St. Stephen's Day")
                            .countryCode("LT")
                            .type("Public")
                            .global(true)
                            .build()
            )
    );


    @Override
    public List<PublicHoliday> getPublicHolidays(int year) {
        return switch (year) {
            case 2025 -> LT_HOLIDAYS_2025.getOrDefault(year, List.of());
            case 2026 -> LT_HOLIDAYS_2026.getOrDefault(year, List.of());
            default -> List.of();
        };
    }

}
