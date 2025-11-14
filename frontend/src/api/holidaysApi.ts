import { httpClient } from "./httpClient";
import type { PublicHoliday } from "../types/holiday";

export function getHolidays(year?: number): Promise<PublicHoliday[]> {
    const query = year != null ? `?year=${year}` : "";
    return httpClient.get<PublicHoliday[]>(`/holidays${query}`);
}
