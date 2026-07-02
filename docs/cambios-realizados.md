# Resumen de Cambios Realizados

## Problemas Identificados y Soluciones

### 1. Pantalla Azul al Iniciar Sesión (Blank Screen)

**Causa:** `AuthPage.tsx` redirigía a `/app/dashboard` (ruta vacía sin componente asociado) en vez de al dashboard específico del rol del usuario. No existía una ruta `/app/dashboard` con un componente real; solo había `/app/director`, `/app/secretary`, `/app/teacher`, `/app/student`.

**Solución:**
- Se creó la función `getDashboardPath(user)` en `useAuthStore.ts:25` que mapea cada rol a su ruta:
  - `DIRECTOR` → `/app/director`
  - `SECRETARY` → `/app/secretary`
  - `TEACHER` → `/app/teacher`
  - `STUDENT` → `/app/student`
  - `null` o rol desconocido → `/login`

**Archivos modificados:**
- `frontend/src/shared/store/useAuthStore.ts` — nueva función `getDashboardPath()`
- `frontend/src/features/auth/pages/AuthPage.tsx:22` — usa `getDashboardPath()`
- `frontend/src/app/routes/router.tsx` — nuevo componente `RoleDashboardRedirect` para `/app` (index) y `/app/dashboard`
- `frontend/src/shared/components/navigation/Sidebar.tsx` — link "Dashboard" dinámico por rol; fix de `</nav>` faltante; fix de crash cuando `user` es null
- `frontend/src/features/auth/components/AnimatedForm.tsx` — navegación post-login con `getDashboardPath()`
- `frontend/src/features/auth/components/LoginForm.tsx` — navegación post-login con `getDashboardPath()`

---

### 2. Sesión se Perdía al Recargar Página

**Causa:** `ProtectedRoute`, `RoleGuard` y `httpClient` dependían de la hidratación asíncrona de Zustand `persist`. Al recargar, había un lapso donde el store decía "no autenticado", causando redirección a `/login` antes de que la rehidratación terminara.

**Solución:** Leer `localStorage['auth-storage']` sincrónicamente como fallback mientras Zustand se hidrata.

**Archivos modificados:**
- `frontend/src/app/routes/ProtectedRoute.tsx:4-12` — nueva función `readPersistedAuth()` que accede a localStorage directamente
- `frontend/src/app/routes/RoleGuard.tsx:5-13` — nueva función `readPersistedRoles()` similar
- `frontend/src/shared/api/httpClient.ts:13-20` — lectura sincrónica de `accessToken` desde `localStorage['auth-storage']` al cargar módulo
- `frontend/src/shared/api/httpClient.ts:42-58` — request interceptor con fallback a localStorage si `accessToken` es null

---

### 3. Error 500 en Endpoints del Dashboard (Redis Serialization)

**Causa:** Los use cases de reporting (`GetInstitutionalOverviewUseCase`, `GetCoursePerformanceUseCase`, `GetAttendanceTrendUseCase`) tenían anotación `@Cacheable`, que intentaba serializar los records de resultados a Redis. Los records de Java (con `LocalDate`) no eran serializables por el serializador Jdk por defecto de Redis.

**Solución:** Eliminar `@Cacheable` de los 3 use cases. Las queries son simples (counts y promedios), Redis cache agregaba complejidad innecesaria para MVP.

**Archivos modificados:**
- `backend/.../reporting/application/usecase/GetInstitutionalOverviewUseCase.java`
- `backend/.../reporting/application/usecase/GetCoursePerformanceUseCase.java`
- `backend/.../reporting/application/usecase/GetAttendanceTrendUseCase.java`

---

### 4. Componente At-Risk Students: `studentName` y `sectionName` Vacíos

**Causa:** El DTO `AtRiskStudentResponse.java` no tenía campo `studentName`, y el `sectionName` era un valor fake generado en `DetectAtRiskStudentsUseCase` ("Section-" + UUID truncado).

**Solución:**
- Agregado `studentName` a `AtRiskStudentResponse.java`
- En `ReportsController.java`, inyectar `SpringDataStudentRepository`, `SpringDataUserRepository` y `SpringDataCourseSectionRepository` para hacer lookup de nombres reales
- Cachear en `HashMap` para evitar N+1 queries
- El `sectionName` real se obtiene de la tabla `course_sections`

**Archivos modificados:**
- `backend/.../reporting/presentation/dto/AtRiskStudentResponse.java` — nuevo campo `studentName`
- `backend/.../reporting/presentation/controller/ReportsController.java:73-91` — lookup de nombres con caché

---

### 5. Componente Course Performance: `attendanceRate` Siempre 0

**Causa:** `CourseReportRepositoryAdapter.java` hardcodeaba `BigDecimal.ZERO` para `attendanceRate`.

**Solución:**
- Inyectar `SpringDataAttendanceRepository` en `CourseReportRepositoryAdapter`
- Para cada sección, obtener `enrollmentIds`, consultar asistencias con `findByEnrollmentIdIn()`, y calcular el porcentaje de asistencias `PRESENT` o `LATE`

**Archivos modificados:**
- `backend/.../reporting/infrastructure/adapter/CourseReportRepositoryAdapter.java` — nueva dependencia, cálculo de attendanceRate

