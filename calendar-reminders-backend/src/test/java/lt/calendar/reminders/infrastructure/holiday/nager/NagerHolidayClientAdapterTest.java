package lt.calendar.reminders.infrastructure.holiday.nager;

import lt.calendar.reminders.domain.holiday.PublicHoliday;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import org.springframework.test.web.client.MockRestServiceServer;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

class NagerHolidayClientAdapterTest {

    private RestTemplate restTemplate;
    private MockRestServiceServer server;
    private NagerHolidayProperties properties;
    private NagerHolidayClientAdapter adapter;

    private String getBaseUrlFromProperties(int year) {
        return properties.getBaseUrl()
                + "/PublicHolidays/"
                + year
                + "/"
                + properties.getCountryCode();
    }

    @BeforeEach
    void setUp() {
        // Use a RestTemplate we can attach the mock server to
        this.restTemplate = new RestTemplate();
        this.server = MockRestServiceServer.createServer(restTemplate);

        this.properties = new NagerHolidayProperties();
        properties.setBaseUrl("https://date.nager.at/api/v3");
        properties.setCountryCode("LT");

        this.adapter = new NagerHolidayClientAdapter(restTemplate, properties);
    }

    @Test
    @DisplayName("getPublicHolidays should map Nager API response to domain objects")
    void getPublicHolidays_mapsResponseCorrectly() {
        int year = 2025;
        String url = getBaseUrlFromProperties(year);

        String json = """
                [
                  {
                    "date": "2025-01-01",
                    "localName": "Naujieji metai",
                    "name": "New Year's Day",
                    "countryCode": "LT",
                    "fixed": true,
                    "global": true,
                    "type": "Public"
                  }
                ]
                """;

        server.expect(requestTo(url)).andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

        List<PublicHoliday> result = adapter.getPublicHolidays(year);

        server.verify();

        assertThat(result).hasSize(1);
        PublicHoliday holiday = result.getFirst();
        assertThat(holiday.date()).isEqualTo(LocalDate.of(2025, 1, 1));
        assertThat(holiday.localName()).isEqualTo("Naujieji metai");
        assertThat(holiday.englishName()).isEqualTo("New Year's Day");
        assertThat(holiday.countryCode()).isEqualTo("LT");
        assertThat(holiday.type()).isEqualTo("Public");
        assertThat(holiday.global()).isTrue();
    }

    @Test
    @DisplayName("getPublicHolidays should cache results per year and not call API again")
    void getPublicHolidays_usesCachePerYear() {
        int year = 2025;
        String url = getBaseUrlFromProperties(year);

        String json = """
                [
                  {
                    "date": "2025-02-16",
                    "localName": "Valstybės atkūrimo diena",
                    "name": "Restoration of the State Day",
                    "countryCode": "LT",
                    "fixed": true,
                    "global": true,
                    "type": "Public"
                  }
                ]
                """;

        server.expect(requestTo(url)).andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

        List<PublicHoliday> firstCall = adapter.getPublicHolidays(year);
        List<PublicHoliday> secondCall = adapter.getPublicHolidays(year);

        server.verify();

        assertThat(firstCall).hasSize(1);
        assertThat(secondCall).hasSize(1);
        assertThat(secondCall.getFirst().date()).isEqualTo(LocalDate.of(year, 2, 16));
    }

    @Test
    @DisplayName("getPublicHolidays should return empty list when API fails")
    void getPublicHolidays_returnsEmptyListOnError() {
        int year = 2025;
        String url = getBaseUrlFromProperties(year);

        server.expect(requestTo(url)).andRespond(withServerError());

        List<PublicHoliday> result = adapter.getPublicHolidays(year);

        server.verify();

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("getPublicHolidays should return empty list when API returns empty array")
    void getPublicHolidays_returnsEmptyListWhenBodyEmpty() {
        int year = 2025;
        String url = getBaseUrlFromProperties(year);

        String json = "[]";

        server.expect(requestTo(url))
                .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

        List<PublicHoliday> result = adapter.getPublicHolidays(year);

        server.verify();

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("getPublicHolidays should handle null element in response array gracefully")
    void getPublicHolidays_handlesNullElementInArray() {
        int year = 2025;
        String url = getBaseUrlFromProperties(year);

        String json = "[null]";

        server.expect(requestTo(url))
                .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

        List<PublicHoliday> result = adapter.getPublicHolidays(year);

        server.verify();

        assertThat(result).isEmpty();
    }


    @Test
    @DisplayName("getPublicHolidays should map multiple holidays correctly")
    void getPublicHolidays_mapsMultipleEntries() {
        int year = 2025;
        String url = getBaseUrlFromProperties(year);

        String json = """
                [
                  {
                    "date": "2025-01-01",
                    "localName": "Naujieji metai",
                    "name": "New Year's Day",
                    "countryCode": "LT",
                    "fixed": true,
                    "global": true,
                    "type": "Public"
                  },
                  {
                    "date": "2025-02-16",
                    "localName": "Valstybės atkūrimo diena",
                    "name": "Restoration of the State Day",
                    "countryCode": "LT",
                    "fixed": true,
                    "global": true,
                    "type": "Public"
                  }
                ]
                """;

        server.expect(requestTo(url)).andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

        List<PublicHoliday> result = adapter.getPublicHolidays(year);

        server.verify();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).date()).isEqualTo(LocalDate.of(year, 1, 1));
        assertThat(result.get(1).date()).isEqualTo(LocalDate.of(year, 2, 16));
    }

    @Test
    @DisplayName("getPublicHolidays should not reuse cache across different years")
    void getPublicHolidays_cacheIsPerYear() {
        int year2025 = 2025;
        int year2026 = 2026;

        String url2025 = getBaseUrlFromProperties(year2025);

        String url2026 =  getBaseUrlFromProperties(year2026);

        String json2025 = """
                [
                  {
                    "date": "2025-01-01",
                    "localName": "Naujieji metai",
                    "name": "New Year's Day",
                    "countryCode": "LT",
                    "fixed": true,
                    "global": true,
                    "type": "Public"
                  }
                ]
                """;

        String json2026 = """
                [
                  {
                    "date": "2026-01-01",
                    "localName": "Naujieji metai",
                    "name": "New Year's Day",
                    "countryCode": "LT",
                    "fixed": true,
                    "global": true,
                    "type": "Public"
                  }
                ]
                """;

        server.expect(requestTo(url2025)).andRespond(withSuccess(json2025, MediaType.APPLICATION_JSON));

        server.expect(requestTo(url2026)).andRespond(withSuccess(json2026, MediaType.APPLICATION_JSON));

        List<PublicHoliday> holidays2025 = adapter.getPublicHolidays(year2025);
        List<PublicHoliday> holidays2026 = adapter.getPublicHolidays(year2026);

        server.verify();

        assertThat(holidays2025).hasSize(1);
        assertThat(holidays2025.getFirst().date()).isEqualTo(LocalDate.of(year2025, 1, 1));

        assertThat(holidays2026).hasSize(1);
        assertThat(holidays2026.getFirst().date()).isEqualTo(LocalDate.of(year2026, 1, 1));
    }

    @Test
    @DisplayName("getPublicHolidays should return empty list when response body is null")
    void getPublicHolidays_returnsEmptyListWhenBodyNull() {
        int year = 2025;
        String url = getBaseUrlFromProperties(year);

        server.expect(requestTo(url)).andRespond(withNoContent());

        List<PublicHoliday> result = adapter.getPublicHolidays(year);

        server.verify();

        assertThat(result).isEmpty();
    }


}
