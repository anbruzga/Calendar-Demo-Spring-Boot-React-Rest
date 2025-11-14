package lt.calendar.reminders.infrastructure.persistence.jpa;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

class ReminderEntityTest {

    private Clock clock;

    @BeforeEach
    void setUp() {
        clock = Clock.systemDefaultZone();
    }

    @Test
    @DisplayName("prePersist should set createdAt and updatedAt when createdAt is null")
    void prePersist_setsCreatedAndUpdatedWhenCreatedAtNull() {
        ReminderEntity entity = ReminderEntity.builder()
                .id(null)
                .reminderText("Test")
                .reminderDate(LocalDate.now(clock).plusDays(1))
                .reminderTime(LocalTime.of(10, 0))
                .createdAt(null)
                .updatedAt(null)
                .build();

        entity.prePersist();

        assertThat(entity.getCreatedAt()).isNotNull();
        assertThat(entity.getUpdatedAt()).isNotNull();

        assertThat(entity.getUpdatedAt()).isEqualTo(entity.getCreatedAt());
    }

    @Test
    @DisplayName("prePersist should keep existing createdAt and only update updatedAt")
    void prePersist_keepsExistingCreatedAt() {

        LocalDateTime existingCreatedAt = LocalDateTime.of(2025, 1, 1, 12, 0);
        ReminderEntity entity = ReminderEntity.builder()
                .id(1L)
                .reminderText("Existing")
                .reminderDate(LocalDate.now(clock).plusDays(1))
                .reminderTime(LocalTime.of(11, 0))
                .createdAt(existingCreatedAt)
                .updatedAt(null)
                .build();

        entity.prePersist();

        assertThat(entity.getCreatedAt()).isEqualTo(existingCreatedAt);
        assertThat(entity.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("preUpdate should update updatedAt")
    void preUpdate_updatesUpdatedAt() {
        LocalDateTime previousUpdatedAt = LocalDateTime.of(2025, 1, 1, 13, 0);
        ReminderEntity entity = ReminderEntity.builder()
                .id(1L)
                .reminderText("Update test")
                .reminderDate(LocalDate.now(clock).plusDays(2))
                .reminderTime(LocalTime.of(12, 0))
                .createdAt(LocalDateTime.of(2025, 1, 1, 12, 0))
                .updatedAt(previousUpdatedAt)
                .build();

        entity.preUpdate();

        assertThat(entity.getUpdatedAt()).isNotNull();
        assertThat(entity.getUpdatedAt()).isNotEqualTo(previousUpdatedAt);
    }
}