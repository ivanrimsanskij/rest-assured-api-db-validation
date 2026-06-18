# Rest Assured + API + DB Validation

API test automation framework that calls a public REST API, deserializes the JSON response into DTOs, persists a filtered subset to a MySQL database, and validates that the data was stored correctly at the database level.

The single test (`restSqlTest`):
1. Calls the **randomuser.me** API for 20 Finnish users (Rest Assured) and asserts `HTTP 200`.
2. Deserializes the response into `ResultDto` (Jackson).
3. Counts the existing rows in the `Persons` table (baseline).
4. Inserts only the **male** users into the `Persons` table (females are skipped).
5. Asserts the row delta equals the number of inserted males.
6. Asserts that no female user ended up in the table (the filter worked).
7. Truncates the `Persons` table after the suite.

## Tech stack

| Area | Tool |
|------|------|
| Language | Java 21 |
| Build | Maven |
| API testing | Rest Assured 6 |
| Test runner | TestNG |
| JSON mapping | Jackson 3 |
| Database | MySQL (Connector/J 9) |
| Reporting | Allure |
| Logging | SLF4J + Logback |
| Boilerplate | Lombok |

## Architecture

The code is split into layers:

- **`RestExecute`** — API client. Builds the request, calls the API, validates the response, returns DTOs.
- **`DbExecute`** — data access (DAO). All SQL lives here: insert, count, read back.
- **`TestClass`** — orchestration and assertions. Wires the layers together via TestNG lifecycle.
- **`ResultDto` / `PersonDto` / `NameDto`** — DTOs the JSON response is mapped to.

## Prerequisites

- JDK 21
- Maven 3.9+
- MySQL server running locally on `localhost:3306`

> Containerization is intentionally out of scope for this project, so you need a local MySQL
> instance. The connection URL, credentials, and API endpoint are all read from environment
> variables (see Configuration).

## Database setup

Create the schema and the table the test expects:

```sql
CREATE DATABASE IF NOT EXISTS db;
USE db;

CREATE TABLE IF NOT EXISTS Persons (
    FirstName VARCHAR(255),
    LastName  VARCHAR(255),
    Gender    VARCHAR(255),
    Title     VARCHAR(255),
    Nat       VARCHAR(255)
);
```

## Configuration (environment variables)

Nothing sensitive or environment-specific is stored in the repo — it is all read from
environment variables at runtime:

| Variable | Description | Example |
|----------|-------------|---------|
| `Db_Url` | JDBC connection URL | `jdbc:mysql://localhost:3306/db` |
| `Db_User` | MySQL username | `root` |
| `Db_Password` | MySQL password | `your_password` |
| `Api_Base_Url` | Base URI of the API under test | `https://randomuser.me/` |
| `Api_Base_Path` | Base path of the API | `/api` |

Set them before running (note the exact capitalization):

**macOS / Linux**

```bash
export Db_Url="jdbc:mysql://localhost:3306/db"
export Db_User="root"
export Db_Password="your_password"
export Api_Base_Url="https://randomuser.me/"
export Api_Base_Path="/api"
```

**Windows (PowerShell)**

```powershell
$env:Db_Url="jdbc:mysql://localhost:3306/db"
$env:Db_User="root"
$env:Db_Password="your_password"
$env:Api_Base_Url="https://randomuser.me/"
$env:Api_Base_Path="/api"
```

## Run the tests

```bash
mvn clean test
```

## Allure report

```bash
mvn allure:serve
```

Opens the generated HTML report in your browser.

## Project structure

```text
src/test
├── java/org/prog/api
│   ├── TestClass.java   -> Test + suite setup (orchestration & assertions)
│   ├── RestExecute.java -> API client (request, status check, returns DTO)
│   ├── DbExecute.java   -> DAO: insert / count / read back from DB
│   ├── ResultDto.java   -> Root DTO: list of persons
│   ├── PersonDto.java   -> Person DTO: name, gender, nationality
│   └── NameDto.java     -> Name DTO: first, last, title
└── resources
    └── testng.xml
```

## Notes

- The test relies on the live **randomuser.me** API; the response shape is mapped via the DTO
  classes, so a breaking change to the API contract would require updating them.
- Built as a learning/portfolio project to demonstrate API automation combined with
  database-level validation across clean, separated layers.
