package lt.calendar.reminders.application.reminder;

import lombok.extern.slf4j.Slf4j;
import lt.calendar.reminders.application.exception.BusinessRuleViolationException;
import lt.calendar.reminders.application.exception.ReminderNotFoundException;
import lt.calendar.reminders.domain.reminder.Reminder;
import lt.calendar.reminders.domain.reminder.ReminderRepositoryPort;
import lombok.RequiredArgsConstructor;
import lt.calendar.reminders.util.MyStopWatch;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReminderService {

    private final ReminderRepositoryPort reminderRepositoryPort;
    private final DateRangePolicy dateRangePolicy;

    @Transactional(readOnly = true)
    public List<Reminder> getAllReminders() {
        log.debug("Fetching all reminders");
        MyStopWatch stopWatch = new MyStopWatch();
        List<Reminder> all = reminderRepositoryPort.findAll();
        log.debug("Fetched all reminders in {} ms", stopWatch.stopAndGetMillis());

        return all;
    }

    @Transactional(readOnly = true)
    public List<Reminder> getRemindersForDate(LocalDate date) {
        Objects.requireNonNull(date, "date must not be null");

        MyStopWatch stopWatch = new MyStopWatch();

        List<Reminder> reminders = reminderRepositoryPort.findByDate(date);

        log.debug("Fetched {} reminders for date {} in {} ms",
                reminders.size(), date, stopWatch.stopAndGetMillis());

        return reminders;
    }

    @Transactional
    public Reminder createReminder(Reminder reminder) {
        Objects.requireNonNull(reminder, "Reminder must not be null");

        log.debug("Creating reminder for date {}", reminder.getDate());
        validateReminderDate(reminder.getDate());

        MyStopWatch stopWatch = new MyStopWatch();
        Reminder created = reminderRepositoryPort.save(reminder);

        log.debug("Created reminder for date {} in {} ms", created.getDate(), stopWatch.stopAndGetMillis());
        return created;
    }

    @Transactional
    public Reminder updateReminder(Long id, Reminder updated) {
        Objects.requireNonNull(updated, "Updated reminder must not be null");

        log.debug("Updating reminder with id {}", id);
        MyStopWatch stopWatch = new MyStopWatch();

        Reminder existing = reminderRepositoryPort.findById(id)
                .orElseThrow(() -> new ReminderNotFoundException("Reminder not found with id: " + id));

        validateReminderDate(updated.getDate());

        Reminder toSave = Reminder.builder()
                .id(existing.getId())
                .text(updated.getText())
                .date(updated.getDate())
                .time(updated.getTime())
                .createdAt(existing.getCreatedAt())
                .updatedAt(existing.getUpdatedAt())
                .build();

        Reminder saved = reminderRepositoryPort.save(toSave);

        log.debug("Updated reminder in {} ms", stopWatch.stopAndGetMillis());
        return saved;
    }

    @Transactional
    public void deleteReminder(Long id) {
        log.debug("Deleting reminder with id {}", id);
        MyStopWatch stopWatch = new MyStopWatch();

        reminderRepositoryPort.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Reminder not found with id: " + id));

        reminderRepositoryPort.deleteById(id);
        log.debug("Deleted reminder with id {} in {} ms", id, stopWatch.stopAndGetMillis());
    }

    @Transactional
    public void deleteRemindersByDate(LocalDate date) {
        Objects.requireNonNull(date, "date must not be null");

        log.debug("Deleting reminders for date {}", date);
        MyStopWatch stopWatch = new MyStopWatch();
        reminderRepositoryPort.deleteByDate(date);
        log.debug("Deleted reminders for date {} in {} ms", date, stopWatch.stopAndGetMillis());
    }

    private void validateReminderDate(LocalDate date) {
        Objects.requireNonNull(date, "Reminder date is required");

        if (!dateRangePolicy.isWithinAllowedRange(date)) {
            throw new BusinessRuleViolationException("Reminder date is outside the allowed range");
        }
    }
}