package lt.calendar.reminders.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SpringDataReminderRepository extends JpaRepository<ReminderEntity, Long> {

    List<ReminderEntity> findByReminderDateOrderByReminderTimeAsc(LocalDate reminderDate);

    void deleteByReminderDate(LocalDate reminderDate);
}