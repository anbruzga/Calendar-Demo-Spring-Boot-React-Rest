package lt.calendar.reminders.application.holiday;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lt.calendar.reminders.domain.holiday.HolidayProviderPort;
import lt.calendar.reminders.domain.holiday.PublicHoliday;
import lt.calendar.reminders.util.MyStopWatch;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class HolidayService {

    private final HolidayProviderPort holidayProviderPort;

    public List<PublicHoliday> getPublicHolidays(int year) {
        log.debug("Fetching public holidays for year {}", year);
        MyStopWatch stopWatch = new MyStopWatch();

        List<PublicHoliday> holidays = Optional
                .ofNullable(holidayProviderPort.getPublicHolidays(year))
                .orElseGet(List::of);

        log.debug("Fetched {} public holidays for year {} in {} ms",
                holidays.size(), year, stopWatch.stopAndGetMillis());

        return holidays;
    }

    public boolean isPublicHoliday(LocalDate date) {
        Objects.requireNonNull(date, "date must not be null");
        int year = date.getYear();
        log.debug("Checking if date {} is a public holiday (year {})", date, year);
        MyStopWatch stopWatch = new MyStopWatch();
        boolean isHoliday = Optional
                .ofNullable(holidayProviderPort.getPublicHolidays(year))
                .orElseGet(List::of)
                .stream()
                .anyMatch(holiday -> date.equals(holiday.date()));
        log.debug("Date {} public holiday result: {}; in {} ms", date, isHoliday, stopWatch.stopAndGetMillis());
        return isHoliday;
    }
}