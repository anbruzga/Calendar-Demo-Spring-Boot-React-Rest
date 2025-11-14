package lt.calendar.reminders.web.reminder.mapper;

import lt.calendar.reminders.domain.reminder.Reminder;
import lt.calendar.reminders.web.reminder.dto.ReminderRequest;
import lt.calendar.reminders.web.reminder.dto.ReminderResponse;
import org.springframework.stereotype.Component;

@Component
public class ReminderApiMapper {

    public Reminder toDomain(ReminderRequest request) {
        return Reminder.builder()
                .text(request.getText())
                .date(request.getDate())
                .time(request.getTime())
                .build();
    }

    public ReminderResponse toResponse(Reminder reminder) {
        ReminderResponse response = new ReminderResponse();
        response.setId(reminder.getId());
        response.setText(reminder.getText());
        response.setDate(reminder.getDate());
        response.setTime(reminder.getTime());
        response.setCreatedAt(reminder.getCreatedAt());
        response.setUpdatedAt(reminder.getUpdatedAt());
        return response;
    }
}