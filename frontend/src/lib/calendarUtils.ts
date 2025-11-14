import { toDateString } from "./dateUtils";

export interface MonthYear {
    year: number;
    month: number; // 1-12
}

export function getMonthYearFromDateString(dateStr: string): MonthYear {
    const [y, m] = dateStr.split("-").map(Number);
    return { year: y, month: m };
}

export function getPrevMonth({ year, month }: MonthYear): MonthYear {
    if (month === 1) {
        return { year: year - 1, month: 12 };
    }
    return { year, month: month - 1 };
}

export function getNextMonth({ year, month }: MonthYear): MonthYear {
    if (month === 12) {
        return { year: year + 1, month: 1 };
    }
    return { year, month: month + 1 };
}

function normalizeWeekday(date: Date, weekStartsOnMonday: boolean): number {
    const jsDay = date.getDay(); // 0 = Sunday, 1 = Monday, ...
    if (!weekStartsOnMonday) {
        return jsDay;
    }
    // Convert to 0=Mon,...6=Sun
    return (jsDay + 6) % 7;
}

function daysInMonth(year: number, month: number): number {
    return new Date(year, month, 0).getDate(); // month is 1-12, JS uses day=0 => last day prev month
}

/**
 * Returns a flat array [42] of dates representing 6x7 grid incl days from adjacent months
 */
export function buildMonthGridFlat(
    { year, month }: MonthYear,
    weekStartsOnMonday = true
): string[] {
    const firstOfMonth = new Date(year, month - 1, 1);
    const firstWeekday = normalizeWeekday(firstOfMonth, weekStartsOnMonday);

    // Start date is firstOfMonth minus "firstWeekday" days
    const startDate = new Date(year, month - 1, 1 - firstWeekday);

    const result: string[] = [];
    for (let i = 0; i < 42; i++) {
        const d = new Date(startDate);
        d.setDate(startDate.getDate() + i);
        result.push(toDateString(d));
    }
    return result;
}

/**
 * Same as buildMonthGridFlat but grouped into 6 rows of 7 days.
 */
export function buildMonthGridMatrix(
    monthYear: MonthYear,
    weekStartsOnMonday = true
): string[][] {
    const flat = buildMonthGridFlat(monthYear, weekStartsOnMonday);
    const weeks: string[][] = [];
    for (let i = 0; i < flat.length; i += 7) {
        weeks.push(flat.slice(i, i + 7));
    }
    return weeks;
}

/**
 * Returns first and last day strings for a given month.
 */
export function getMonthBounds({ year, month }: MonthYear): { firstDay: string; lastDay: string } {
    const first = toDateString(new Date(year, month - 1, 1));
    const last = toDateString(new Date(year, month - 1, daysInMonth(year, month)));
    return { firstDay: first, lastDay: last };
}