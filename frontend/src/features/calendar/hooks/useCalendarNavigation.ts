import { useEffect, useState } from "react";
import {
    getTodayDateString,
    isBefore,
    isAfter,
} from "../../../lib/dateUtils";
import {
    getMonthYearFromDateString,
    getPrevMonth,
    getNextMonth,
    getMonthBounds,
    type MonthYear,
} from "../../../lib/calendarUtils";

export interface CalendarNavigationState {
    currentMonth: MonthYear;
    selectedDate: string;
    canGoPrevMonth: boolean;
    canGoNextMonth: boolean;
    goToPrevMonth: () => void;
    goToNextMonth: () => void;
    goToToday: () => void;
    selectDate: (date: string) => void;
}

/**
 * Handles current month, selected date and prev/next navigation,
 * clamped to an allowed date range
 */
export function useCalendarNavigation(
    minDate?: string,
    maxDate?: string
): CalendarNavigationState {
    const today = getTodayDateString();

    const [selectedDate, setSelectedDate] = useState<string>(today);
    const [currentMonth, setCurrentMonth] = useState<MonthYear>(
        () => getMonthYearFromDateString(today)
    );

    // When allowed range arrives/changes, clamp selected date into the range
    useEffect(() => {
        if (!minDate || !maxDate) {
            return;
        }

        let next = selectedDate;

        if (isBefore(next, minDate)) {
            next = minDate;
        } else if (isAfter(next, maxDate)) {
            next = maxDate;
        }

        if (next !== selectedDate) {
            setSelectedDate(next);
            setCurrentMonth(getMonthYearFromDateString(next));
        }
    }, [minDate, maxDate, selectedDate]);

    const prevMonth = getPrevMonth(currentMonth);
    const nextMonth = getNextMonth(currentMonth);

    const prevBounds = getMonthBounds(prevMonth);
    const nextBounds = getMonthBounds(nextMonth);

    const canGoPrevMonth =
        !minDate || prevBounds.lastDay >= minDate;

    const canGoNextMonth =
        !maxDate || nextBounds.firstDay <= maxDate;

    const goToPrevMonth = () => {
        if (!canGoPrevMonth) return;
        setCurrentMonth((m) => getPrevMonth(m));
    };

    const goToNextMonth = () => {
        if (!canGoNextMonth) return;
        setCurrentMonth((m) => getNextMonth(m));
    };

    const goToToday = () => {
        let target = today;

        if (minDate && isBefore(target, minDate)) {
            target = minDate;
        }
        if (maxDate && isAfter(target, maxDate)) {
            target = maxDate;
        }

        setSelectedDate(target);
        setCurrentMonth(getMonthYearFromDateString(target));
    };

    const selectDate = (date: string) => {
        if (minDate && isBefore(date, minDate)) return;
        if (maxDate && isAfter(date, maxDate)) return;

        setSelectedDate(date);
        setCurrentMonth(getMonthYearFromDateString(date));
    };

    return {
        currentMonth,
        selectedDate,
        canGoPrevMonth,
        canGoNextMonth,
        goToPrevMonth,
        goToNextMonth,
        goToToday,
        selectDate,
    };
}