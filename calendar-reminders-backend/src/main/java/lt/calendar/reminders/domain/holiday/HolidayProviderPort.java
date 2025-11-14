package lt.calendar.reminders.domain.holiday;

import java.util.List;

public interface HolidayProviderPort {

    List<PublicHoliday> getPublicHolidays(int year);

}