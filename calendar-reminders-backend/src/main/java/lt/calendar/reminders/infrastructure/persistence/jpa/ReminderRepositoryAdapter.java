package lt.calendar.reminders.infrastructure.persistence.jpa;

import lt.calendar.reminders.domain.reminder.Reminder;
import lt.calendar.reminders.domain.reminder.ReminderRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ReminderRepositoryAdapter implements ReminderRepositoryPort {

    private final SpringDataReminderRepository springDataReminderRepository;

    @Override
    public Reminder save(Reminder reminder) {
        ReminderEntity entity = toEntity(reminder);
        ReminderEntity saved = springDataReminderRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Reminder> findById(Long id) {
        return springDataReminderRepository.findById(id)
                .map(this::toDomain);
    }

    @Override
    public List<Reminder> findAll() {
        return springDataReminderRepository.findAll()
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<Reminder> findByDate(LocalDate date) {
        return springDataReminderRepository.findByReminderDateOrderByReminderTimeAsc(date)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public void deleteByDate(LocalDate date) {
        springDataReminderRepository.deleteByReminderDate(date);
    }

    @Override
    public void deleteById(Long id) {
        springDataReminderRepository.deleteById(id);
    }

    private ReminderEntity toEntity(Reminder reminder) {
        return ReminderEntity.builder()
                .id(reminder.getId())
                .reminderText(reminder.getText())
                .reminderDate(reminder.getDate())
                .reminderTime(reminder.getTime())
                .createdAt(reminder.getCreatedAt())
                .updatedAt(reminder.getUpdatedAt())
                .build();
    }

    private Reminder toDomain(ReminderEntity entity) {
        return Reminder.builder()
                .id(entity.getId())
                .text(entity.getReminderText())
                .date(entity.getReminderDate())
                .time(entity.getReminderTime())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}