package lt.calendar.reminders.domain.reminder;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Reminder {

    @EqualsAndHashCode.Include
    private Long id;
    private String text;
    private LocalDate date;
    private LocalTime time;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}