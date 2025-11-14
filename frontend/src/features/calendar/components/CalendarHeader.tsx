import React from "react";
import type { MonthYear } from "../../../lib/calendarUtils";

type CalendarHeaderProps = {
    currentMonth: MonthYear;
    canGoPrevMonth: boolean;
    canGoNextMonth: boolean;
    onPrevMonth: () => void;
    onNextMonth: () => void;
    onToday: () => void;
};

const MONTH_NAMES = [
    "January",
    "February",
    "March",
    "April",
    "May",
    "June",
    "July",
    "August",
    "September",
    "October",
    "November",
    "December",
];

const CalendarHeader: React.FC<CalendarHeaderProps> = ({
                                                           currentMonth,
                                                           canGoPrevMonth,
                                                           canGoNextMonth,
                                                           onPrevMonth,
                                                           onNextMonth,
                                                           onToday,
                                                       }) => {
    const monthLabel = `${MONTH_NAMES[currentMonth.month - 1]} ${currentMonth.year}`;

    return (
        <div className="calendar-header">
            <div className="calendar-header__left">
                <button
                    type="button"
                    className="calendar-header__button"
                    onClick={onPrevMonth}
                    disabled={!canGoPrevMonth}>
                    Prev
                </button>
                <button
                    type="button"
                    className="calendar-header__button"
                    onClick={onNextMonth}
                    disabled={!canGoNextMonth}>
                    Next
                </button>

            </div>

            <div className="calendar-header__center">
                <span className="calendar-header__month">{monthLabel}</span>
            </div>

            <div className="calendar-header__right">
                <button
                    type="button"
                    className="calendar-header__button calendar-header__button--today"
                    onClick={onToday}>
                    Today
                </button>
            </div>

            {/* Right side is just a spacer to keep layout stable */}
            <div className="calendar-header__right" />
        </div>
    );
};

export default CalendarHeader;
