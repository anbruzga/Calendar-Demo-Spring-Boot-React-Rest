package lt.calendar.reminders.application.reminder;

import lt.calendar.reminders.application.exception.BusinessRuleViolationException;
import lt.calendar.reminders.application.exception.ReminderNotFoundException;
import lt.calendar.reminders.domain.reminder.Reminder;
import lt.calendar.reminders.domain.reminder.ReminderRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ReminderServiceTest {

    private ReminderRepositoryPort reminderRepositoryPort;
    private DateRangePolicy dateRangePolicy;
    private ReminderService reminderService;

    private Clock clock;

    @BeforeEach
    void setUp() {
        clock = Clock.systemDefaultZone();
        reminderRepositoryPort = mock(ReminderRepositoryPort.class);
        dateRangePolicy = mock(DateRangePolicy.class);
        reminderService = new ReminderService(reminderRepositoryPort, dateRangePolicy);
    }


    @Test
    @DisplayName("createReminder should save and return reminder for valid date")
    void createReminder_validDate_savesReminder() {
        LocalDate date = LocalDate.now(clock).plusDays(1);
        LocalTime time = LocalTime.of(10, 0);

        Reminder input = Reminder.builder()
                .text("Test reminder")
                .date(date)
                .time(time)
                .build();

        Reminder saved = Reminder.builder()
                .id(1L)
                .text("Test reminder")
                .date(date)
                .time(time)
                .build();

        when(reminderRepositoryPort.save(any(Reminder.class))).thenReturn(saved);
        when(dateRangePolicy.isWithinAllowedRange(date)).thenReturn(true);

        Reminder result = reminderService.createReminder(input);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getText()).isEqualTo("Test reminder");
        verify(reminderRepositoryPort, times(1)).save(any(Reminder.class));
        verify(dateRangePolicy).isWithinAllowedRange(date);
    }

    @Test
    @DisplayName("createReminder should throw BusinessRuleViolationException for past date")
    void createReminder_pastDate_throwsException() {
        LocalDate pastDate = LocalDate.now(clock).minusDays(1);
        LocalTime time = LocalTime.of(10, 0);

        Reminder input = Reminder.builder()
                .text("Past reminder")
                .date(pastDate)
                .time(time)
                .build();

        assertThatThrownBy(() -> reminderService.createReminder(input))
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessageContaining("Reminder date is outside the allowed range");

        verifyNoInteractions(reminderRepositoryPort);
    }
    @Test
    @DisplayName("createReminder should throw BusinessRuleViolationException for date beyond allowed range")
    void createReminder_dateBeyondRange_throwsException() {
        LocalDate tooFar = LocalDate.now(clock).plusYears(2);
        LocalTime time = LocalTime.of(10, 0);

        Reminder input = Reminder.builder()
                .text("Too far future")
                .date(tooFar)
                .time(time)
                .build();

        assertThatThrownBy(() -> reminderService.createReminder(input))
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessageContaining("outside the allowed range");

        verifyNoInteractions(reminderRepositoryPort);
    }

    @Test
    @DisplayName("updateReminder should update existing reminder")
    void updateReminder_existing_updatesFields() {
        LocalDate originalDate = LocalDate.now(clock).plusDays(2);
        LocalTime originalTime = LocalTime.of(9, 0);

        LocalDate newDate = LocalDate.now(clock).plusDays(3);
        LocalTime newTime = LocalTime.of(11, 0);

        Reminder existing = Reminder.builder()
                .id(42L)
                .text("Original")
                .date(originalDate)
                .time(originalTime)
                .build();

        Reminder updatedInput = Reminder.builder()
                .text("Updated text")
                .date(newDate)
                .time(newTime)
                .build();

        Reminder saved = Reminder.builder()
                .id(42L)
                .text("Updated text")
                .date(newDate)
                .time(newTime)
                .build();

        when(reminderRepositoryPort.findById(42L)).thenReturn(Optional.of(existing));
        when(dateRangePolicy.isWithinAllowedRange(newDate)).thenReturn(true);

        when(reminderRepositoryPort.save(any(Reminder.class))).thenReturn(saved);

        Reminder result = reminderService.updateReminder(42L, updatedInput);

        assertThat(result.getId()).isEqualTo(42L);
        assertThat(result.getText()).isEqualTo("Updated text");
        assertThat(result.getDate()).isEqualTo(newDate);
        assertThat(result.getTime()).isEqualTo(newTime);

        verify(reminderRepositoryPort).findById(42L);
        verify(reminderRepositoryPort).save(any(Reminder.class));
    }

    @Test
    @DisplayName("updateReminder should throw when reminder not found")
    void updateReminder_notFound_throwsException() {
        LocalDate validDate = LocalDate.now(clock).plusDays(1);
        LocalTime time = LocalTime.of(10, 0);

        when(reminderRepositoryPort.findById(999L)).thenReturn(Optional.empty());

        Reminder updatedInput = Reminder.builder()
                .text("Does not matter")
                .date(validDate)
                .time(time)
                .build();

        assertThatThrownBy(() -> reminderService.updateReminder(999L, updatedInput))
                .isInstanceOf(ReminderNotFoundException.class)
                .hasMessageContaining("Reminder not found with id");

        verify(reminderRepositoryPort).findById(999L);
        verify(reminderRepositoryPort, never()).save(any(Reminder.class));
    }

}