package lt.calendar.reminders.web.reminder;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import lt.calendar.reminders.application.reminder.DateRangePolicy;
import lt.calendar.reminders.application.reminder.ReminderDateRange;
import lt.calendar.reminders.application.reminder.ReminderService;
import lt.calendar.reminders.domain.reminder.Reminder;
import lt.calendar.reminders.web.reminder.dto.AllowedDateRangeResponse;
import lt.calendar.reminders.web.reminder.dto.ReminderRequest;
import lt.calendar.reminders.web.reminder.dto.ReminderResponse;
import lt.calendar.reminders.web.reminder.mapper.ReminderApiMapper;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/reminders")
public class ReminderController {

    private final ReminderService reminderService;
    private final ReminderApiMapper reminderApiMapper;
    private final DateRangePolicy dateRangePolicy;

    public ReminderController(ReminderService reminderService,
                              ReminderApiMapper reminderApiMapper, DateRangePolicy dateRangePolicy) {
        this.reminderService = reminderService;
        this.reminderApiMapper = reminderApiMapper;
        this.dateRangePolicy = dateRangePolicy;
    }

    /**
     * GET /reminders
     * Optional: ?date=YYYY-MM-DD to filter by date.
     */
    @GetMapping
    public List<ReminderResponse> getReminders(
            @RequestParam(value = "date", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {


        log.info("Fetching reminders with date filter: {}", date);
        List<Reminder> reminders = (date == null)
                ? reminderService.getAllReminders()
                : reminderService.getRemindersForDate(date);

        return reminders.stream()
                .map(reminderApiMapper::toResponse)
                .toList();
    }

    /**
     * GET /reminders/range
     * Returns the currently allowed reminder date range.
     */
    @GetMapping("/range")
    public AllowedDateRangeResponse getAllowedDateRange() {
        ReminderDateRange range = dateRangePolicy.getCurrentRange();
        LocalDate minDate = range.minDate();
        LocalDate maxDate = range.maxDate();

        log.info("Returning allowed reminder date range: {} to {}", minDate, maxDate);
        return new AllowedDateRangeResponse(minDate, maxDate);
    }

    /**
     * POST /reminders
     * Creates a new reminder.
     */
    @PostMapping
    public ResponseEntity<ReminderResponse> createReminder(@Valid @RequestBody ReminderRequest request) {

        log.info("Creating reminder for date {} and time {}", request.getDate(), request.getTime());

        Reminder toCreate = reminderApiMapper.toDomain(request);
        Reminder created = reminderService.createReminder(toCreate);

        log.debug("Created reminder with id {}", created.getId());

        ReminderResponse response = reminderApiMapper.toResponse(created);

        URI location = URI.create("/reminders/" + created.getId());
        return ResponseEntity.created(location).body(response);
    }

    /**
     * PUT /reminders/{id}
     * Updates an existing reminder.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ReminderResponse> updateReminder(
            @PathVariable Long id,
            @Valid @RequestBody ReminderRequest request) {

        log.info("Updating reminder with id {}", id);

        Reminder updatedDomain = reminderApiMapper.toDomain(request);
        Reminder updated = reminderService.updateReminder(id, updatedDomain);

        log.debug("Updated reminder with id {}", updated.getId());

        ReminderResponse response = reminderApiMapper.toResponse(updated);
        return ResponseEntity.ok(response);
    }

    /**
     * DELETE /reminders/{id}
     * Deletes one reminder by id.
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteReminder(@PathVariable Long id) {
        log.info("Deleting reminder with id {}", id);
        reminderService.deleteReminder(id);
    }
    /**
     * DELETE /reminders?date=YYYY-MM-DD
     * Deletes all reminders for a given date.
     * Useful for "click the same day again to remove all reminders" UX.
     */
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRemindersByDate(
            @RequestParam("date")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        log.info("Deleting reminders for date {}", date);
        reminderService.deleteRemindersByDate(date);
    }

}