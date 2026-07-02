# ADR-002: Arquitectura Hexagonal con Bounded Contexts

## Contexto
El sistema debe escalar a producción sin reescrituras mayores y permitir que múltiples desarrolladores trabajen en paralelo sin conflictos.

## Decisión
Arquitectura Hexagonal (Ports & Adapters) con organización en Bounded Contexts (DDD táctico):
- `identity` — usuarios, roles, autenticación
- `academic` — cursos, matrículas, notas, asistencias
- `communications` — mensajes, notificaciones
- `reporting` — reportes, métricas

Cada contexto tiene 4 capas: domain → application → infrastructure → presentation.

## Consecuencias
- El dominio nunca depende de infraestructura
- Los bounded contexts se comunican vía interfaces explícitas, no importando entidades internas
- Preparado para futura extracción a microservicios
- Verificado con ArchUnit en tests de arquitectura

## Estado
Aceptado
