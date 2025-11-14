import { useQuery } from "@tanstack/react-query";
import { getHolidays } from "../../../api/holidaysApi";
import type { PublicHoliday } from "../../../types/holiday";
import type { ApiError } from "../../../api/httpClient";

export function useHolidaysByYear(year: number | null | undefined) {
    const enabled = typeof year === "number";

    const query = useQuery<PublicHoliday[], ApiError>({
        queryKey: ["holidays", year ?? "none"],
        enabled,
        queryFn: () => {
            if (year == null) {
                return Promise.resolve([]);
            }
            return getHolidays(year);
        },
        staleTime: 24 * 60 * 60 * 1000, // 1 day
    });

    return {
        holidays: query.data ?? [],
        isLoading: query.isLoading,
        isFetching: query.isFetching,
        isError: query.isError,
        error: query.error,
        refetch: query.refetch,
    };
}