export interface Reminder {
    id: number;
    text: string;
    date: string;
    time: string;
    createdAt: string;
    updatedAt: string;
}
export type ReminderPayload = Omit<Reminder, "id" | "createdAt" | "updatedAt">;