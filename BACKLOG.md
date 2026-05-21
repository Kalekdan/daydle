# Daydle Implementation Backlog

## How to use this backlog
- Status values: `todo`, `in-progress`, `blocked`, `done`
- Priority values: `P0` (must-have), `P1` (should-have), `P2` (nice-to-have)
- Estimate values are rough team estimates in story points
- Definition of done for all items: code merged, tests passing, and docs updated

## Milestone 1: Foundation and Auth

### EPIC-1: Project Setup

#### BE-001 - Initialize Spring Boot service
- Priority: P0
- Estimate: 3
- Status: todo
- Description: Create a Spring Boot app with Web, Security, Validation, Data JPA, PostgreSQL driver, Flyway/Liquibase.
- Acceptance criteria:
1. Service starts locally with one command.
2. Health endpoint returns HTTP 200.
3. DB migration tool runs on startup.
4. Basic CI command for backend tests exists.

#### FE-001 - Initialize React frontend
- Priority: P0
- Estimate: 3
- Status: todo
- Description: Create React + TypeScript app with router and API client scaffolding.
- Acceptance criteria:
1. Frontend starts locally with one command.
2. Routing supports public and authenticated pages.
3. Shared API client supports auth token injection.
4. Basic CI command for frontend tests/lint exists.

#### OPS-001 - Local development orchestration
- Priority: P0
- Estimate: 2
- Status: todo
- Description: Add Docker Compose (or equivalent) for PostgreSQL and app env defaults.
- Acceptance criteria:
1. One documented flow to run DB + backend + frontend.
2. Environment variables documented in `.env.example` files.
3. README includes local run instructions.

### EPIC-2: Authentication and User Accounts

#### BE-010 - User entity and auth schema
- Priority: P0
- Estimate: 3
- Status: todo
- Description: Add `users` table and related constraints.
- Acceptance criteria:
1. `users` has unique username and email.
2. Passwords are hashed with BCrypt or Argon2.
3. Migration is reversible or has rollback notes.

#### BE-011 - Register endpoint
- Priority: P0
- Estimate: 3
- Status: todo
- Description: Implement `POST /api/auth/register`.
- Acceptance criteria:
1. Valid payload creates a user.
2. Duplicate username/email returns validation error.
3. Input validation covers email format and password policy.

#### BE-012 - Login and token issuance
- Priority: P0
- Estimate: 5
- Status: todo
- Description: Implement `POST /api/auth/login` with JWT access token.
- Acceptance criteria:
1. Valid credentials return token and user summary.
2. Invalid credentials return HTTP 401.
3. Expired/invalid tokens are rejected consistently.

#### BE-013 - Current user endpoint
- Priority: P0
- Estimate: 2
- Status: todo
- Description: Implement `GET /api/auth/me`.
- Acceptance criteria:
1. Authenticated call returns current user profile.
2. Unauthenticated call returns HTTP 401.

#### FE-010 - Auth UI and route guards
- Priority: P0
- Estimate: 5
- Status: todo
- Description: Build login/register screens and protected route handling.
- Acceptance criteria:
1. User can register and login from UI.
2. Auth state persists across refresh.
3. Protected routes redirect unauthenticated users.

## Milestone 2: Games and Paste Parsing

### EPIC-3: Core Game Data Model

#### BE-020 - Game catalog schema
- Priority: P0
- Estimate: 2
- Status: todo
- Description: Add `games` table with stable keys (wordle, maptap, connections, parseword).
- Acceptance criteria:
1. Seed script inserts initial supported games.
2. Game key is unique and immutable.

#### BE-021 - Game results schema
- Priority: P0
- Estimate: 5
- Status: todo
- Description: Add `game_results` with normalized fields and raw text.
- Acceptance criteria:
1. Stores `user_id`, `game_id`, `played_on`, `raw_text`, `parsed_json`.
2. Unique constraint on (`user_id`, `game_id`, `played_on`).
3. Indexes exist for user/game/date queries.

### EPIC-4: Detector/Parser Module System

#### BE-030 - Module interface and registry
- Priority: P0
- Estimate: 5
- Status: todo
- Description: Build plugin contract: detector + parser for each game.
- Acceptance criteria:
1. Common interface for all modules is enforced in code.
2. Registry can list enabled modules.
3. Pipeline executes all detectors and ranks by confidence.

