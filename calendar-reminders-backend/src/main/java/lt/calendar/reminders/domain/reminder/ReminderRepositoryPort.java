package lt.calendar.reminders.domain.reminder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReminderRepositoryPort {

    Reminder save(Reminder reminder);

    Optional<Reminder> findById(Long id);

    List<Reminder> findAll();

    List<Reminder> findByDate(LocalDate date);

    void deleteById(Long id);

    void deleteByDate(LocalDate date);
}