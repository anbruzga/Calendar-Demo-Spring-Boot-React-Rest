import React from "react";
import type { CalendarDay } from "../types/calendarDay";

type CalendarDayCellProps = {
    day: CalendarDay;
    isSelected: boolean;
    onClick: (date: string) => void;
};

const CalendarDayCell: React.FC<CalendarDayCellProps> = ({
                                                             day,
                                                             isSelected,
                                                             onClick,
                                                         }) => {
    const dateObj = new Date(day.date);
    const dayNumber = dateObj.getDate();

    const handleClick = () => {
        if (!day.isInAllowedRange) return;
        onClick(day.date);
    };

    const classNames = [
        "calendar-day-cell",
        !day.isInCurrentMonth && "calendar-day-cell--outside-month",
        day.isToday && "calendar-day-cell--today",
        isSelected && "calendar-day-cell--selected",
        !day.isInAllowedRange && "calendar-day-cell--disabled",
        day.isHoliday && "calendar-day-cell--holiday",
        day.hasReminders && "calendar-day-cell--has-reminders",
    ]
        .filter(Boolean)
        .join(" ");

    return (
        <button
            type="button"
            className={classNames}
            onClick={handleClick}
            disabled={!day.isInAllowedRange}
        >
            <div className="calendar-day-cell__header">
                <span className="calendar-day-cell__number">{dayNumber}</span>
                {day.isHoliday && (
                    <span
                        className="calendar-day-cell__badge"
                        title={day.holiday?.localName}>
                    </span>
                )}
            </div>

            {day.isHoliday && day.holiday && (
                <div className="calendar-day-cell__holiday-name">
                    {day.holiday.localName}
                </div>
            )}

            {day.hasReminders && (
                <span
                    className="calendar-day-cell__dot"
                    aria-label="Has reminders"
                />
            )}
        </button>
    );
};

export default CalendarDayCell;
