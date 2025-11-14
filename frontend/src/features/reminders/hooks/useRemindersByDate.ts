import { useQuery } from "@tanstack/react-query";
import { getReminders } from "../../../api/remindersApi";
import type { Reminder } from "../../../types/reminder";
import type { ApiError } from "../../../api/httpClient";

export function useRemindersByDate(date: string | null | undefined) {
    const enabled = !!date;

    const query = useQuery<Reminder[], ApiError>({
        queryKey: ["reminders", "byDate", date ?? "none"],
        enabled,
        queryFn: () => {
            if (!date) {
                // Should never run because enabled=false
                return Promise.resolve([]);
            }
            return getReminders(date);
        },
    });

    return {
        reminders: query.data ?? [],
        isLoading: query.isLoading,
        isFetching: query.isFetching,
        isError: query.isError,
        error: query.error,
        refetch: query.refetch,
    };
}
