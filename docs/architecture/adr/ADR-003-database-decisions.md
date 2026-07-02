# ADR-003: Decisiones de Base de Datos

## Contexto
El modelo de datos académico tiene relaciones profundas y requiere integridad referencial fuerte.

## Decisiones
1. **PostgreSQL 16** sobre MySQL — mejor soporte de tipos, índices parciales, RLS futuro
2. **UUID como PK** — evita enumeración de recursos, facilita particionamiento futuro
3. **Flyway** sobre Liquibase — migraciones SQL puras, más simple, versionado explícito
4. **3FN como norma** — tabla `period_averages` es excepción documentada por performance
5. **snake_case** para tablas y columnas — consistencia con PostgreSQL

## Consecuencias
- UUIDs como PK tienen impacto en performance de índices vs BIGSERIAL (mitigado con valores secuenciales o ULIDs a futuro)
- Separación de migraciones por bounded context (V1__identity, V2__academic, etc.)

## Estado
Aceptado