---

### 6. Bucle Infinito de Recarga (Infinite Refresh Loop)

**Causa:** Cuando el token expiraba, el interceptor 401 de `httpClient.ts` hacía `window.location.href = '/login'` (recarga completa de página) **sin limpiar** `localStorage['auth-storage']`. Al recargar, Zustand `persist` rehidrataba el estado obsoleto (`isAuthenticated: true` con token expirado), `AuthPage` redirigía al dashboard, las APIs respondían 401 otra vez, y el ciclo se repetía infinitamente.

**Solución:** Agregar `localStorage.removeItem('auth-storage')` antes del hard redirect.

**Archivos modificados:**
- `frontend/src/shared/api/httpClient.ts:108` — limpiar estado persistido antes de redirigir

---

### 7. Páginas Teachers y Students sin Datos

**Causa:** Los endpoints `GET /api/v1/teachers` y `GET /api/v1/students` no existían. Solo había:
- `GET /api/v1/teachers/me` — perfil del profesor autenticado
- `GET /api/v1/teachers/me/sections` — secciones del profesor autenticado
- `GET /api/v1/students/me/*` — endpoints para el estudiante autenticado
- `GET /api/v1/students/{id}` — estudiante por ID (requiere DIRECTOR)

**Solución:** Crear ambos endpoints:
- `TeacherController.getAll()` — obtiene todos los profesores con `teacherRepository.findAll()`, cruza con `userRepository.findById()` para obtener `fullName`, `email`, `status`
- `StudentController.getAll()` — similar usando `jpaStudentRepository.findAll()`

DTOs creados:
- `TeacherListDto` — `id`, `fullName`, `email`, `specialty`, `hireDate`, `status`
- `StudentListDto` — `id`, `enrollmentCode`, `fullName`, `email`, `guardian`, `status`

**Archivos nuevos:**
- `backend/.../academic/presentation/dto/TeacherListDto.java`
- `backend/.../academic/presentation/dto/StudentListDto.java`

**Archivos modificados:**
- `backend/.../academic/presentation/controller/TeacherController.java` — nuevo endpoint `GET /api/v1/teachers`
- `backend/.../academic/presentation/controller/StudentController.java` — nuevo endpoint `GET /api/v1/students`

---

### 8. Build: Volumen Maven Cache para Compilación Rápida

Para evitar descargar dependencias en cada build, se creó un volumen Docker `maven-repo-cache` que persiste `.m2/repository` entre ejecuciones de `docker run --rm maven:3.9-eclipse-temurin-21-alpine mvn clean package`.

---

## Estructura del Proyecto

### Backend (Spring Boot 3.x, Java 21, Maven)
- **Módulo único:** `backend/pom.xml` con sub-paquetes:
  - `com.academicsaas.academic` — entidades académicas (cursos, alumnos, profesores, secciones, matrículas, evaluaciones)
  - `com.academicsaas.identity` — usuarios, roles, autenticación
  - `com.academicsaas.reporting` — reportes del dashboard
  - `com.academicsaas.shared` — utilidades compartidas
- **Base de datos:** PostgreSQL 16 con Flyway migrations (seed data en `V10__seed_academic_data.sql`)
- **Cache:** Redis 7 (deshabilitado para reporting por problemas de serialización)

### Frontend (React 18 + TypeScript + Vite)
- **Router:** React Router v6 (`createBrowserRouter`)
- **State management:** Zustand con `persist` middleware (almacena en `localStorage['auth-storage']`)
- **Data fetching:** TanStack React Query
- **HTTP client:** Axios con interceptors para token y refresh
- **Auth flow:** Login → JWT (access + refresh tokens) → store en Zustand + localStorage

### Infraestructura (Docker Compose)
- `docker-compose.yml` define 3 servicios: `backend`, `postgres`, `redis`
- Backend expone puerto `8080`
- Frontend se corre manual con `npm run dev` (puerto `5173` con proxy a `8080`)

---

## Comandos Útiles

### Backend

```bash
# Build JAR (saltando tests y checkstyle)
docker run --rm -v maven-repo-cache:/root/.m2/repository -v "${PWD}:/app" -w /app maven:3.9-eclipse-temurin-21-alpine mvn -q clean package "-Dmaven.test.skip=true" "-Dcheckstyle.skip=true"

# Copiar JAR al contenedor
docker cp target/academic-saas-backend-0.0.1-SNAPSHOT.jar academic-saas-backend-1:/app/app.jar

# Flush Redis cache
docker exec academic-saas-redis-1 redis-cli FLUSHALL

# Reiniciar backend
docker restart academic-saas-backend-1
```

### Frontend

```bash
# Iniciar servidor de desarrollo
npm run dev
```

---

## Credenciales de Prueba

| Rol       | Email                            | Contraseña | Código Institución |
|-----------|----------------------------------|------------|-------------------|
| DIRECTOR  | director.inst1@academia.com      | Admin123   | CSM-001           |
| SECRETARY | secretary.inst1@academia.com     | Admin123   | CSM-001           |
| TEACHER   | teacher.inst1.1@academia.com     | Admin123   | CSM-001           |
| STUDENT   | student.inst1.1@academia.com     | Admin123   | CSM-001           |
