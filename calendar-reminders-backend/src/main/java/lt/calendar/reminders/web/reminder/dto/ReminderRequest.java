package lt.calendar.reminders.web.reminder.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Setter
@Getter
public class ReminderRequest {

    @NotBlank(message = "Reminder text must not be blank")
    @Size(max = 255, message = "Reminder text must be at most 255 characters")
    private String text;

    @NotNull(message = "Reminder date is required")
    private LocalDate date;

    @NotNull(message = "Reminder time is required")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime time;

}