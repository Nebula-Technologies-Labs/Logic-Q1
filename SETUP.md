# Setup & Run

Spring Boot (Java 25) CRM API with PostgreSQL, run via Docker Compose. The build
happens inside Docker, so you don't need Java or Maven installed locally.

## Prerequisites

- Docker (or OrbStack) with Docker Compose v2

## 1. Create the `.env` file

The app reads its config from a git-ignored `.env` file (referenced by
`docker-compose.yml`). It fills the `${...}` placeholders in
`src/main/resources/application.properties`.

Create `.env` in the repo root with the following keys:

```dotenv
# Database — DB_HOST must be the compose service name: db
DB_HOST=
DB_PORT=
DB_NAME=
DB_USERNAME=
DB_PASSWORD=

# Google OAuth (must be non-empty — an OAuth2 client is configured at startup)
GOOGLE_CLIENT_ID=
GOOGLE_CLIENT_SECRET=

# JWT signing key
JWT_SECRET=

# Mail (Gmail SMTP)
EMAIL_USERNAME=
EMAIL_PASSWORD=
```

> **Get the actual values from the development team.** The `DB_*` values must
> match the `db` service credentials in `docker-compose.yml`, and `DB_HOST` must
> be `db` (the compose service name, not `localhost`).

## 2. Run

```bash
docker compose up -d --build
```

This builds the backend image (JDK 25), starts PostgreSQL, waits for it to be
healthy, runs the Flyway migrations, then starts the API.

Check it's up:

```bash
docker compose ps
docker compose logs -f backend     # look for "Started CrmApplication"
```

## 3. Access points

| Service     | URL                                            |
|-------------|------------------------------------------------|
| API         | http://localhost:8081                          |
| Swagger UI  | http://localhost:8081/swagger-ui.html          |
| OpenAPI doc | http://localhost:8081/v3/api-docs              |
| PostgreSQL  | `localhost:5432`                               |

> The API is on host port **8081**

## 4. Test it

### Option A — Swagger UI (browser)

1. Open http://localhost:8081/swagger-ui.html
2. `POST /auth/register` — create a user.
3. `POST /auth/login` — copy the returned JWT.
4. Click **Authorize** (top-right), paste the token, **Authorize** → **Close**.
5. Call any secured endpoint (e.g. `POST /organization/create`, `GET /api/users`).

### Option B - Postman/Bruno

A typical first flow: **register → login → create organization → create contacts.**

## 5. Common commands

```bash
docker compose logs -f backend          # tail backend logs
docker compose up -d --build backend    # rebuild & restart just the backend
docker compose restart backend          # restart without rebuilding
docker compose down                      # stop & remove containers
docker compose down -v                   # also delete the DB volume (fresh start)

# psql into the database
docker exec -it logicq1-db psql -U <DB_USERNAME> -d <DB_NAME>
```

## 6. Troubleshooting

- **Backend won't start, `password authentication failed for user "${DB_USERNAME}"`**
  — the `.env` file is missing or incomplete; the `${...}` placeholders weren't
  resolved. Recreate `.env` (step 1) and `docker compose up -d` again.
- **Secured call returns `401`** — your token is missing/expired (tokens expire
  quickly); log in again and re-Authorize.
- **Port already in use (5432/8081)** — stop the conflicting service or change the
  host port mapping in `docker-compose.yml`.
- **Fresh database** — `docker compose down -v` drops the volume; the next
  `up` re-runs migrations from scratch.
