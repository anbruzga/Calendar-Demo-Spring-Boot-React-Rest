package lt.calendar.reminders.web.holiday;

import lt.calendar.reminders.application.holiday.HolidayService;
import lt.calendar.reminders.domain.holiday.PublicHoliday;
import lt.calendar.reminders.web.holiday.dto.PublicHolidayResponse;
import lt.calendar.reminders.web.holiday.mapper.HolidayApiMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HolidayController.class)
class HolidayControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private HolidayService holidayService;

    @MockitoBean
    private HolidayApiMapper holidayApiMapper;

    @MockitoBean
    private Clock clock;

    @BeforeEach
    void setUp() {
        ZoneId zone = ZoneId.systemDefault();
        LocalDate fixedDate = LocalDate.of(2025, 1, 1);
        Instant fixedInstant = fixedDate.atStartOfDay(zone).toInstant();

        when(clock.getZone()).thenReturn(zone);
        when(clock.instant()).thenReturn(fixedInstant);
    }

    @Test
    @DisplayName("GET /holidays without year should use current year and return holidays")
    void getHolidays_withoutYear_usesCurrentYear() throws Exception {
        int currentYear = LocalDate.now(clock).getYear();
        LocalDate date = LocalDate.of(currentYear, 1, 1);

        var domainHoliday = new PublicHoliday(
                date,
                "Naujieji metai",
                "New Year's Day",
                "LT",
                "Public",
                true
        );

        var responseDto = new PublicHolidayResponse(
                date,
                "Naujieji metai",
                "New Year's Day",
                "LT",
                "Public",
                true
        );

        when(holidayService.getPublicHolidays(anyInt())).thenReturn(List.of(domainHoliday));
        when(holidayApiMapper.toResponse(domainHoliday)).thenReturn(responseDto);

        mockMvc.perform(get("/holidays")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].date").value(date.toString()))
                .andExpect(jsonPath("$[0].localName").value("Naujieji metai"))
                .andExpect(jsonPath("$[0].englishName").value("New Year's Day"));

        ArgumentCaptor<Integer> yearCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(holidayService).getPublicHolidays(yearCaptor.capture());
        assertThat(yearCaptor.getValue()).isEqualTo(currentYear);
    }

    @Test
    @DisplayName("GET /holidays?year=2025 should call service with that year")
    void getHolidays_withYear_usesProvidedYear() throws Exception {
        int year = 2025;
        LocalDate date = LocalDate.of(year, 3, 11);

        var domainHoliday = new PublicHoliday(
                date,
                "Nepriklausomybės atkūrimo diena",
                "Restoration of Independence of Lithuania",
                "LT",
                "Public",
                true
        );

        var responseDto = new PublicHolidayResponse(
                date,
                "Nepriklausomybės atkūrimo diena",
                "Restoration of Independence of Lithuania",
                "LT",
                "Public",
                true
        );

        when(holidayService.getPublicHolidays(year)).thenReturn(List.of(domainHoliday));
        when(holidayApiMapper.toResponse(domainHoliday)).thenReturn(responseDto);

        mockMvc.perform(get("/holidays")
                        .param("year", String.valueOf(year))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].date").value(date.toString()))
                .andExpect(jsonPath("$[0].localName").value("Nepriklausomybės atkūrimo diena"));

        verify(holidayService).getPublicHolidays(year);
    }

    @Test
    @DisplayName("GET /holidays should return empty array when service returns no holidays")
    void getHolidays_returnsEmptyArrayWhenServiceEmpty() throws Exception {
        when(holidayService.getPublicHolidays(anyInt())).thenReturn(List.of());

        mockMvc.perform(get("/holidays")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(holidayService).getPublicHolidays(anyInt());
    }

    @Test
    @DisplayName("GET /holidays?year=foo should return 400 for invalid year parameter")
    void getHolidays_invalidYearParam_returns400() throws Exception {
        mockMvc.perform(get("/holidays")
                        .param("year", "foo")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(holidayService);
    }
}
