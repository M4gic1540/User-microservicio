# ShopSmart — Microservicio de Usuarios

Microservicio REST construido con **Spring Boot 3.2** para la gestión de usuarios, autenticación JWT, direcciones de envío y preferencias de personalización.

---

## Arquitectura

```
shopsmart-usuarios/
├── src/main/java/com/shopsmart/usuarios/
│   ├── controller/
│   │   ├── AuthController.java        # POST /auth/registro, /auth/login
│   │   ├── UsuarioController.java     # GET/PUT /usuarios/me, direcciones, preferencias
│   │   └── AdminController.java       # ADMIN: listar, buscar, desactivar usuarios
│   ├── service/
│   │   ├── UsuarioService.java        # Lógica de negocio principal
│   │   └── UsuarioDetailsService.java # Integración con Spring Security
│   ├── model/
│   │   ├── Usuario.java
│   │   ├── DireccionEnvio.java
│   │   └── PreferenciaUsuario.java
│   ├── dto/
│   │   ├── UsuarioDTO.java            # Request/Response de usuario y auth
│   │   ├── DireccionDTO.java
│   │   └── PreferenciaDTO.java
│   ├── repository/                    # Interfaces JPA
│   ├── config/
│   │   ├── SecurityConfig.java        # Configuración Spring Security
│   │   ├── JwtUtil.java               # Generación y validación JWT
│   │   ├── JwtAuthFilter.java         # Filtro de autenticación por request
│   │   └── SwaggerConfig.java         # OpenAPI / Swagger UI
│   └── exception/                     # Excepciones tipadas + handler global
├── docker-compose.yml
├── Dockerfile
└── pom.xml
```

---

## Requisitos

- Java 17+
- Maven 3.9+
- Docker y Docker Compose

---

## Levantamiento rápido con Docker

```bash
# 1. Clonar y entrar al proyecto
cd shopsmart-usuarios

# 2. Levantar base de datos + microservicio
docker-compose up -d

# 3. Verificar que estén corriendo
docker-compose ps

# 4. (Opcional) Levantar también pgAdmin en localhost:5050
docker-compose --profile tools up -d
```

---

## Levantamiento en desarrollo local

```bash
# 1. Levantar solo PostgreSQL
docker-compose up -d postgres

# 2. Compilar y correr el microservicio
./mvnw spring-boot:run

# O con variables de entorno personalizadas:
DB_USER=postgres DB_PASSWORD=postgres123 ./mvnw spring-boot:run
```

---

## Endpoints principales

| Método | Ruta | Descripción | Auth |
|--------|------|-------------|------|
| POST | `/api/v1/auth/registro` | Crear cuenta nueva | Pública |
| POST | `/api/v1/auth/login` | Obtener tokens JWT | Pública |
| GET | `/api/v1/usuarios/me` | Ver mi perfil | JWT |
| PUT | `/api/v1/usuarios/me` | Actualizar perfil | JWT |
| DELETE | `/api/v1/usuarios/me` | Desactivar cuenta | JWT |
| GET | `/api/v1/usuarios/me/direcciones` | Mis direcciones | JWT |
| POST | `/api/v1/usuarios/me/direcciones` | Agregar dirección | JWT |
| DELETE | `/api/v1/usuarios/me/direcciones/{id}` | Eliminar dirección | JWT |
| GET | `/api/v1/usuarios/me/preferencias` | Mis preferencias | JWT |
| PUT | `/api/v1/usuarios/me/preferencias` | Actualizar preferencias | JWT |
| GET | `/api/v1/admin/usuarios` | Listar usuarios | ADMIN |
| GET | `/api/v1/admin/usuarios/buscar?q=` | Buscar usuarios | ADMIN |

---

## Swagger UI

Disponible en: [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html)

Para probar endpoints protegidos:
1. Llama a `POST /api/v1/auth/login`
2. Copia el `accessToken` de la respuesta
3. En Swagger, haz clic en **Authorize** → pega el token

---

## Variables de entorno

| Variable | Descripción | Default |
|----------|-------------|---------|
| `DB_USER` | Usuario PostgreSQL | `postgres` |
| `DB_PASSWORD` | Contraseña PostgreSQL | `postgres` |
| `JWT_SECRET` | Secreto para firmar JWT | ver application.yml |
| `SPRING_DATASOURCE_URL` | URL de conexión | `jdbc:postgresql://localhost:5432/shopsmart_usuarios` |

---

## Tests

```bash
# Correr todos los tests
mvn test

# Usando el wrapper en Windows
./mvnw.cmd test

# Verificacion completa (test + empaquetado)
mvn -DskipTest=false verify
```

---

## API Gateway

El archivo `application-gateway.yml` contiene la configuración para enrutar tráfico desde el gateway hacia este servicio. El gateway escucha en el puerto `8080` y redirige `/api/v1/usuarios/**` al puerto `8081`.

Para activar Circuit Breaker en caídas del servicio, agregar al `pom.xml` del gateway:

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-gateway</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-circuitbreaker-reactor-resilience4j</artifactId>
</dependency>
```
