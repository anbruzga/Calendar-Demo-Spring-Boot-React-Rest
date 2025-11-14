import { useQuery } from "@tanstack/react-query";
import { getReminders } from "../../../api/remindersApi";
import type { Reminder } from "../../../types/reminder";
import type { ApiError } from "../../../api/httpClient";

/**
 * Fetches all reminders once and exposes a set of dates (YYYY-MM-DD)
 * that have at least one reminder.
 */

function toIsoDate(value: string | Date): string {
    if (value instanceof Date) {
        return value.toISOString().slice(0, 10);
    }
    // handle both YYYY-MM-DD and YYYY-MM-DDTHH:mm:ss
    return value.slice(0, 10);
}

export function useRemindersOverview() {
    const query = useQuery<Reminder[], ApiError>({
        queryKey: ["reminders", "overview"],
        queryFn: () => getReminders(), // no date param -> all reminders -> low performance on scale todo
        staleTime: 30_000,
    });

    const datesWithReminders = new Set<string>();

    (query.data ?? []).forEach((reminder: any) => {
        const raw = reminder.date as string | Date | undefined;
        if (!raw) return;
        const iso = toIsoDate(raw);
        datesWithReminders.add(iso);
    });

    return {
        datesWithReminders,
        isLoading: query.isLoading,
        isError: query.isError,
        error: query.error,
    };
}