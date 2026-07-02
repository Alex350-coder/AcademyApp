# Academic SaaS

Plataforma SaaS de gestión académica multi-rol (Director, Secretaria, Profesor, Alumno). MVP construido con Spring Boot 3 + React + PostgreSQL.

## Tecnologías

| Capa | Tecnología |
|------|-----------|
| Backend | Java 21, Spring Boot 3.4, Spring Security, Spring Data JPA |
| Frontend | React 18, TypeScript 5, Vite 5, TailwindCSS 3 |
| Base de datos | PostgreSQL 16, Flyway, Redis |
| Testing | JUnit 5, Testcontainers, ArchUnit, Mockito |

## Requisitos

- Java 21 (LTS)
- Maven 3.9+
- Node.js 20+
- PostgreSQL 16
- Redis 7+

## Arranque local

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

## Variables de entorno

| Variable | Default | Descripción |
|----------|---------|-------------|
| `DATASOURCE_URL` | `jdbc:postgresql://localhost:5432/academic_saas` | URL de conexión |
| `DATASOURCE_USERNAME` | `postgres` | Usuario BD |
| `DATASOURCE_PASSWORD` | `postgres` | Contraseña BD |
| `REDIS_HOST` | `localhost` | Host Redis |
| `SERVER_PORT` | `8080` | Puerto del servidor |

## Perfiles

- **dev**: SQL logueado, pool pequeño, hot-reload
- **prod**: Log mínimo, pool optimizado, sin debug SQL

## Estructura

```
academic-saas/
  backend/     → Spring Boot (Arquitectura Hexagonal)
  frontend/    → React + Vite SPA
  docs/        → ADRs, diagramas, documentación
```

## Comandos útiles

```bash
# Backend
mvn clean install              # Build completo
mvn test                       # Tests
mvn checkstyle:check           # Linter

# Frontend
npm run dev                    # Dev server
npm run build                  # Build producción
npm run lint                   # Linter
```
