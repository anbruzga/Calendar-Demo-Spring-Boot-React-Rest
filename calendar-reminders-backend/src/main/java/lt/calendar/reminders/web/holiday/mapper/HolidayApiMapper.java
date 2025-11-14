package lt.calendar.reminders.web.holiday.mapper;

import lt.calendar.reminders.domain.holiday.PublicHoliday;
import lt.calendar.reminders.web.holiday.dto.PublicHolidayResponse;
import org.springframework.stereotype.Component;

@Component
public class HolidayApiMapper {

    public PublicHolidayResponse toResponse(PublicHoliday holiday) {
        return new PublicHolidayResponse(
                holiday.date(),
                holiday.localName(),
                holiday.englishName(),
                holiday.countryCode(),
                holiday.type(),
                holiday.global()
        );
    }

}