package lt.calendar.reminders.infrastructure.holiday.nager;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "calendar.holidays.nager")
public class NagerHolidayProperties {

    private String baseUrl; // links to .properties todo
    private String countryCode;  // links to .properties

}
