# Comandos para levantar el proyecto

## 1. DB + Redis (Docker)

```powershell
cd "Academy\academic-saas"
docker compose up -d postgres redis
```

## 2. Backend (Spring Boot)

```powershell
# Opción A: vía Docker (recomendado)
docker compose up -d backend

# Opción B: local con Maven (requiere Java 21 + Maven)
cd "Academy\academic-saas\backend"
mvn spring-boot:run
```

## 3. Verificar estado

```powershell
docker ps --filter "name=academic-saas" --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
```

## URLs

| Servicio  | URL                                         |
|-----------|---------------------------------------------|
| Backend   | http://localhost:8080                        |
| Swagger   | http://localhost:8080/swagger-ui.html        |
| API Docs  | http://localhost:8080/api-docs               |
