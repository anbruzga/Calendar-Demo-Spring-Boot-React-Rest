import React, { useMemo, useState } from "react";
import { useAllowedDateRange } from "../../reminders/hooks/useAllowedDateRange";
import { useCalendarNavigation } from "../hooks/useCalendarNavigation";
import { useHolidaysByYear } from "../../holidays/hooks/useHolidaysByYear";
import { useRemindersByDate } from "../../reminders/hooks/useRemindersByDate";
import { useReminderMutations } from "../../reminders/hooks/useReminderMutations";
import {
    buildMonthGridMatrix,
    getMonthYearFromDateString,
} from "../../../lib/calendarUtils";
import { getTodayDateString, isWithinRange } from "../../../lib/dateUtils";
import type { CalendarDay } from "../types/calendarDay";
import type { PublicHoliday } from "../../../types/holiday";
import type { Reminder } from "../../../types/reminder";

import CalendarHeader from "./CalendarHeader";
import CalendarGrid from "./CalendarGrid";
import ReminderList from "../../reminders/components/ReminderList";
import ReminderFormDialog from "../../reminders/components/ReminderFormDialog";
import ConfirmDialog from "../../../ui/components/ConfirmDialog";
import LoadingSpinner from "../../../ui/components/LoadingSpinner";
import ErrorAlert from "../../../ui/components/ErrorAlert";
import { useRemindersOverview } from "../../reminders/hooks/useRemindersOverview";

const CalendarPage: React.FC = () => {
    const {
        range,
        isLoading: isRangeLoading,
        isError: isRangeError,
        error: rangeError,
    } = useAllowedDateRange();

    const { deleteRemindersByDateMutation } = useReminderMutations();

    const [formOpen, setFormOpen] = useState(false);
    const [editingReminder, setEditingReminder] = useState<Reminder | null>(null);
    const [deleteConfirmOpen, setDeleteConfirmOpen] = useState(false);

    const {
        currentMonth,
        selectedDate,
        canGoPrevMonth,
        canGoNextMonth,
        goToPrevMonth,
        goToNextMonth,
        goToToday,
        selectDate,
    } = useCalendarNavigation(range?.minDate, range?.maxDate);

    const { holidays } = useHolidaysByYear(currentMonth.year);
    const { reminders } = useRemindersByDate(selectedDate);
    const { datesWithReminders } = useRemindersOverview();

    const holidayByDate = useMemo(() => {
        const map = new Map<string, PublicHoliday>();
        holidays.forEach((h) => {
            map.set(h.date, h);
        });
        return map;
    }, [holidays]);

    const selectedHoliday = holidayByDate.get(selectedDate) ?? null;

    const today = getTodayDateString();

    const weeks: CalendarDay[][] = useMemo(() => {
        // While range is not loaded yet, just return an empty grid
        // the loading UI below will be shown anyway
        if (!range) {
            return [];
        }

        const grid = buildMonthGridMatrix(currentMonth, true);

        return grid.map((week) =>
            week.map((date) => {
                const monthOfCell = getMonthYearFromDateString(date);
                const isInCurrentMonth =
                    monthOfCell.year === currentMonth.year &&
                    monthOfCell.month === currentMonth.month;

                const isInAllowedRange = isWithinRange(
                    date,
                    range.minDate,
                    range.maxDate
                );

                const holiday = holidayByDate.get(date) ?? null;
                const isHoliday = !!holiday;

                const hasReminders = datesWithReminders.has(date);

                const day: CalendarDay = {
                    date,
                    isToday: date === today,
                    isInCurrentMonth,
                    isInAllowedRange,
                    isHoliday,
                    holiday,
                    hasReminders,
                    // keep full reminders only for the selected day
                    reminders: date === selectedDate ? reminders : undefined,
                };

                return day;
            })
        );
    }, [currentMonth, range, holidayByDate, datesWithReminders, selectedDate, reminders, today]);

    if (isRangeLoading || !range) {
        return (
            <div>
                <LoadingSpinner />
                <div>Loading allowed date range...</div>
            </div>
        );
    }

    if (isRangeError) {
        return (
            <div>
                <ErrorAlert
                    message={rangeError?.message ?? "Failed to load allowed date range."}
                />
            </div>
        );
    }

    const openCreateForm = () => {
        setEditingReminder(null);
        setFormOpen(true);
    };

    const openEditForm = (reminder: Reminder) => {
        setEditingReminder(reminder);
        setFormOpen(true);
    };

    const handleDayClick = (date: string) => {
        if (date === selectedDate) {
            if (reminders.length > 0) {
                setDeleteConfirmOpen(true);
            } else {
                openCreateForm();
            }
        } else {
            selectDate(date);
            openCreateForm();
        }
    };

    const handleDeleteAllConfirmed = () => {
        setDeleteConfirmOpen(false);
        deleteRemindersByDateMutation.mutate(selectedDate);
    };

    const handleDeleteAllCancelled = () => {
        setDeleteConfirmOpen(false);
    };

    return (
        <div className="calendar-page">
            <CalendarHeader
                currentMonth={currentMonth}
                canGoPrevMonth={canGoPrevMonth}
                canGoNextMonth={canGoNextMonth}
                onPrevMonth={goToPrevMonth}
                onNextMonth={goToNextMonth}
                onToday={goToToday}
            />

            <div className="calendar-page__content">
                <div className="calendar-page__calendar">
                    <CalendarGrid
                        weeks={weeks}
                        selectedDate={selectedDate}
                        onDayClick={handleDayClick}
                    />
                </div>

                <div className="calendar-page__sidebar">
                    {selectedHoliday && (
                        <div className="calendar-page__selected-holiday">
                            Public Holiday:&nbsp;
                            <strong>{selectedHoliday.localName}</strong>
                        </div>
                    )}

                    <ReminderList
                        date={selectedDate}
                        reminders={reminders}
                        onCreate={openCreateForm}
                        onEdit={openEditForm}
                        onDeleteAll={() => setDeleteConfirmOpen(true)}
                    />

                    <ReminderFormDialog
                        open={formOpen}
                        selectedDate={selectedDate}
                        initialReminder={editingReminder}
                        onClose={() => setFormOpen(false)}
                    />
                </div>
            </div>

            <ConfirmDialog
                open={deleteConfirmOpen}
                title="Delete all reminders"
                message={`Delete all reminders for ${selectedDate}?`}
                confirmLabel="Delete"
                cancelLabel="Cancel"
                onConfirm={handleDeleteAllConfirmed}
                onCancel={handleDeleteAllCancelled}
            />
        </div>
    );
};

export default CalendarPage;
