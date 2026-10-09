# PULSE / Athlete HUD

An online fitness tracking application built with Java 17, Jakarta Servlet 6, JSP/JSTL, JDBC, and MySQL 8. It is packaged as a WAR for Tomcat 10.1+. Spring and Hibernate are not used.

## Solution Design

### Problem analysis

Members need one place to record training, set time-bound goals, review progress, compare challenge participation, and share useful fitness content. Administrators need protected controls for accounts, content review, system settings, platform statistics, and recent activity. The application separates these use cases by `ADMIN` and `USER` roles, enforces authorization at the servlet filter boundary, and keeps persistence behind JDBC DAOs.

### ER diagram

```text
users 1 ─── * workouts
users 1 ─── * goals
users * ─── * challenges       (through challenge_participants)
users 1 ─── * fitness_content  (reviewed_by is an optional users reference)
users 1 ─── * activity_log     (nullable user for system events)
system_settings                (key/value configuration)

users(id PK, email UNIQUE)
workouts(id PK, user_id FK)
goals(id PK, user_id FK)
challenges(id PK)
challenge_participants(id PK, challenge_id FK, user_id FK, UNIQUE(challenge_id,user_id))
fitness_content(id PK, user_id FK, reviewed_by FK NULL)
system_settings(setting_key PK)
activity_log(id PK, user_id FK NULL)
```

### Architecture

```text
Browser (HTML/CSS/vanilla JS, Chart.js)
  -> Filters (UTF-8, authenticated session, ADMIN authorization)
  -> HttpServlet controllers (MVC, request attributes, CSRF, PRG/flash)
  -> Services (authentication, calorie calculation, recommendations, async activity)
  -> DAO interfaces / DAOFactory / JDBC implementations
  -> DBConnectionManager singleton -> MySQL 8
  -> JSP + JSTL/EL views (shared fragments, no scriptlets)
```

### Use cases

- Guest: register, sign in, sign out.
- Member: CRUD workouts and goals; inspect weekly/monthly summaries, training mix, activity rings, heatmap, and recommendations; join/leave challenges and view leaders; submit tips/plans; update profile and password.
- Administrator: create/edit/delete users and roles; approve/reject content; edit key-value settings; inspect KPI charts and a five-second-polled activity feed.

## Requirements

- JDK 17
- Maven 3.9+
- MySQL 8
- Apache Tomcat 10.1 (Servlet 6 / JSP 3.1)
- Network access for Google Fonts, Lucide, and Chart.js CDN assets (the app remains usable without charts/icons if offline)

## Setup

1. Create the database and tables, then load demo data:

   ```powershell
   mysql -u root -p < schema.sql
   mysql -u root -p fitness_tracker < data.sql
   ```

   The canonical SQL files are also in `src/main/resources/`.

2. Set the MySQL connection in `src/main/resources/db.properties` (`db.url`, `db.username`, `db.password`). The checked-in password is intentionally `change-me`; replace it for your local database.

3. Build the deployable WAR from the project root:

   ```powershell
   mvn clean package
   ```

4. Copy `target/fitness-tracker.war` to Tomcat's `webapps` directory and start Tomcat. Visit `http://localhost:8080/fitness-tracker/`.

5. Demo access:

   | Role | Email | Password |
   |---|---|---|
   | Admin | `admin@fit.com` | `Admin@123` |
   | User | `jordan@fit.com` | `MoveMore@123` |
   | User | `sam@fit.com` | `MoveMore@123` |

   Demo passwords are stored as salted SHA-256 strings. New passwords use a random 128-bit salt. For production deployment, put credentials in a protected external configuration and use a dedicated least-privilege database account.

## Screenshots

Capture these views after deploying with seeded data. Suggested filenames for a submission evidence folder:

- `01-login-register.png` — split-screen sign-in and registration.
- `02-user-dashboard.png` — activity rings, workout heatmap, progress charts, and recommendations.
- `03-workouts-goals.png` — workout log, intensity selector, and goal progress.
- `04-challenges-leaderboard.png` — challenge cards and top-three leaderboard.
- `05-admin-overview.png` — KPIs, engagement chart, and live activity feed.
- `06-admin-management.png` — member table, content review, and settings.

## Grading Map

| Criterion | Implementation files |
|---|---|
| Problem understanding and solution design | This README; `src/main/java/com/fitness/model/`; `src/main/java/com/fitness/servlet/DashboardServlet.java`; `src/main/webapp/WEB-INF/views/user/` and `admin/` |
| Core Java concepts | `model/Person.java`, `User.java`, `Admin.java`, `Workout.java`; `enums/`; `dao/GenericDAO.java`; `model/Result.java`; `service/Recommendable.java`; `util/Loggable.java`; `service/RecommendationService.java`; `listener/AppContextListener.java`; `dao/DAOFactory.java`; `util/ValidationUtil.java`, `PasswordUtil.java`, `DateUtil.java` |
| Database integration with JDBC | `src/main/resources/schema.sql`, `data.sql`, `db.properties`; `util/DBConnectionManager.java`; `dao/` and `dao/impl/` |
| Servlets and web integration | `servlet/`; `filter/`; `listener/`; `src/main/webapp/WEB-INF/web.xml`; `src/main/webapp/WEB-INF/views/`; `src/main/webapp/js/` |

## Design Notes

- MVC: servlets own request handling, services own application rules, DAOs own SQL, and JSPs render EL/JSTL data.
- Database writes use `PreparedStatement`; challenge enrollment and its activity record commit or roll back as one transaction.
- JDBC resources use try-with-resources. `DataAccessException` preserves the root SQL cause; credentials and invalid input are handled at the service/controller boundaries.
- `Workout.Builder` validates required fields; estimated calories use workout MET, intensity multiplier, member weight, and duration: `MET × weightKg × durationMinutes / 60`.
- Activity writes run on an `ExecutorService`; scheduled retention and expired-challenge cleanup run on a `ScheduledExecutorService`; both stop with the servlet context.
- CSRF tokens are session-scoped and included in all state-changing forms. Mutations use POST and redirect after completion.
- Chart.js and Lucide are CDN dependencies. Core forms and tables are server-rendered and continue to function if those assets are unavailable.
