# ADR-001: Elección de Stack Backend — Spring Boot vs Rust/Actix

## Contexto
Necesitamos elegir el stack backend para una plataforma SaaS de gestión académica con operaciones CRUD complejas, relaciones profundas entre entidades, y reglas de negocio cambiantes durante el desarrollo del MVP.

## Opciones Consideradas
1. **Spring Boot 3 + Java 21** — Framework maduro con ecosistema enterprise completo
2. **Rust + Actix-web** — Alto rendimiento, sin GC, memory-safe

## Decisión
**Spring Boot 3 + Java 21 (Virtual Threads)**

## Justificación
| Factor | Spring Boot | Rust/Actix |
|--------|-------------|-------------|
| Velocidad de desarrollo MVP | Alta | Baja |
| ORM con migraciones | Spring Data JPA + Flyway | Diesel/SeaORM (menos maduro) |
| Seguridad (RBAC, JWT, OAuth2) | Spring Security (maduro) | Requiere construir desde cero |
| Modelo de dominio complejo | JPA anotaciones | Más código manual |
| Disponibilidad de talento | Alta | Baja |
| Virtual Threads (Java 21) | Cierra brecha de concurrencia | — |

El perfil de carga del sistema (CRUD académico B2B) no justifica el overhead de desarrollo de Rust.

## Consecuencias
- Positivas: MVP más rápido, seguridad enterprise out-of-the-box, fácil contratación futura
- Negativas: Mayor consumo de memoria que Rust, GC pauses (mitigado por Virtual Threads)
- Futuro: Si un microservicio específico (ej. motor de notificaciones) requiere ultra-baja latencia, podría implementarse en Rust como pieza aislada

## Estado
Aceptado