#### BE-031 - Ambiguity handling logic
- Priority: P0
- Estimate: 3
- Status: todo
- Description: Define threshold/margin rules for auto-detect vs user disambiguation.
- Acceptance criteria:
1. API returns top candidates sorted by confidence.
2. Ambiguous input returns structured disambiguation response.
3. Clear error is returned when no detector matches.

#### BE-032 - Wordle module
- Priority: P0
- Estimate: 5
- Status: todo
- Description: Implement detector and parser for Wordle result text.
- Acceptance criteria:
1. Detects common Wordle share formats.
2. Extracts attempts/max attempts and date or puzzle number where available.
3. Unit tests cover valid, invalid, and edge cases.

#### BE-033 - Maptap module
- Priority: P0
- Estimate: 5
- Status: todo
- Description: Implement detector and parser for Maptap result text.
- Acceptance criteria:
1. Detects Maptap share text variants.
2. Extracts distance/accuracy metrics where present.
3. Unit tests cover valid, invalid, and edge cases.

#### BE-034 - Connections module
- Priority: P0
- Estimate: 5
- Status: todo
- Description: Implement detector and parser for Connections result text.
- Acceptance criteria:
1. Detects Connections share formats.
2. Extracts solved/failed and mistakes if present.
3. Unit tests cover valid, invalid, and edge cases.

#### BE-035 - Parseword module
- Priority: P1
- Estimate: 5
- Status: todo
- Description: Implement detector and parser for Parseword result text.
- Acceptance criteria:
1. Detects Parseword share formats.
2. Extracts core score/performance fields.
3. Unit tests cover valid, invalid, and edge cases.

### EPIC-5: Result Submission APIs

#### BE-040 - Parse preview endpoint
- Priority: P0
- Estimate: 5
- Status: todo
- Description: Implement `POST /api/results/parse-preview` to preview detection and parse output.
- Acceptance criteria:
1. Returns candidate modules with confidence.
2. Returns normalized preview for selected or top candidate.
3. Returns parse validation errors in consistent schema.

#### BE-041 - Save result endpoint
- Priority: P0
- Estimate: 5
- Status: todo
- Description: Implement `POST /api/results` to persist parsed result.
- Acceptance criteria:
1. Saves selected module output and raw text.
2. Duplicate day result for same user/game is rejected or upserted per product rule.
3. Endpoint requires authentication.

#### FE-020 - Paste and preview UX
- Priority: P0
- Estimate: 5
- Status: todo
- Description: Build main paste screen with detection preview and confirmation.
- Acceptance criteria:
1. User can paste text and see detected game candidates.
2. User can choose game when detection is ambiguous.
3. User can confirm and save parsed result.
4. Error states are clear and actionable.

## Milestone 3: Contributions Graphs and Stats

### EPIC-6: Contribution Aggregations

#### BE-050 - Contribution query service
- Priority: P0
- Estimate: 5
- Status: todo
- Description: Build aggregation logic for Git-style daily grids per game/user/year.
- Acceptance criteria:
1. Returns every day in requested year with intensity value.
2. Supports `activity` and `performance` intensity modes.
3. Handles leap years and timezone boundaries correctly.

#### BE-051 - Stats endpoints
- Priority: P0
- Estimate: 3
- Status: todo
- Description: Implement stats APIs.
- Acceptance criteria:
1. `GET /api/stats/me/contributions` works per game/year.
2. `GET /api/stats/{userId}/contributions` obeys privacy rules.
3. `GET /api/stats/me/overview` returns streak and totals.

#### FE-030 - Contribution graph components
- Priority: P0
- Estimate: 5
- Status: todo
- Description: Implement reusable contribution grid component and per-game dashboards.
- Acceptance criteria:
1. Graph renders a full year in Git-style week/day layout.
2. Tooltip/day details are accessible by mouse and keyboard.
3. User can switch games and year.

#### FE-031 - Dashboard summary cards
- Priority: P1
- Estimate: 3
- Status: todo
- Description: Add streak, total plays, and win-rate summary UI.
- Acceptance criteria:
1. Summary reflects selected game and date range.
2. Loading and empty states are handled.

## Milestone 4: Social Following and Feeds

### EPIC-7: Following Graph

#### BE-060 - Follow schema and constraints
- Priority: P0
- Estimate: 2
- Status: todo
- Description: Add `follows` table.
- Acceptance criteria:
1. Unique constraint on (`follower_id`, `followee_id`).
2. Self-follow is prevented.

