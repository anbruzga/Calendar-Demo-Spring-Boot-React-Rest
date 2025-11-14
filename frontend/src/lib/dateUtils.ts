// Basic helpers for working with 'YYYY-MM-DD' strings
export function toDateString(date: Date): string {
  const y = date.getFullYear();
  const m = String(date.getMonth() + 1).padStart(2, "0");
  const d = String(date.getDate()).padStart(2, "0");
  return `${y}-${m}-${d}`;
}

export function getTodayDateString(): string {
  return toDateString(new Date());
}

export function toDateParts(dateStr: string): { year: number; month: number; day: number } {
  const [y, m, d] = dateStr.split("-").map(Number);
  return { year: y, month: m, day: d };
}

export function toJsDate(dateStr: string): Date {
  const { year, month, day } = toDateParts(dateStr);
  return new Date(year, month - 1, day);
}

export function isBefore(a: string, b: string): boolean {
  return a < b;
}

export function isAfter(a: string, b: string): boolean {
  return a > b;
}

export function isWithinRange(date: string, minDate: string, maxDate: string): boolean {
  return date >= minDate && date <= maxDate;
}