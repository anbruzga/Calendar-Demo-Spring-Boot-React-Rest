import { env } from "../config/env";

export interface ApiErrorResponse {
    timestamp: string;
    status: number;
    error: string;
    message: string;
    path: string;
    fieldErrors?: Record<string, string>;
}

export class ApiError extends Error {
    status: number;
    response: ApiErrorResponse | null;

    constructor(message: string, status: number, response: ApiErrorResponse | null) {
        super(message);
        this.status = status;
        this.response = response;
    }
}

const BASE_URL = env.BACKEND_BASE_URL.replace(/\/+$/, "");

async function request<T>(path: string, options: RequestInit = {}): Promise<T> {
    const url = `${BASE_URL}${path}`;

    const headers: HeadersInit = {
        Accept: "application/json",
        ...(options.body ? { "Content-Type": "application/json" } : {}),
        ...options.headers,
    };

    const response = await fetch(url, { ...options, headers });

    if (!response.ok) {
        let errorBody: ApiErrorResponse | null = null;

        try {
            const contentType = response.headers.get("Content-Type") || "";
            if (contentType.includes("application/json")) {
                errorBody = (await response.json()) as ApiErrorResponse;
            }
        } catch {
            // ignore JSON parse errors
        }

        const message = errorBody?.message || `Request failed with status ${response.status}`;
        throw new ApiError(message, response.status, errorBody);
    }

    if (response.status === 204) {
        return undefined as T;
    }

    const data = (await response.json()) as T;
    return data;
}

export const httpClient = {
    get: <T>(path: string) => request<T>(path, { method: "GET" }),
    post: <T>(path: string, body: unknown) =>
        request<T>(path, { method: "POST", body: JSON.stringify(body) }),
    put: <T>(path: string, body: unknown) =>
        request<T>(path, { method: "PUT", body: JSON.stringify(body) }),
    delete: <T>(path: string) => request<T>(path, { method: "DELETE" }),
};