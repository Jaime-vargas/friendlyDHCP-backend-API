# friendlyDHCP-backend-API

Backend desarrollado en **Java 21 + Spring Boot 3.5.6** que permite administrar redes y dispositivos, generar dinámicamente el archivo `dhcpd.conf` y desplegarlo remotamente en un servidor Linux mediante SSH.

El sistema automatiza la creación, validación y aplicación de configuraciones DHCP, eliminando la edición manual del archivo y reduciendo errores.

---

## Tecnologías Utilizadas

- Java 21
- Spring Boot 3.5.6
- Spring Web
- Spring Data JPA
- MySQL
- Hibernate
- JSch (SSH + SFTP)
- Docker
- Docker Compose
- Nginx (para el frontend)

---

## Arquitectura

Arquitectura en capas siguiendo separación de responsabilidades:
```
src/main/java/com/app/dhcp
├── controller
├── service
├── repository
├── model
├── dto
├── mapper
├── security
├── exeption
├── exeptionsHandler
└── Valid
```

### Capas principales

- **Controller** → Expone endpoints REST
- **Service** → Lógica de negocio
- **Repository** → Acceso a datos (JPA)
- **DTO** → Separación entre modelo interno y exposición externa
- **Mapper** → Transformación DTO ↔ Entity
- **Global Exception Handler** → Manejo centralizado de errores

---

## Endpoints 

### Networks

- `GET /api/v1/networks`
- `GET /api/v1/networks/{id}`
- `POST /api/v1/networks`
- `PUT /api/v1/networks/{id}`
- `DELETE /api/v1/networks/{id}`

### Devices

- `GET /api/v1/devices`
- `POST /api/v1/devices`
- `PUT /api/v1/devices/{id}`
- `DELETE /api/v1/devices/{id}`

### Configuration

- `GET /api/v1/configuration`
- `POST /api/v1/configuration`
- `POST /api/v1/configuration/apply`

---

##  Base de Datos

- MySQL
- JPA / Hibernate
- Relación:
    - Network (1) → (N) Devices
- Constraints a nivel base de datos:
    - MAC única
    - IP única
    - Nombre único

Configuración mediante variables de entorno:
```
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PASS}
``` 
---
``` 
NOTA: el servidor cuenta con:
jwt.secret.key=${JWT_SECRET_KEY}
jwt.time.expiration=${JWT_TOKEN_EXPIRATION}

Aunque no se ha implementado la seguridad JWT, estas variables están preparadas para futuras implementaciones de autenticación y autorización.
por lo que deben ser asignadas en el entorno de ejecución para evitar errores de configuración, aunque actualmente no se utilicen.
``` 

---

## Generación del Archivo `dhcpd.conf`

El archivo se genera dinámicamente utilizando `StringBuilder`.

### Flujo:

1. Obtiene redes y dispositivos desde la base de datos
2. Construye estructura DHCP:
    - Subnets
    - Rango
    - Routers
    - DNS
    - Host reservations (MAC + fixed-address)
3. Guarda archivo temporal en carpeta raíz
4. Copia archivo vía SFTP al servidor Linux
5. Ejecuta comandos remotos:
    - Mover archivo a ubicación final
    - Reiniciar servicio DHCP (o contenedor Docker)

### Ejecución SSH

Se utiliza `SSHClient` + `SFTPClient`:

- Conexión por usuario y contraseña
- Ejecución de comandos remotos configurables
- Manejo de excepciones IO
- Retorno de HTTP Status al frontend

---

## Seguridad

- CORS configurado
- Variables sensibles gestionadas mediante **environment variables**
- Sistema preparado para JWT (actualmente no activo)

---

## Despliegue

Arquitectura en contenedores:
- Backend → Docker
- Base de datos → Docker
- Frontend → Docker + Nginx
- Comunicación interna en red privada

Uso de `docker-compose` para orquestación.

---

## Manejo de Errores

- `@ControllerAdvice` para manejo global
- Excepciones personalizadas (`HandleException`)
- Códigos HTTP apropiados
- Validaciones propias en capa Service

---

## ▶ Ejecución Local

```bash
mvn clean install
mvn spring-boot:run
