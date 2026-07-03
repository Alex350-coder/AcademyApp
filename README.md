# Academic SaaS

Plataforma SaaS de gestión académica multi-tenant y multi-rol (Director, Secretaria, Profesor, Alumno). Cada institución educativa gestiona sus propios cursos, secciones, matrículas, asistencias y calificaciones de forma aislada.

## Roles y funcionalidades

| Rol | Funcionalidades principales |
|-----|------------------------------|
| **Director** | Reportes institucionales, gestión de profesores/alumnos/cursos/aulas, detección de alumnos en riesgo |
| **Secretaria** | Asistente de matrícula, registro de asistencia por sección |
| **Profesor** | Sus secciones, registro de asistencia, evaluaciones y notas |
| **Alumno** | Sus cursos, notas, asistencia y horario |

## Tecnologías

| Capa | Tecnología |
|------|-----------|
| Backend | Java 21, Spring Boot 3.4, Spring Security (JWT), Spring Data JPA, Flyway |
| Frontend | React 18, TypeScript 5, Vite 8 (Rolldown), TailwindCSS 3, TanStack Query, Zustand |
| Base de datos | PostgreSQL 16, Redis 7 |
| Testing | JUnit 5, ArchUnit, Mockito, Vitest, Playwright |

Backend organizado en arquitectura hexagonal (domain / application / infrastructure / presentation), verificada automáticamente con ArchUnit.

## Arranque rápido (Docker)

La forma más simple de levantar todo el stack (Postgres + Redis + backend + frontend):

```bash
docker compose up -d --build
```

- Frontend: http://localhost:3000
- Backend API: http://localhost:8080/api/v1
- Postgres: `localhost:5432` (usuario/clave: `postgres`/`postgres`)
- Redis: `localhost:6379`

Los datos de ejemplo (10 instituciones con directores, profesores, alumnos, cursos y notas) se cargan automáticamente vía migraciones Flyway. Credenciales de prueba:

| Rol | Email | Contraseña | Código Institución |
|-----|-------|------------|---------------------|
| DIRECTOR | `director.inst1@academia.com` | `Admin123` | `CSM-001` |
| SECRETARY | `secretary.inst1@academia.com` | `Admin123` | `CSM-001` |
| TEACHER | `teacher.inst1.1@academia.com` | `Admin123` | `CSM-001` |
| STUDENT | `student.inst1.1@academia.com` | `Admin123` | `CSM-001` |

> Nota: si cambias `package.json` en `frontend/`, regenera `package-lock.json` con Node en Linux/Docker antes de reconstruir la imagen (`npm ci` en el contenedor puede rechazar un lockfile generado con una versión de npm distinta a la de la imagen).

## Arranque local (sin Docker)

### Requisitos

- Java 21 (LTS) + Maven 3.9+
- Node.js 22+
- PostgreSQL 16 y Redis 7 corriendo localmente

### Backend

```bash
cd backend
mvn clean install
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Frontend

```bash
cd frontend
npm install
npm run dev
```

## Variables de entorno (backend)

| Variable | Default | Descripción |
|----------|---------|-------------|
| `DATASOURCE_URL` | `jdbc:postgresql://localhost:5432/academic_saas` | URL de conexión |
| `DATASOURCE_USERNAME` | `postgres` | Usuario BD |
| `DATASOURCE_PASSWORD` | `postgres` | Contraseña BD |
| `REDIS_HOST` | `localhost` | Host Redis |
| `SERVER_PORT` | `8080` | Puerto del servidor |
| `JWT_SECRET` | *(requerido en prod)* | Secreto de firma JWT, mínimo 256 bits |
| `JWT_ISSUER` | *(requerido en prod)* | Emisor de los tokens JWT |

En `docker-compose.prod.yml` estas variables se toman del entorno; nunca hardcodear secretos de producción.

## Perfiles Spring

- **dev**: SQL logueado, pool pequeño, hot-reload
- **prod**: log mínimo, pool optimizado, sin debug SQL

## Estructura

```
academic-saas/
  backend/     → Spring Boot (arquitectura hexagonal)
  frontend/    → React + Vite SPA
  docs/        → ADRs, diagramas, documentación de arquitectura
  .github/     → CI (build/test backend y frontend, e2e, security scan)
  docker-compose.yml       → Stack de desarrollo
  docker-compose.prod.yml  → Stack de producción
```

## Comandos útiles

```bash
# Backend
mvn clean install              # Build completo (tests + checkstyle)
mvn test                       # Solo tests
mvn checkstyle:check           # Linter

# Frontend
npm run dev                    # Dev server
npm run build                  # Build de producción (tsc + vite build)
npm run lint                   # ESLint
npm run test                   # Vitest
npm run test:e2e               # Playwright
```

## Seguridad

Las peticiones se aíslan por institución (multi-tenant) y se autentican vía JWT. Antes de desplegar a producción:

- Define `JWT_SECRET`/`JWT_ISSUER` propios (nunca uses el valor de desarrollo del `docker-compose.yml`).
- Configura `CORS` con el origen real del frontend (`SecurityConfig`).
- Revisa `docs/` para el historial de decisiones de arquitectura y seguridad.
