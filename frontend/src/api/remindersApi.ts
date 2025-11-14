import { httpClient } from "./httpClient";
import type { Reminder, ReminderPayload } from "../types/reminder";
import type { DateRange } from "../types/dateRange";

export function getReminders(date?: string): Promise<Reminder[]> {
    const query = date ? `?date=${encodeURIComponent(date)}` : "";
    return httpClient.get<Reminder[]>(`/reminders${query}`);
}

export function getAllowedDateRange(): Promise<DateRange> {
    return httpClient.get<DateRange>("/reminders/range");
}

export function createReminder(payload: ReminderPayload): Promise<Reminder> {
    return httpClient.post<Reminder>("/reminders", payload);
}

export function updateReminder(
    id: number,
    payload: ReminderPayload
): Promise<Reminder> {
    return httpClient.put<Reminder>(`/reminders/${id}`, payload);
}

export function deleteReminder(id: number): Promise<void> {
    return httpClient.delete<void>(`/reminders/${id}`);
}

export function deleteRemindersByDate(date: string): Promise<void> {
    const query = `?date=${encodeURIComponent(date)}`;
    return httpClient.delete<void>(`/reminders${query}`);
}
