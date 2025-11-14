import "./App.css";
import AppLayout from "./features/layout/AppLayout";
import CalendarPage from "./features/calendar/components/CalendarPage";

function App() {
  return (
      <AppLayout>
        <CalendarPage />
      </AppLayout>
  );
}

export default App;