package lt.calendar.reminders.application.holiday;

import lt.calendar.reminders.domain.holiday.HolidayProviderPort;
import lt.calendar.reminders.domain.holiday.PublicHoliday;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class HolidayServiceTest {

    private final HolidayProviderPort holidayProviderPort = mock(HolidayProviderPort.class);
    private final HolidayService holidayService = new HolidayService(holidayProviderPort);

    @Test
    @DisplayName("getPublicHolidays should delegate to provider port")
    void getPublicHolidays_delegatesToProvider() {
        int year = 2025;
        var holiday = new PublicHoliday(
                LocalDate.of(2025, 1, 1),
                "Naujieji metai",
                "New Year's Day",
                "LT",
                "Public",
                true
        );

        when(holidayProviderPort.getPublicHolidays(year)).thenReturn(List.of(holiday));

        List<PublicHoliday> result = holidayService.getPublicHolidays(year);

        assertThat(result)
                .hasSize(1)
                .containsExactly(holiday);

        verify(holidayProviderPort).getPublicHolidays(year);
        verifyNoMoreInteractions(holidayProviderPort);
    }

    @Test
    @DisplayName("isPublicHoliday should return true when date is in provider's list")
    void isPublicHoliday_returnsTrueWhenDateMatches() {
        LocalDate date = LocalDate.of(2025, 1, 1);
        int year = date.getYear();

        var holiday = new PublicHoliday(
                date,
                "Naujieji metai",
                "New Year's Day",
                "LT",
                "Public",
                true
        );

        when(holidayProviderPort.getPublicHolidays(year)).thenReturn(List.of(holiday));

        boolean result = holidayService.isPublicHoliday(date);

        assertThat(result).isTrue();
        verify(holidayProviderPort).getPublicHolidays(year);
    }

    @Test
    @DisplayName("isPublicHoliday should return false when date is not in provider's list")
    void isPublicHoliday_returnsFalseWhenNoMatch() {
        LocalDate date = LocalDate.of(2025, 1, 2);
        int year = date.getYear();

        var anotherHoliday = new PublicHoliday(
                LocalDate.of(2025, 1, 1),
                "Naujieji metai",
                "New Year's Day",
                "LT",
                "Public",
                true
        );

        when(holidayProviderPort.getPublicHolidays(year)).thenReturn(List.of(anotherHoliday));

        boolean result = holidayService.isPublicHoliday(date);

        assertThat(result).isFalse();
        verify(holidayProviderPort).getPublicHolidays(year);
    }

    @Test
    @DisplayName("getPublicHolidays should return empty list when provider returns empty list")
    void getPublicHolidays_returnsEmptyWhenProviderEmpty() {
        int year = 2025;

        when(holidayProviderPort.getPublicHolidays(year)).thenReturn(List.of());

        List<PublicHoliday> result = holidayService.getPublicHolidays(year);

        assertThat(result).isEmpty();
        verify(holidayProviderPort).getPublicHolidays(year);
    }

    @Test
    @DisplayName("isPublicHoliday should return false when provider returns empty list")
    void isPublicHoliday_returnsFalseWhenProviderEmpty() {
        LocalDate date = LocalDate.of(2025, 5, 1);
        int year = date.getYear();

        when(holidayProviderPort.getPublicHolidays(year)).thenReturn(List.of());

        boolean result = holidayService.isPublicHoliday(date);

        assertThat(result).isFalse();
        verify(holidayProviderPort).getPublicHolidays(year);
    }


}
