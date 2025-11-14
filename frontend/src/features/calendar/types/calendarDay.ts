import type { Reminder } from "../../../types/reminder";
import type { PublicHoliday } from "../../../types/holiday";

export interface CalendarDay {
    date: string;
    isToday: boolean;
    isInCurrentMonth: boolean;
    isInAllowedRange: boolean;
    isHoliday: boolean;
    holiday?: PublicHoliday | null;
    hasReminders: boolean;
    reminders?: Reminder[];
}
