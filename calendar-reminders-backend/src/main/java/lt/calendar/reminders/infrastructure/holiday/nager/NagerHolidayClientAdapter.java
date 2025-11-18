package lt.calendar.reminders.infrastructure.holiday.nager;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lt.calendar.reminders.domain.holiday.HolidayProviderPort;
import lt.calendar.reminders.domain.holiday.PublicHoliday;
import lt.calendar.reminders.util.MyStopWatch;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class NagerHolidayClientAdapter implements HolidayProviderPort {

    private final RestTemplate restTemplate;
    private final NagerHolidayProperties properties;

    private final Map<Integer, List<PublicHoliday>> cache = new ConcurrentHashMap<>();

    @Override
    public List<PublicHoliday> getPublicHolidays(int year) {
        return cache.compute(year, (y, existing) -> {
            if (existing != null && !existing.isEmpty()) {
                return existing;
            }
            return fetchFromApi(y);
        });
    }

    private List<PublicHoliday> fetchFromApi(int year) {
        String url = String.format(
                "%s/PublicHolidays/%d/%s",
                properties.getBaseUrl(),
                year,
                properties.getCountryCode()
        );

        log.info("Fetching public holidays from Nager API: {}", url);


        MyStopWatch stopWatch = new MyStopWatch();

        try {
            ResponseEntity<NagerHolidayDto[]> response =
                    restTemplate.getForEntity(url, NagerHolidayDto[].class);

            NagerHolidayDto[] body = response.getBody();
            if (body == null || body.length == 0) {
                log.warn("No holidays received from Nager API for year {} ({} ms)", year, stopWatch.stopAndGetMillis());
                return Collections.emptyList();
            }

            List<PublicHoliday> result = Arrays.stream(body)
                    .filter(Objects::nonNull)
                    .map(this::toDomain)
                    .toList();

            log.debug("Fetched {} holidays from Nager API for year {} in {} ms",
                    result.size(), year, stopWatch.stopAndGetMillis());

            return result;

        } catch (Exception ex) {
            if (stopWatch.isRunning()) {
                stopWatch.stop();
            }
            log.error("Failed to fetch public holidays from Nager API for year {}: \nException msg: {} \nException stacktrace: {} \nUrl: {}", year, ex.getMessage(), ex.getStackTrace(), url);
            return Collections.emptyList();
        }
    }

    private PublicHoliday toDomain(NagerHolidayDto dto) {
        return PublicHoliday.builder()
                .date(dto.date)
                .localName(dto.localName)
                .englishName(dto.name)
                .countryCode(dto.countryCode)
                .type(dto.type)
                .global(dto.global)
                .build();
    }

    /**
     * Internal DTO for Nager API JSON mapping.
     */
    private record NagerHolidayDto(
            LocalDate date,
            String localName,
            String name,
            String countryCode,
            boolean fixed,
            boolean global,
            String type
    ) {
    }
}