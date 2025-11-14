import { useMutation, useQueryClient } from "@tanstack/react-query";
import {
    createReminder,
    updateReminder,
    deleteReminder,
    deleteRemindersByDate,
} from "../../../api/remindersApi";
import type { Reminder, ReminderPayload } from "../../../types/reminder";
import type { ApiError } from "../../../api/httpClient";

export function useReminderMutations() {
    const queryClient = useQueryClient();


    const invalidateReminders = () => {
        queryClient.invalidateQueries({ queryKey: ["reminders", "byDate"] });
        queryClient.invalidateQueries({ queryKey: ["reminders", "overview"] });
    };

    const createReminderMutation = useMutation<Reminder, ApiError, ReminderPayload>({
        mutationFn: (payload) => createReminder(payload),
        onSuccess: () => {
            invalidateReminders();
        },
    });

    const updateReminderMutation = useMutation<
        Reminder,
        ApiError,
        { id: number; payload: ReminderPayload }
    >({
        mutationFn: ({ id, payload }) => updateReminder(id, payload),
        onSuccess: () => {
            invalidateReminders();
        },
    });

    const deleteReminderMutation = useMutation<void, ApiError, number>({
        mutationFn: (id) => deleteReminder(id),
        onSuccess: () => {
            invalidateReminders();
        },
    });

    const deleteRemindersByDateMutation = useMutation<void, ApiError, string>({
        mutationFn: (date) => deleteRemindersByDate(date),
        onSuccess: () => {
            invalidateReminders();
        },
    });

    return {
        createReminderMutation,
        updateReminderMutation,
        deleteReminderMutation,
        deleteRemindersByDateMutation,
    };
}
