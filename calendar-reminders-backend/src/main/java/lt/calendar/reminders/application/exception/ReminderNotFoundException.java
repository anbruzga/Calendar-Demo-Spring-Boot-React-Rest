package lt.calendar.reminders.application.exception;

public class ReminderNotFoundException extends RuntimeException {

  public ReminderNotFoundException(String message) {
    super(message);
  }
}