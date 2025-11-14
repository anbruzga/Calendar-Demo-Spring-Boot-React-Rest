import React, { useEffect, useState } from "react";
import { Formik, Form, Field, ErrorMessage, FormikHelpers } from "formik";
import { reminderValidationSchema, ReminderFormValues } from "../validation/reminderValidation";
import { useReminderMutations } from "../hooks/useReminderMutations";
import type { Reminder } from "../../../types/reminder";
import { ApiError } from "../../../api/httpClient";
import ErrorAlert from "../../../ui/components/ErrorAlert";

type ReminderFormDialogProps = {
    open: boolean;
    selectedDate: string;
    initialReminder: Reminder | null;
    onClose: () => void;
};

const ReminderFormDialog: React.FC<ReminderFormDialogProps> = ({
                                                                   open,
                                                                   selectedDate,
                                                                   initialReminder,
                                                                   onClose,
                                                               }) => {
    const { createReminderMutation, updateReminderMutation } = useReminderMutations();
    const [submitError, setSubmitError] = useState<string | null>(null);

    const isEditMode = !!initialReminder;

    const initialValues: ReminderFormValues = {
        text: initialReminder?.text ?? "",
        time: initialReminder?.time ?? "",
    };

    useEffect(() => {
        setSubmitError(null);
    }, [open, initialReminder, selectedDate]);

    const handleSubmit = async (
        values: ReminderFormValues,
        helpers: FormikHelpers<ReminderFormValues>
    ) => {
        setSubmitError(null);

        const payload = {
            text: values.text,
            time: values.time,
            date: selectedDate,
        };

        try {
            if (isEditMode && initialReminder) {
                await updateReminderMutation.mutateAsync({
                    id: initialReminder.id,
                    payload,
                });
            } else {
                await createReminderMutation.mutateAsync(payload);
            }

            helpers.resetForm();
            onClose();
        } catch (err) {
            if (err instanceof ApiError && err.response) {
                const fieldErrors = err.response.fieldErrors ?? {};
                Object.entries(fieldErrors).forEach(([field, message]) => {
                    helpers.setFieldError(field, message);
                });
                if (err.response.message && Object.keys(fieldErrors).length === 0) {
                    setSubmitError(err.response.message);
                }
            } else if (err instanceof Error) {
                setSubmitError(err.message);
            } else {
                setSubmitError("Unexpected error");
            }
        } finally {
            helpers.setSubmitting(false);
        }
    };

    const isSubmitting =
        createReminderMutation.isPending || updateReminderMutation.isPending;

    if (!open) return null;

    return (
        <div className="reminder-form">
            <div className="reminder-form__header">
                <h2 className="reminder-form__title">
                    {isEditMode ? "Edit reminder" : "Add reminder"}
                </h2>
                <button
                    type="button"
                    className="reminder-form__close"
                    onClick={onClose}
                >
                    ✕
                </button>
            </div>

            <div className="reminder-form__date">
                Date: <strong>{selectedDate}</strong>
            </div>

            <Formik
                initialValues={initialValues}
                validationSchema={reminderValidationSchema}
                enableReinitialize
                onSubmit={handleSubmit}
            >
                {({ isSubmitting: formSubmitting }) => (
                    <Form className="reminder-form__body">
                        <div className="reminder-form__field">
                            <label htmlFor="text">Reminder</label>
                            <Field id="text" name="text" as="textarea" rows={2} />
                            <div className="reminder-form__error">
                                <ErrorMessage name="text" />
                            </div>
                        </div>

                        <div className="reminder-form__field">
                            <label htmlFor="time">Time</label>
                            <Field id="time" name="time" type="time" />
                            <div className="reminder-form__error">
                                <ErrorMessage name="time" />
                            </div>
                        </div>

                        <ErrorAlert message={submitError} />

                        <div className="reminder-form__actions">
                            <button
                                type="button"
                                className="reminder-form__button reminder-form__button--secondary"
                                onClick={onClose}
                                disabled={isSubmitting || formSubmitting}
                            >
                                Cancel
                            </button>
                            <button
                                type="submit"
                                className="reminder-form__button reminder-form__button--primary"
                                disabled={isSubmitting || formSubmitting}
                            >
                                {isEditMode ? "Save" : "Create"}
                            </button>
                        </div>
                    </Form>
                )}
            </Formik>
        </div>
    );
};

export default ReminderFormDialog;