import React from "react";
import type { Reminder } from "../../../types/reminder";
import { useReminderMutations } from "../hooks/useReminderMutations";

type ReminderListProps = {
    date: string;
    reminders: Reminder[];
    onCreate: () => void;
    onEdit: (reminder: Reminder) => void;
    onDeleteAll: () => void;
};

const ReminderList: React.FC<ReminderListProps> = ({
                                                       date,
                                                       reminders,
                                                       onCreate,
                                                       onEdit,
                                                       onDeleteAll,
                                                   }) => {
    const { deleteReminderMutation } = useReminderMutations();

    const handleDeleteOne = (reminder: Reminder) => {
        if (deleteReminderMutation.isPending) return;
        deleteReminderMutation.mutate(reminder.id);
    };

    return (
        <div className="reminder-list">
            <div className="reminder-list__header">
                <div className="reminder-list__title">
                    Reminders For <strong>{date}</strong>
                </div>
                <div className="reminder-list__header-actions">
                    {reminders.length > 0 && (
                        <button
                            type="button"
                            className="reminder-list__button reminder-list__button--danger"
                            onClick={onDeleteAll}
                        >
                            Delete All
                        </button>
                    )}
                    <button
                        type="button"
                        className="reminder-list__button reminder-list__button--primary"
                        onClick={onCreate}
                    >
                        Add Reminder
                    </button>
                </div>
            </div>

            {reminders.length === 0 ? (
                <div className="reminder-list__empty">No reminders for this date yet.</div>
            ) : (
                <ul className="reminder-list__items">
                    {reminders.map((rem) => (
                        <li key={rem.id} className="reminder-list__item">
                            <div className="reminder-list__item-main">
                                <span className="reminder-list__time">{rem.time}</span>
                                <span className="reminder-list__text">{rem.text}</span>
                            </div>
                            <div className="reminder-list__item-actions">
                                <button
                                    type="button"
                                    className="reminder-list__icon-button"
                                    onClick={() => onEdit(rem)}
                                >
                                    Edit
                                </button>
                                <button
                                    type="button"
                                    className="reminder-list__icon-button"
                                    onClick={() => handleDeleteOne(rem)}
                                    disabled={deleteReminderMutation.isPending}
                                >
                                    Bin
                                </button>
                            </div>
                        </li>
                    ))}
                </ul>
            )}
        </div>
    );
};

export default ReminderList;
