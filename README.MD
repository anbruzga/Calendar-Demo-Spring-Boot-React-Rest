Calendar Reminders

# Calendar Reminders

A full-stack demo application for managing calendar reminders with Lithuanian public holidays.

The **backend** is a Spring Boot service exposing a REST API for reminders and public holidays.  
The **frontend** is a React application that displays a calendar with Lithuanian public holidays and per-day reminders, with navigation restricted to an allowed date range.

- - -

## Tech Stack

### Backend

*   Java 21
*   Spring Boot
*   Spring Web / Spring MVC
*   Spring Data JPA / Hibernate
*   H2 in-memory database (dev/test)
*   RestTemplate client for external API (Nager public holidays)
*   JUnit, MockMvc

### Frontend

*   React
*   @tanstack/react-query
*   Formik + Yup

- - -

## Repository Structure

calendar-reminders-backend </br>
--> src </br>
--> pom.xml </br>
frontend  </br>
--> src </br>
--> package.json </br>
docker-compose.yml (for front end and back end)</br>


- - -

## Running the Project

There are three main ways to run the project.

### Option 1 – Docker Compose

From root dir containing `docker-compose.yml`:
`docker compose up ` or `docker compose up -d`</br>
**(Might require a couple of minutes for everything to boot up, please be patient, it looks like it doesn't work at first)**

This will:
*   Start the backend service using a Maven + Temurin 21 image, running `mvn spring-boot:run` in `calendar-reminders-backend/`.
*   Start the frontend service using a Node image, running `npm install` and `npm start` in `frontend/`.

Endpoints:
*   Backend: `http://localhost:8080`
*   Frontend: `http://localhost:3000`


### Option 2 – Run via IDE

#### Backend (IntelliJ IDEA Ultimate or similar)
1.  Open `calendar-reminders-backend/` as a Maven project
2.  Ensure project SDK is set to **Java 21**. Project was not tested on lower versions.
3.  Run the main class: `lt.calendar.reminders.CalendarRemindersBackendApplication`
4.  The backend starts on `http://localhost:8080` with the `dev` profile (in-memory H2, sample data)

#### Frontend (WebStorm / IntelliJ / VS Code...)
1.  Open `frontend` dir
2.  Install dependencies: `npm install`
3.  Run the dev server: `npm start`
4.  CRA dev server will start on `http://localhost:3000`

### Option 3 – Build & run from CLI
(No instructions provided)

- - -

## API Overview
### Reminders

*   `GET /reminders` – list all reminders
*   `GET /reminders?date=YYYY-MM-DD` – list reminders for a specific date
*   `GET /reminders/range` – get allowed reminder date range (minDate, maxDate)
*   `POST /reminders` – create reminder, example:

    {</br>
    "text": "Call insurance",</br>
    "date": "2025-02-01",</br>
    "time": "14:30"</br>
    }</br>

*   `PUT /reminders/{id}` – update reminder
*   `DELETE /reminders/{id}` – delete one reminder by id
*   `DELETE /reminders?date=YYYY-MM-DD` – delete all reminders for the given date

### Holidays
*   `GET /holidays?year=YYYY` – Lithuanian public holidays for given year
*   If `year` is omitted, current year (from backend `Clock`) is used
- - -

## Tests

### Running Backend Tests
Run via Terminal or IDEA - Coverage is very representable

### Frontend Tests
not implemented

- - -

## Known Issues / Limitations

This app is not focused on full production hardening. Known issues and limitations:

### 1\. DateRangePolicy & potential race conditions
*   `DefaultDateRangePolicy` exposes the allowed range based on `LocalDate.now(clock)` as `[today, today + 1 year]`
*   While it avoids calling `now()` multiple times inside a single `getCurrentRange()` invocation, it still relies on the underlying system clock
*   In edge cases (e.g. time changes, deployment spanning midnight), the frontend and backend might temporarily disagree about the allowed date range:
    *   frontend may still consider a date valid, while
    *   backend might already treat it as “past” or outside the range and reject it
*   For a real system, one might centralize the “business date” more strictly and force a UI refresh when the day changes
*  In practice, currently it is a big task with little benefit

### 2\. Docker Compose configuration is hardcoded and dev-only
*   `docker-compose.yml` is a simple convenience:
    *   uses specific images (`maven:3.9-eclipse-temurin-21`, `node:22`),
    *   hardcodes ports (`8080` for backend, `3000` for frontend)
    *   does not use environment-specific overrides or secrets
*   It is not intended as a production deployment manifest, only as the easiest way to spin up both services locally

### 3\. React overview fetch loads all reminders
*   `useRemindersOverview` currently calls `GET /reminders` and builds a set of dates that have at least one reminder.
*   This is simple and works well for small datasets, but doesn't scale

### 4\. Time zone assumptions
*   Backend uses `Clock.systemDefaultZone()`
*   Frontend uses the browser’s local timezone
*   If backend and frontend run in different time zones, the exact boundaries of “today” and date comparisons may differ slightly

### 5\. No authentication / multi-tenancy
*   The app intentionally has no authentication, reminders are not tied to users
*   For a prod per-user data and authorization rules would be needed

### 6\. H2 and in-memory data
*   Backend uses an in-memory H2 database for `dev` and tests, data is reset on backend restart
### 7\. Front-end CSS
*   Might not be 100% tested and perfect.


## Architecture overview

### Backend architecture
The backend follows a layered hexagonal architecture. The main ideas are:
*  1.  **Domain layer**  - contains the core business concepts for reminders and public holidays, along with abstract ports that describe what the application needs from persistence and external services. This layer has no dependencies on frameworks or infra
*  2.  **Application layer**  - Implements the use cases and business rules: creating, updating, deleting and listing reminders. Enforcing allowed date range for reminders, retrieving public holidays. Orchestrates domain objects, coordinates calls through the ports, and defines transactional boundaries. It does not know about HTTP, databases or JSON
*  3. **Infrastructure layer** -  Concrete implementations for ports defined in the domain. Persistence adapted to JPA/H2 for storing reminder. Has HTTP client adapter that calls the external public-holiday API. It also includes simple caching of holiday data. This layer translates between technical representations (database rows, HTTP responses) and domain objects
*  4. **Web / API layer**  -  Exposes the backend functionality as a REST API. It maps HTTP requests into application-layer calls and maps domain results into JSON responses. It also contains centralized error handling that converts validation errors, business rule violations and not-found situations into consistent HTTP responses
*  5.  **Configuration and technical utilities**  -  Contains configuration for the HTTP client, CORS, data source, and time source, plus small helpers and utilities

### Frontend architecture
The frontend uses a feature-based structure and is built around React with React Query for data fetching and Formik/Yup for forms
*   1. **API layer**  
    Communication with REST API. Provides functions for loading reminders,  allowed date range, public holidays, standardizes error handling so the rest of the code does not depend on low-level HTTP details
*   2. **Calendar feature**  
    Main calendar screen. The month view, weekday labels, day cells and navigation controls. Uses helper hooks and utilities to build monthly grid in allowed date range, and marks “today” and public holidays, days that have reminders
*   3. **Reminders feature**  
    Contains everything related to reminders for a selected date. List of reminders, form dialog for creating and editing, hooks for loading and mutating reminder data. Form validation is handled with Formik/Yup, and React Query is used to keep the list and calendar indicators in sync after create/update/delete operations
*   4. **Holidays feature**  
    Provides a small hook responsible for loading public holidays for the current calendar year and exposing them in a form that the calendar feature can use to mark holiday dates
*   5. **Layout, shared UI and state**  
    Includes header, main content, reusable UI components (confirmation dialog, error alert), a central React Query client, and utility modules for date and calendar calculations. Gives a consistent look and behavior across features
