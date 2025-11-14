package lt.calendar.reminders.web.holiday;

import lombok.extern.slf4j.Slf4j;
import lt.calendar.reminders.application.holiday.HolidayService;
import lt.calendar.reminders.domain.holiday.PublicHoliday;
import lt.calendar.reminders.web.holiday.dto.PublicHolidayResponse;
import lt.calendar.reminders.web.holiday.mapper.HolidayApiMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/holidays")
public class HolidayController {

    private final HolidayService holidayService;
    private final HolidayApiMapper holidayApiMapper;
    private final Clock clock;

    public HolidayController(HolidayService holidayService, HolidayApiMapper holidayApiMapper, Clock clock) {
        this.holidayService = holidayService;
        this.holidayApiMapper = holidayApiMapper;
        this.clock = clock;
    }

    /**
     * GET /holidays?year=2025
     * year is optional, defaults to current year.
     */
    @GetMapping
    public List<PublicHolidayResponse> getHolidays(
            @RequestParam(value = "year", required = false) Integer year) {

        int resolvedYear = (year != null) ? year : LocalDate.now(clock).getYear();

        List<PublicHoliday> holidays = holidayService.getPublicHolidays(resolvedYear);
        log.info("Fetched {} holidays for year {}", holidays.size(), resolvedYear);

        return holidays.stream()
                .map(holidayApiMapper::toResponse)
                .toList();
    }
}