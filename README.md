# GameZ

GameZ is a desktop game library and launcher built with JavaFX. Users can browse their library and add, update, delete and launch games.

**Team:** Nazarii Tsubera (lead, infrastructure), Jaxon Constanzo (repositories), Luke Arredondo (services), Zander Hartman (console UI)

**Design prototype:** [Figma (Simplified Design 2)](https://www.figma.com/design/gCiade6dsAYQaJixSaqB8J/Application-Project?node-id=60-30&t=ea5ZrB8rkPhgBOAH-1)

---

## Planned features

- **Library view.** Browse every game in your collection.
- **Add game.** Register a new game with its title, description, author, install location and cover image.
- **Update game.** Edit a game's details.
- **Delete game.** Remove a game from the library.
- **Launch game.** Start a game from its install location.

The Figma prototype shows the navigation between these screens, using "Game 2" as the selected game.

## Status

The project is at the scaffolding stage. The package structure, the SQLite connection and the Gradle setup are in place; `GameService` and `GameController` are still empty, and the migration only creates a placeholder `test` table.

---

## Roadmap: console prototype

**Goal:** all five features working end to end from the terminal (`./gradlew consoleApplication`), using the real Models → Repositories → Services → Controllers layers. The JavaFX GUI comes later and reuses the same services, so only the controller layer will change.

**Out of scope for now:** JavaFX screens and cover images (store `imagePath`, but don't display it).

### Who owns what

| Person | Layer | Packages |
|---|---|---|
| **Nazarii** | Project lead: scaffolding, infrastructure, conventions, unblocking others | `Infrastructure/`, `Models/`, `build.gradle.kts`, `module-info.java` |
| **Jaxon** | Persistence | `Repositories/` |
| **Luke** | Business logic | `Services/` |
| **Zander** | Console input/output | `Controllers/`, `Entrypoints/ConsoleApplication` |

Each person owns their layer and its tests. Nazarii reviews pull requests and helps whoever is blocked.

### Shared contracts

These signatures let everyone work in parallel. Nazarii commits them as stubs first (step 0), and then each owner fills in the body. If a signature needs to change, agree on it in the team chat before changing it, because the layer above you depends on it.

```java
// Models/Game.java (Nazarii)
public class Game {
    int id; String title; String description; String author; String location; String imagePath;
    // getters + setters for all fields, a no-arg constructor,
    // and Game(String title, String description, String author, String location, String imagePath)
}

// Repositories/GameRepository.java (Jaxon)
public class GameRepository {
    List<Game> findAll() throws SQLException;
    Optional<Game> findById(int id) throws SQLException;
    int insert(Game game) throws SQLException;       // returns the generated id
    boolean update(Game game) throws SQLException;   // false if no row had that id
    boolean delete(int id) throws SQLException;      // false if no row had that id
}

// Services/GameService.java (Luke)
public class GameService {
    GameService(GameRepository repository);
    List<Game> listGames();
    Game getGame(int id);                 // IllegalArgumentException if not found
    Game addGame(Game game);              // validates, returns the game with its new id
    void updateGame(Game game);           // validates; IllegalArgumentException if not found
    void deleteGame(int id);              // IllegalArgumentException if not found
    void launchGame(int id);              // IllegalStateException if the file is missing or can't be opened
}
```

**Error convention:** the repository throws `SQLException`. The service wraps it in a `RuntimeException` and throws `IllegalArgumentException` / `IllegalStateException` with a message a user can read. The controller catches these and prints `e.getMessage()`, so the app never crashes on bad input.

### Step 0: Unblock everyone (Nazarii, first)

| # | Task | Status |
|---|---|---|
| N.0 | Scaffold the project, set up the package structure and Gradle build | ✅ Done |
| N.1 | Add the SQLite `Database` connection and the migration runner | ✅ Done |
| N.2 | Write the architecture conventions and this plan | ✅ Done |
| N.3 | **Finalize the `Game` model.** Change `id` to `int`, add the `setId` and `imagePath` getter/setter and both constructors, and remove the unused `Date` import. | To do |
| N.4 | **Write the `games` migration**: `id INTEGER PRIMARY KEY AUTOINCREMENT`, `title TEXT NOT NULL`, `description TEXT`, `author TEXT`, `location TEXT NOT NULL`, `image_path TEXT`. Remove the placeholder `test` table. | To do |
| N.5 | **Commit stubs for the shared contracts** (method bodies `throw new UnsupportedOperationException()`) so everyone compiles against the same API. | To do |
| N.6 | **Enable terminal input**: add `standardInput = System.in` to the `consoleApplication` task, otherwise `Scanner` gets no input. | To do |
| N.7 | **Make the database URL configurable** (for example `Database.setUrl(String)`) so tests can use a temporary database instead of `data/gamez.db`. | To do |

N.3 to N.6 should land in one small PR as soon as possible, since everyone else depends on it.

### Jaxon: Repository layer

| # | Task | Depends on |
|---|---|---|
| J.1 | Remove the stray `PieChart` import from `GameRepository` | |
| J.2 | Add a private `mapRow(ResultSet)` helper that turns a row into a `Game` | N.3, N.4 |
| J.3 | Implement `findAll()` and `findById(int)` | J.2 |
| J.4 | Implement `insert(Game)` and return the generated id (`Statement.RETURN_GENERATED_KEYS`) | N.4 |
| J.5 | Implement `update(Game)` and `delete(int)`, returning `false` when no row was affected | N.4 |
| J.6 | Write JUnit tests: an insert → find → update → delete round-trip on a temporary database | J.3–J.5, N.7 |

**Rules:** use `PreparedStatement` for every query (never build SQL by joining strings), and put all SQL in this package only.

### Luke: Service layer

| # | Task | Depends on |
|---|---|---|
| L.1 | Add a constructor that takes a `GameRepository`, so tests can pass in a fake | N.5 |
| L.2 | Implement `listGames()` and `getGame(int)` | N.5 |
| L.3 | Implement `addGame` and `updateGame` with validation: title and location are required and not blank; trim the text fields | L.2 |
| L.4 | Implement `deleteGame(int)` with a not-found check | L.2 |
| L.5 | Implement `launchGame(int)`: check that `location` exists, then open it with `Desktop.getDesktop().open(file)` (add `requires java.desktop;` to `module-info.java`, coordinating with Nazarii) or `ProcessBuilder` | L.2 |
| L.6 | Write JUnit tests for validation and not-found cases, using a fake or in-memory `GameRepository` | L.3, L.4 |

You can build and test against a hand-written fake repository before Jaxon's real one is finished.

### Zander: Console layer

| # | Task | Depends on |
|---|---|---|
| Z.1 | Create `Controllers/ConsoleGameController`. It takes a `GameService` and a `Scanner` and has a `run()` method with the main menu loop: `1` list, `2` view details, `3` add, `4` update, `5` delete, `6` launch, `0` exit | N.5, N.6 |
| Z.2 | Write input helpers such as `readInt(prompt)`, `readRequired(prompt)` and `readOptional(prompt, currentValue)` that re-prompt on invalid input instead of crashing | Z.1 |
| Z.3 | **List and view:** print a table of id and title for all games, and all fields for the selected one | Z.1 |
| Z.4 | **Add and update:** prompt for each field; when updating, pressing Enter keeps the current value | Z.2 |
| Z.5 | **Delete and launch:** ask "Are you sure? (y/n)" before deleting, and show a success or error message after each action | Z.2 |
| Z.6 | Wire it up in `ConsoleApplication`: run `Database.migrate()`, build repository → service → controller, then call `run()` | Z.1 |

**Rules:** the controller only calls `GameService`, never the repository or SQL. Catch service exceptions and print `e.getMessage()`. Until Luke's service works, you can test with a fake service that returns hard-coded games.

### Integration and demo (everyone)

| # | Task | Owner |
|---|---|---|
| I.1 | Merge all layers, then run `./gradlew consoleApplication` on a fresh database and work through all five features | Nazarii (with whole team) |
| I.2 | Fix any integration bugs, with each owner fixing their own layer | Everyone |
| I.3 | Update the README Status section and record a short demo | Nazarii |

### Timeline

```
Step 0   Nazarii: N.3–N.6 (model, migration, contract stubs, stdin)   ← everyone waits on this
   │
   ├── Jaxon:  J.1–J.5 ─────────► J.6 (needs N.7)
   ├── Luke:   L.1–L.5 (fake repo) ─► L.6
   └── Zander: Z.1–Z.5 (fake service) ─► Z.6
   │
Integration: swap the fakes for the real classes → I.1–I.3
```

### Working rules

- One branch per task, for example `jaxon/repository-crud`. Open a pull request into `main`, and Nazarii reviews it.
- Don't edit another person's package without telling them. If you're blocked, ask Nazarii.
- Keep `main` building: `./gradlew build` must pass before you merge.

### Definition of done

- `./gradlew consoleApplication` on a fresh clone creates the database and opens the menu.
- All five features work, and invalid input never crashes the app.
- No SQL outside `Repositories/`, and no business logic in `Controllers/` or `Models/`.
- `./gradlew test` passes.

## Design notes

We first tried an AI-generated prototype, but it was too complex to implement, so we moved to a simpler layout that is easy to understand at a glance. We kept the color scheme from the AI prototype because it suited the app better than our original palette. The final prototype is the result of several rounds of design trial and error, aiming for a clean, minimal look.

---

## Getting started

### Requirements

- JDK 21 (Gradle will provision it through the toolchain if it is missing)
- No separate database server. SQLite is embedded.

### Run the app

```bash
# 1. Create/migrate the database (data/gamez.db)
./gradlew consoleApplication

# 2. Launch the JavaFX GUI
./gradlew run
```

On Windows, use `gradlew.bat` instead of `./gradlew`.

> Run both commands from the project root. The database path `data/gamez.db` is relative to the working directory.

### Other Gradle tasks

| Task | Description |
|---|---|
| `./gradlew run` | Launch the JavaFX GUI (`Entrypoints/GUIApplication`) |
| `./gradlew consoleApplication` | Run the console entry point, which applies database migrations (`Entrypoints/ConsoleApplication`) |
| `./gradlew test` | Run the JUnit 5 tests |
| `./gradlew jlink` | Build a self-contained runtime image under `build/` |

---

## Database

- **Engine:** SQLite, via `org.xerial:sqlite-jdbc`
- **File:** `data/gamez.db`
- **Connection:** `Infrastructure/Database.getConnection()` returns a shared connection.
- **Migrations:** `Database.migrate()` runs automatically when you start the console app. Migrations use `CREATE TABLE IF NOT EXISTS`, so running them more than once is safe.

To reset the database, delete `data/gamez.db` and run `./gradlew consoleApplication` again.

---

## Architecture

All source code lives in `src/main/java/org/example/gamez`. The project follows a clean architecture: each package has a single responsibility, and dependencies point inward toward the models.

```
Entrypoints  ──►  Controllers  ──►  Services  ──►  Repositories  ──►  Infrastructure (Database)
                                        │               │
                                        └──── Models ◄──┘
```

| Package | Responsibility | Rules |
|---|---|---|
| `Entrypoints/` | Application entry points (JavaFX GUI, console). | Wire things up and start the app. No business logic. |
| `Controllers/` | JavaFX (or console) controllers. Turn user input into service calls and update the view. | Talk to **Services** only, never to repositories or SQL. |
| `Services/` | Business logic layer. **One service per model** (e.g. `GameService`). | Validation, rules and orchestration live here. |
| `Repositories/` | Persistence layer. **One repository per model** (e.g. `GameRepository`). Maps Models ↔ SQL. | **All database access goes here.** |
| `Models/` | Domain models (e.g. `Game`). | Plain data only, with no business logic and no SQL. |
| `Infrastructure/` | Technical components that don't belong to a business domain (e.g. `Database`). | No domain knowledge. |

FXML views live in `src/main/resources/org/example/gamez/views/`.

### Adding a new domain entity

1. Create the data class in `Models/`.
2. Add its table to `Database.migrate()`.
3. Create `<Model>Repository` in `Repositories/` for its SQL.
4. Create `<Model>Service` in `Services/` for its business logic.
5. Create a controller in `Controllers/` (and an FXML view in `resources/.../views/`) to expose it in the UI.
6. If a package is accessed reflectively by JavaFX, export or open it in `src/main/java/module-info.java`.

---

## Project structure

```
GameZ/
├── data/
│   └── gamez.db                  # SQLite database
├── src/main/java/
│   ├── module-info.java
│   └── org/example/gamez/
│       ├── Controllers/
│       ├── Entrypoints/
│       ├── Infrastructure/
│       ├── Models/
│       ├── Repositories/
│       └── Services/
├── src/main/resources/org/example/gamez/views/   # FXML views
└── build.gradle.kts
```

## Tech stack

- Java 21 and JavaFX 21
- Gradle (Kotlin DSL) with the Java module system
- SQLite (`sqlite-jdbc`)
- JUnit 5
