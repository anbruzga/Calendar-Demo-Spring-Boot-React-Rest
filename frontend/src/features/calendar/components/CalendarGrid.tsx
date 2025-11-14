import React from "react";
import type { CalendarDay } from "../types/calendarDay";
import CalendarDayCell from "./CalendarDayCell";

type CalendarGridProps = {
    weeks: CalendarDay[][];
    selectedDate: string;
    onDayClick: (date: string) => void;
};

const WEEKDAY_LABELS = ["Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"];

const CalendarGrid: React.FC<CalendarGridProps> = ({
                                                       weeks,
                                                       selectedDate,
                                                       onDayClick,
                                                   }) => {
    return (
        <div className="calendar-grid">
            <div className="calendar-grid__weekdays">
                {WEEKDAY_LABELS.map((label) => (
                    <div key={label} className="calendar-grid__weekday">
                        {label}
                    </div>
                ))}
            </div>

            <div className="calendar-grid__weeks">
                {weeks.map((week, rowIndex) => (
                    <div key={rowIndex} className="calendar-grid__week">
                        {week.map((day) => (
                            <CalendarDayCell
                                key={day.date}
                                day={day}
                                isSelected={day.date === selectedDate}
                                onClick={onDayClick}
                            />
                        ))}
                    </div>
                ))}
            </div>
        </div>
    );
};

export default CalendarGrid;
