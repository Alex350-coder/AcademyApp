# Diagrama Entidad-Relación — Academic SaaS

```mermaid
erDiagram
    users ||--o{ user_roles : has
    roles ||--o{ user_roles : contains
    roles ||--o{ role_permissions : grants
    permissions ||--o{ role_permissions : assigned

    users ||--o{ refresh_tokens : owns
    users ||--o{ audit_logs : performs
    users ||--o{ teachers : is
    users ||--o{ students : is

    courses ||--o{ course_sections : has
    academic_periods ||--o{ course_sections : defines
    teachers ||--o{ course_sections : teaches
    classrooms ||--o{ course_sections : located

    course_sections ||--o{ schedules : scheduled
    course_sections ||--o{ enrollments : contains
    course_sections ||--o{ evaluations : evaluates

    enrollments ||--o{ attendances : tracks
    students ||--o{ enrollments : registered
    students ||--o{ grades : receives
    students ||--o{ period_averages : calculated

    evaluations ||--o{ grades : results
    evaluation_types ||--o{ evaluations : categorizes

    users ||--o{ messages : sends
    users ||--o{ messages : receives
    users ||--o{ notifications : receives

    report_snapshots }o--|| users : generated

    system_settings }o--|| users : updated
```

## Convenciones

- **PK**: UUID en todas las tablas (columna `id`)
- **Timestamps**: `created_at`, `updated_at` en todas las tablas
- **Soft-delete**: `deleted_at` en `users`, `courses`, `students`
- **FKs**: `ON DELETE RESTRICT` por defecto, `CASCADE` solo en composición real
- **Nombres**: snake_case, plural

## Excepciones a 3FN

- `period_averages`: tabla derivada (cache) para performance de dashboard. Se recalcula periódicamente vía evento de dominio o tarea programada. Documentado en ADR-003.
