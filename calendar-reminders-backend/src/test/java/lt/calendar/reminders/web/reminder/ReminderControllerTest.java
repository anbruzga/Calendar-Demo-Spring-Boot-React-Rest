package lt.calendar.reminders.web.reminder;

import com.fasterxml.jackson.databind.ObjectMapper;
import lt.calendar.reminders.application.reminder.DateRangePolicy;
import lt.calendar.reminders.application.reminder.ReminderDateRange;
import lt.calendar.reminders.web.reminder.dto.ReminderRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ReminderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DateRangePolicy dateRangePolicy;

    private Clock clock;

    @BeforeEach
    void setUp() {
        clock = Clock.systemDefaultZone();
    }

    private ReminderRequest buildValidRequest(String text, LocalDate date, LocalTime time) {
        ReminderRequest request = new ReminderRequest();
        request.setText(text);
        request.setDate(date);
        request.setTime(time);
        return request;
    }

    private Long createReminderViaApi(String text, LocalDate date, LocalTime time) throws Exception {
        ReminderRequest request = buildValidRequest(text, date, time);

        String json = objectMapper.writeValueAsString(request);

        String responseBody = mockMvc.perform(post("/reminders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(responseBody).get("id").asLong();
    }

    @Test
    @DisplayName("POST /reminders - should create a new reminder")
    void createReminder_shouldReturn201AndReminder() throws Exception {
        LocalDate todayOrFuture = LocalDate.now(clock).plusDays(1);
        LocalTime time = LocalTime.of(10, 0);

        ReminderRequest request = buildValidRequest("Interview practice", todayOrFuture, time);

        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/reminders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.text").value("Interview practice"))
                .andExpect(jsonPath("$.date").value(todayOrFuture.toString()))
                .andExpect(jsonPath("$.time").value(time.toString()));
    }

    @Test
    @DisplayName("GET /reminders - should return list of reminders")
    void getReminders_shouldReturnList() throws Exception {

        LocalDate date = LocalDate.now(clock).plusDays(2);
        LocalTime time = LocalTime.of(9, 30);
        String uniqueText = "GetReminders-" + System.currentTimeMillis();

        createReminderViaApi(uniqueText, date, time);

        mockMvc.perform(get("/reminders")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("GET /reminders?date=YYYY-MM-DD - should return reminders for specific date")
    void getRemindersByDate_shouldReturnFilteredList() throws Exception {
        LocalDate date = LocalDate.now(clock).plusDays(3);
        LocalTime time = LocalTime.of(15, 0);
        String uniqueText = "FilterByDate-" + System.currentTimeMillis();

        createReminderViaApi(uniqueText, date, time);

        mockMvc.perform(get("/reminders")
                        .param("date", date.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[?(@.text == '%s')]", uniqueText).exists());
    }

    @Test
    @DisplayName("PUT /reminders/{id} - should update existing reminder")
    void updateReminder_shouldUpdateExistingReminder() throws Exception {
        LocalDate date = LocalDate.now(clock).plusDays(4);
        LocalTime time = LocalTime.of(11, 0);
        String originalText = "Original-" + System.currentTimeMillis();

        Long id = createReminderViaApi(originalText, date, time);

        ReminderRequest updateRequest = buildValidRequest("Updated text", date, LocalTime.of(12, 0));

        String updateJson = objectMapper.writeValueAsString(updateRequest);

        mockMvc.perform(put("/reminders/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.text").value("Updated text"))
                .andExpect(jsonPath("$.time").value("12:00"));
    }

    @Test
    @DisplayName("DELETE /reminders/{id} - should delete reminder")
    void deleteReminder_shouldRemoveReminder() throws Exception {
        LocalDate date = LocalDate.now(clock).plusDays(5);
        LocalTime time = LocalTime.of(14, 0);
        String uniqueText = "DeleteMe-" + System.currentTimeMillis();

        Long id = createReminderViaApi(uniqueText, date, time);

        mockMvc.perform(delete("/reminders/{id}", id))
                .andExpect(status().isNoContent());

        String responseBody = mockMvc.perform(get("/reminders")
                        .param("date", date.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertThat(responseBody).doesNotContain(uniqueText);
    }

    @Test
    @DisplayName("POST /reminders - should return 400 when text is blank")
    void createReminder_shouldReturn400WhenTextBlank() throws Exception {
        LocalDate date = LocalDate.now(clock).plusDays(1);
        LocalTime time = LocalTime.of(10, 0);

        ReminderRequest request = buildValidRequest("   ", date, time);
        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/reminders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /reminders - should return 400 when date is missing")
    void createReminder_shouldReturn400WhenDateMissing() throws Exception {
        LocalTime time = LocalTime.of(10, 0);

        ReminderRequest request = new ReminderRequest();
        request.setText("Missing date");
        request.setDate(null);
        request.setTime(time);

        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/reminders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /reminders - should return 400 when time is missing")
    void createReminder_shouldReturn400WhenTimeMissing() throws Exception {
        LocalDate date = LocalDate.now(clock).plusDays(1);

        ReminderRequest request = new ReminderRequest();
        request.setText("Missing time");
        request.setDate(date);
        request.setTime(null);

        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/reminders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /reminders - should return 400 + ApiErrorResponse when date is in the past")
    void createReminder_shouldReturn400AndErrorBodyWhenDateInPast() throws Exception {
        LocalDate pastDate = LocalDate.now(clock).minusDays(1);
        LocalTime time = LocalTime.of(10, 0);

        ReminderRequest request = buildValidRequest("Past reminder", pastDate, time);

        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/reminders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Reminder date is outside the allowed range"))
                .andExpect(jsonPath("$.path").value("/reminders"));
    }

    @Test
    @DisplayName("POST /reminders - should return 400 when date is outside allowed range (beyond 1 year)")
    void createReminder_shouldReturn400WhenDateBeyondAllowedRange() throws Exception {
        LocalDate tooFarInFuture = LocalDate.now(clock).plusYears(2);
        LocalTime time = LocalTime.of(10, 0);

        ReminderRequest request = buildValidRequest("Too far in future", tooFarInFuture, time);

        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/reminders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /reminders/range - should return allowed date range from DateRangePolicy")
    void getAllowedDateRange_shouldReturnMinAndMaxDates() throws Exception {

        ReminderDateRange range = dateRangePolicy.getCurrentRange();
        LocalDate minDate = range.minDate();
        LocalDate maxDate = range.maxDate();

        mockMvc.perform(get("/reminders/range")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.minDate").value(minDate.toString()))
                .andExpect(jsonPath("$.maxDate").value(maxDate.toString()));
    }

    @Test
    @DisplayName("DELETE /reminders?date=YYYY-MM-DD - should delete all reminders for that date")
    void deleteRemindersByDate_shouldDeleteAllForGivenDate() throws Exception {
        LocalDate min = dateRangePolicy.getMinDate();
        LocalDate targetDate = min.plusDays(2);

        LocalTime time1 = LocalTime.of(9, 0);
        LocalTime time2 = LocalTime.of(11, 0);

        String text1 = "DeleteAll-1-" + System.currentTimeMillis();
        String text2 = "DeleteAll-2-" + System.currentTimeMillis();
        String otherText = "KeepMe-" + System.currentTimeMillis();

        createReminderViaApi(text1, targetDate, time1);
        createReminderViaApi(text2, targetDate, time2);

        LocalDate otherDate = targetDate.plusDays(1);
        createReminderViaApi(otherText, otherDate, LocalTime.of(13, 0));

        mockMvc.perform(delete("/reminders")
                        .param("date", targetDate.toString()))
                .andExpect(status().isNoContent());

        // Target date should now have no reminders
        mockMvc.perform(get("/reminders")
                        .param("date", targetDate.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        // Other date reminder should still exist
        String responseBody = mockMvc.perform(get("/reminders")
                        .param("date", otherDate.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(responseBody).contains(otherText);
    }

    @Test
    @DisplayName("PUT /reminders/{id} - should return 400 + ApiErrorResponse when date is in the past")
    void updateReminder_shouldReturn400WhenDateInPast() throws Exception {
        LocalDate min = dateRangePolicy.getMinDate();
        LocalDate validDate = min.plusDays(1);
        LocalTime time = LocalTime.of(10, 0);

        Long id = createReminderViaApi("Past-update-base-" + System.currentTimeMillis(), validDate, time);

        LocalDate pastDate = min.minusDays(1);
        ReminderRequest updateRequest = buildValidRequest("Updated text", pastDate, LocalTime.of(12, 0));

        String json = objectMapper.writeValueAsString(updateRequest);

        mockMvc.perform(put("/reminders/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Reminder date is outside the allowed range"))
                .andExpect(jsonPath("$.path").value("/reminders/" + id));
    }

    @Test
    @DisplayName("PUT /reminders/{id} - should return 400 + ApiErrorResponse when date is beyond allowed range")
    void updateReminder_shouldReturn400WhenDateBeyondAllowedRange() throws Exception {
        ReminderDateRange range = dateRangePolicy.getCurrentRange();
        LocalDate minDate = range.minDate();
        LocalDate maxDate = range.maxDate();
        LocalTime time = LocalTime.of(10, 0);

        Long id = createReminderViaApi("Future-update-base-" + System.currentTimeMillis(), minDate.plusDays(1), time);

        LocalDate tooFar = maxDate.plusDays(1);
        ReminderRequest updateRequest = buildValidRequest("Updated text", tooFar, LocalTime.of(12, 0));

        String json = objectMapper.writeValueAsString(updateRequest);

        mockMvc.perform(put("/reminders/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Reminder date is outside the allowed range"))
                .andExpect(jsonPath("$.path").value("/reminders/" + id));
    }

    @Test
    @DisplayName("PUT /reminders/{id} - should return 404 + ApiErrorResponse when reminder not found")
    void updateReminder_shouldReturn404WhenIdNotFound() throws Exception {
        LocalDate min = dateRangePolicy.getMinDate();
        LocalDate validDate = min.plusDays(1);
        LocalTime time = LocalTime.of(10, 0);

        ReminderRequest updateRequest = buildValidRequest("Does not matter", validDate, time);
        String json = objectMapper.writeValueAsString(updateRequest);

        long nonExistingId = 999_999L;

        mockMvc.perform(put("/reminders/{id}", nonExistingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Reminder not found with id: " + nonExistingId))
                .andExpect(jsonPath("$.path").value("/reminders/" + nonExistingId));
    }

}