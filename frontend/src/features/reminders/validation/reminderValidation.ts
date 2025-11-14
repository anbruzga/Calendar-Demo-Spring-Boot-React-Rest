import * as Yup from "yup";

export const reminderValidationSchema = Yup.object({
    text: Yup.string()
        .required("Reminder text is required")
        .max(255, "Reminder text must be at most 255 characters"),
    time: Yup.string()
        .required("Time is required")
        .matches(/^\d{2}:\d{2}$/, "Time must be in HH:mm format"),
});

export type ReminderFormValues = Yup.InferType<typeof reminderValidationSchema>;