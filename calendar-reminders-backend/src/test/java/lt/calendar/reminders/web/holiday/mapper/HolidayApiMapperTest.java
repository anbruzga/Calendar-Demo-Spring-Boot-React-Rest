package lt.calendar.reminders.web.holiday.mapper;

import lt.calendar.reminders.domain.holiday.PublicHoliday;
import lt.calendar.reminders.web.holiday.dto.PublicHolidayResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class HolidayApiMapperTest {

    private final HolidayApiMapper mapper = new HolidayApiMapper();

    @Test
    @DisplayName("toResponse should map all fields from domain to DTO")
    void toResponse_mapsAllFields() {
        LocalDate date = LocalDate.of(2025, 3, 11);

        var domain = new PublicHoliday(
                date,
                "Nepriklausomybės atkūrimo diena",
                "Restoration of Independence of Lithuania",
                "LT",
                "Public",
                true
        );

        PublicHolidayResponse dto = mapper.toResponse(domain);

        assertThat(dto.date()).isEqualTo(date);
        assertThat(dto.localName()).isEqualTo("Nepriklausomybės atkūrimo diena");
        assertThat(dto.englishName()).isEqualTo("Restoration of Independence of Lithuania");
        assertThat(dto.countryCode()).isEqualTo("LT");
        assertThat(dto.type()).isEqualTo("Public");
        assertThat(dto.global()).isTrue();
    }

    @Test
    @DisplayName("toResponse should preserve special characters and false global flag")
    void toResponse_preservesSpecialCharactersAndFlags() {
        LocalDate date = LocalDate.of(2025, 6, 24);

        var domain = new PublicHoliday(
                date,
                "Joninės / Rasos šventė",
                "St. John's Day",
                "LT",
                "Observance",
                false
        );

        PublicHolidayResponse dto = mapper.toResponse(domain);

        assertThat(dto.date()).isEqualTo(date);
        assertThat(dto.localName()).isEqualTo("Joninės / Rasos šventė");
        assertThat(dto.englishName()).isEqualTo("St. John's Day");
        assertThat(dto.countryCode()).isEqualTo("LT");
        assertThat(dto.type()).isEqualTo("Observance");
        assertThat(dto.global()).isFalse();
    }

}
