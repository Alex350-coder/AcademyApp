# Academic SaaS

Plataforma de gestión académica multi-tenant y multi-rol (Director, Secretaria, Profesor, Alumno). Cada institución educativa gestiona sus propios cursos, secciones, matrículas, asistencias y calificaciones de forma aislada del resto de instituciones.

> **Sobre este proyecto**: es un proyecto personal, construido para mi portafolio y como ejercicio de aprendizaje de arquitectura hexagonal, patrones multi-tenant y hardening de seguridad en una aplicación full-stack real. No es una aplicación comercial ni está pensada para producción tal cual — no todos los flujos están terminados de punta a punta (ver [Limitaciones conocidas](#limitaciones-conocidas)). El objetivo principal fue practicar diseño de backend con Spring Boot, un frontend React moderno, y un proceso real de auditoría/corrección de seguridad sobre una base de código ya existente.

## Roles y funcionalidades

| Rol | Funcionalidades |
|-----|------------------|
| **Director** | Dashboard de reportes institucionales (promedio general, asistencia, alumnos en riesgo, rendimiento por curso, tendencia de asistencia), gestión CRUD de profesores, alumnos, cursos y aulas |
| **Secretaria** | Asistente de matrícula (búsqueda de alumno → selección de sección → confirmación), registro de asistencia diaria por sección |
| **Profesor** | Sus secciones asignadas, registro de asistencia, creación de evaluaciones y registro de notas |
| **Alumno** | Sus cursos, notas por evaluación, resumen de asistencia por curso |

Cada institución tiene su propio código (ej. `CSM-001`) usado al iniciar sesión; los datos de una institución nunca son visibles para otra.

## Tecnologías

| Capa | Tecnología |
|------|-----------|
| Backend | Java 21, Spring Boot 3.4, Spring Security (JWT), Spring Data JPA, Flyway, MapStruct |
| Frontend | React 18, TypeScript 5, Vite 8 (Rolldown), TailwindCSS 3, TanStack Query, Zustand, React Router 7, Recharts |
| Base de datos | PostgreSQL 16, Redis 7 (caché) |
| Testing | JUnit 5, ArchUnit, Mockito, Vitest, Playwright |

### Arquitectura backend

El backend sigue **arquitectura hexagonal** (puertos y adaptadores), separada en cuatro capas por cada contexto (`academic`, `identity`, `reporting`, `communications`, `shared`):

```
domain/          → Entidades y reglas de negocio puras, sin dependencias externas
application/     → Casos de uso (orquestan el dominio, sin conocer HTTP/JPA)
infrastructure/  → Adaptadores concretos: JPA, seguridad, almacenamiento de archivos
presentation/    → Controladores REST y DTOs
```

Estas reglas de dependencia (dominio no depende de infraestructura, application no depende de infraestructura/presentation, cada contexto no accede a los internos de otro) se verifican automáticamente en cada build con un test de **ArchUnit** — si alguien rompe la arquitectura, el build falla.

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

Hay 10 instituciones sembradas (`director.inst1@academia.com` … `director.inst10@academia.com`, misma contraseña) útiles para verificar en vivo que los datos de una institución no se filtran a otra.

> **Nota Windows**: si cambias `package.json` en `frontend/`, regenera `package-lock.json` con Node en Linux/Docker antes de reconstruir la imagen — `npm ci` dentro del contenedor puede rechazar un lockfile generado con una versión de npm distinta a la de la imagen (`node:22-alpine` trae npm 10.x; npm en Windows suele ser 11.x).

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
  backend/       → Spring Boot (arquitectura hexagonal, ver arriba)
  frontend/      → React + Vite SPA (features por rol bajo src/features/dashboard/<rol>)
  .github/       → CI (build/test backend y frontend, e2e, security scan)
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

Este proyecto pasó por una auditoría de seguridad propia (path traversal, aislamiento multi-tenant, mass assignment, XSS, SQLi) que sirvió como ejercicio práctico de defensa en profundidad; los hallazgos críticos ya están corregidos:

- Todas las lecturas/escrituras están aisladas por institución vía un helper (`CurrentUserContext`) que resuelve la institución del usuario autenticado desde el JWT, en vez de confiar en un ID enviado por el cliente.
- El servicio de archivos valida que la ruta resuelta no escape del directorio de almacenamiento (protección contra path traversal).
- Las contraseñas se guardan con BCrypt (factor de costo 12); los JWT usan HMAC-SHA y expiración validada.

Antes de desplegar a producción real:

- Define `JWT_SECRET`/`JWT_ISSUER` propios (nunca uses el valor de desarrollo del `docker-compose.yml`).
- Configura `CORS` con el origen real del frontend en `SecurityConfig` (hoy está fijo a `localhost:3000`).

## Limitaciones conocidas

Como proyecto de portafolio, hay funcionalidades y flujos que quedaron incompletos a propósito o por alcance de tiempo:

- **No hay horario/timetable real**: las secciones no tienen día/hora/aula asociados; la pantalla "Mi Horario" del alumno lo indica en vez de mostrar datos inventados.
- **El flujo de refresh token JWT no está terminado** (no persiste el usuario/roles al refrescar el access token).
- **El envío de emails no está conectado** a un proveedor real (reseteo de contraseña, bienvenida) — los tokens se generan correctamente pero el correo no llega.
- Algunos endpoints de backend (gestión de periodos académicos, subida de archivos) existen pero no tienen una pantalla del frontend que los use todavía.

Esto se documenta a propósito para ser transparente sobre el estado real del proyecto, no para ocultarlo.
