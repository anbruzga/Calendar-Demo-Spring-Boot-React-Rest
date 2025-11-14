import { render, screen } from "@testing-library/react";
import App from "./App";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";

test("renders calendar header", () => {
  const queryClient = new QueryClient();

  render(
      <QueryClientProvider client={queryClient}>
        <App />
      </QueryClientProvider>
  );

  const titleElement = screen.getByText(/Calendar Reminders/i);
  expect(titleElement).toBeInTheDocument();
});
