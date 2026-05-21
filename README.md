# daydle

A community site for sharing results of daily games like wordle, parseword and maptap.

## Stack
- Backend: Spring Boot (Java 17), Spring Security (JWT), Spring Data JPA, H2 (dev)
- Frontend: React + TypeScript + Vite + React Router + TanStack Query

## Features implemented
- User registration, login, and authenticated profile lookup
- Modular game detection/parsing pipeline
- Supported modules: Wordle, Maptap, Connections, Parseword
- Paste-based parse preview and save
- Per-game contribution graph data endpoint
- Follow/unfollow users
- Following feed endpoint and UI

## Run locally

### 1) Backend
From [backend/pom.xml](backend/pom.xml):

```bash
cd backend
mvn spring-boot:run
```

Backend runs at `http://localhost:8080`.

### 2) Frontend
From [frontend/package.json](frontend/package.json):

```bash
cd frontend
npm install
npm run dev
```

Frontend runs at `http://localhost:5173`.

## API overview
- Auth
	- `POST /api/auth/register`
	- `POST /api/auth/login`
	- `GET /api/auth/me`
- Games and results
	- `GET /api/games`
	- `POST /api/results/parse-preview`
	- `POST /api/results`
	- `GET /api/results/me`
- Stats
	- `GET /api/stats/me/contributions`
	- `GET /api/stats/{userId}/contributions`
	- `GET /api/stats/me/overview`
- Social
	- `GET /api/users`
	- `POST /api/follows/{userId}`
	- `DELETE /api/follows/{userId}`
	- `GET /api/follows/me`
	- `GET /api/feed`

## Notes
- Current persistence is H2 in-memory for local development.
- JWT secret in [backend/src/main/resources/application.properties](backend/src/main/resources/application.properties) is for development only.
- Detectors/parsers are intentionally lightweight and can be hardened with richer pattern support and tests.
