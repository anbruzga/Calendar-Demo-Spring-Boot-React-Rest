import { useQuery } from "@tanstack/react-query";
import { getAllowedDateRange } from "../../../api/remindersApi";
import type { DateRange } from "../../../types/dateRange";
import type { ApiError } from "../../../api/httpClient";

export function useAllowedDateRange() {
    const query = useQuery<DateRange, ApiError>({
        queryKey: ["reminders", "range"],
        queryFn: () => getAllowedDateRange(),
        staleTime: 5 * 60 * 1000, // 5 minutes
    });

    return {
        range: query.data ?? null,
        isLoading: query.isLoading,
        isError: query.isError,
        error: query.error,
    };
}