#### BE-061 - Follow/unfollow endpoints
- Priority: P0
- Estimate: 3
- Status: todo
- Description: Implement `POST /api/follows/{userId}` and `DELETE /api/follows/{userId}`.
- Acceptance criteria:
1. Authenticated user can follow/unfollow another user.
2. Duplicate follow requests are idempotent.
3. Privacy/blocked rules (if implemented) are enforced.

#### BE-062 - Following list endpoint
- Priority: P0
- Estimate: 2
- Status: todo
- Description: Implement `GET /api/follows/me`.
- Acceptance criteria:
1. Returns list of followed users with minimal profile info.
2. Endpoint requires authentication.

#### FE-040 - Follow UI
- Priority: P0
- Estimate: 5
- Status: todo
- Description: Add user search/profile cards and follow buttons.
- Acceptance criteria:
1. User can follow/unfollow from UI.
2. Following state updates optimistically and rolls back on error.

### EPIC-8: Social Feed

#### BE-070 - Feed endpoint
- Priority: P0
- Estimate: 5
- Status: todo
- Description: Implement `GET /api/feed` for followed users' latest results.
- Acceptance criteria:
1. Feed supports pagination.
2. Optional game filter is supported.
3. Results are ordered by played date and submission timestamp.

#### FE-050 - Feed page
- Priority: P0
- Estimate: 5
- Status: todo
- Description: Build following feed view.
- Acceptance criteria:
1. Feed shows followed users' recent results.
2. User can filter by game.
3. Empty state guides user to follow people.

## Milestone 5: Security, Quality, and Launch

### EPIC-9: Security and Privacy

#### BE-080 - Privacy settings
- Priority: P1
- Estimate: 3
- Status: todo
- Description: Add profile visibility settings (public, followers-only, private).
- Acceptance criteria:
1. Visibility setting stored per user.
2. Stats/feed/profile endpoints enforce visibility.

#### BE-081 - Input and abuse protections
- Priority: P1
- Estimate: 3
- Status: todo
- Description: Add request limits and payload size caps.
- Acceptance criteria:
1. Auth endpoints have brute-force mitigation.
2. Paste endpoint has payload length limit.
3. Standardized error response for rate-limited requests.

### EPIC-10: Testing and Observability

#### QA-001 - Backend test suite expansion
- Priority: P0
- Estimate: 5
- Status: todo
- Description: Add unit and integration tests for auth, parser, results, follows, and stats.
- Acceptance criteria:
1. Critical paths have automated integration tests.
2. Each game module has detector/parser unit coverage.

#### QA-002 - Frontend test suite expansion
- Priority: P1
- Estimate: 5
- Status: todo
- Description: Add tests for auth flow, paste workflow, graph rendering, and follow/feed interactions.
- Acceptance criteria:
1. Core user journeys have automated tests.
2. Ambiguity flow in paste parser is covered.

#### OPS-010 - Logging and metrics
- Priority: P1
- Estimate: 3
- Status: todo
- Description: Add structured logs and baseline metrics.
- Acceptance criteria:
1. Request IDs are present in logs.
2. Parse failures and detector ambiguity rates are measurable.

## Nice-to-have backlog

#### FE-090 - Compare with friends view
- Priority: P2
- Estimate: 5
- Status: todo
- Description: Side-by-side weekly performance comparison between current user and followed users.

#### BE-090 - Re-parse historical raw results
- Priority: P2
- Estimate: 3
- Status: todo
- Description: Admin/task endpoint to re-run parser improvements on stored raw text.

#### FE-091 - Import/export personal data
- Priority: P2
- Estimate: 3
- Status: todo
- Description: Let users export their game history and account data.

## Suggested sprint slices
- Sprint 1: BE-001, FE-001, OPS-001, BE-010, BE-011, BE-012, BE-013, FE-010
- Sprint 2: BE-020, BE-021, BE-030, BE-031, BE-032, BE-033, BE-034, BE-040, FE-020
- Sprint 3: BE-041, BE-050, BE-051, FE-030, FE-031
- Sprint 4: BE-060, BE-061, BE-062, FE-040, BE-070, FE-050
- Sprint 5: BE-080, BE-081, QA-001, QA-002, OPS-010, polish and release prep
