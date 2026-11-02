# API y aplicación de gestión de personas, vehículos, documentos, rutas y trayectos

API REST desarrollada en Java con Spring Boot y una aplicación frontend en React + Vite para gestionar personas, usuarios, vehículos, conductores, documentación asociada, rutas y trayectos.

Este proyecto fue desarrollado como parte del curso de profundización en programación orientada a objetos del semestre 5 de la carrera de Ingeniería de Sistemas de la Universidad del Tolima. El sistema se trabajó en el contexto de Cipas y fue desarrollado por el equipo del proyecto para apoyar la gestión operativa documental, administrativa y de rutas de la organización.

## 1. Estado actual del proyecto

La solución actual incluye:

- Gestión de personas con tipo de documento y tipo de persona.
- Registro y administración de usuarios con login, contraseña y API Key.
- Registro y consulta de vehículos, tipos, placas, estados documentales y vencimientos.
- Asociación de conductores a vehículos con estados de operación.
- Administración de documentos parametrizados por tipo de vehículo.
- Consulta de rutas y trayectos por conductor, vehículo y código de ruta.
- Frontend con consultas públicas y privadas para la operación administrativa.
- Seguridad con JWT y validación adicional por API Key en rutas protegidas.

## 2. Objetivo del proyecto

La aplicación permite:

- Registrar personas con tipo de documento y tipo de persona.
- Crear usuarios administrativos asociados a personas con login, contraseña y API Key.
- Registrar vehículos con sus datos básicos y documentos requeridos.
- Asociar conductores a vehículos con estados de operación.
- Consultar vehículos y documentos según estado, tipo y vencimientos.
- Administrar documentos parametrizados por tipo de vehículo.
- Consultar rutas, trayectos y conductores asociados por placa o documento.
- Proteger los servicios administrativos mediante JWT y API Key.
- Exponer una capa frontend para consultas públicas y gestión privada.

## 3. Stack tecnológico

- Java 17
- Spring Boot 4.1.1
- Spring Web MVC
- Spring Data JPA
- Spring Security
- MySQL
- Maven
- JWT (jjwt)
- Node.js
- npm
- React 19
- Vite 7

## 4. Requisitos previos

Antes de ejecutar la API o la aplicación frontend, se requiere contar con:

- Java 17 o superior
- Maven
- MySQL
- Node.js 18 o superior
- npm (incluido con Node.js)
- Acceso a una base de datos MySQL local o configurada para la aplicación

Se recomienda verificar la instalación con los siguientes comandos:

```bash
java -version
mvn -version
mysql --version
node -v
npm -v
```

## 5. Configuración y requisitos de ejecución

Para ejecutar correctamente la solución, es necesario preparar ambos entornos:

### 5.1 Backend (Java/Spring Boot)

Requisitos:

- Java 17
- Maven
- MySQL 8 o compatible
- Puerto 8080 disponible

Configuración recomendada:

- La API se conecta a MySQL en `localhost:3307`
- Base de datos esperada: `tutoria01`
- Usuario por defecto: `root`
- Contraseña por defecto: vacía
- La aplicación crea la base de datos si no existe (`createDatabaseIfNotExist=true`)

Archivo principal de configuración:

- `src/main/resources/application.properties`

Ejemplo de configuración actual:

```properties
spring.application.name=tutoria1
server.port=8080

spring.datasource.url=jdbc:mysql://localhost:3307/tutoria01?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=

spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
spring.config.import=optional:file:.env[.properties]
google.maps.api-key=${GOOGLE_MAPS_API_KEY:}
```

Si la base de datos se corre en otro puerto o con otro usuario, se deben ajustar estos valores antes de levantar la API.

#### Notificaciones de restricción con RabbitMQ y correo

Cuando la tarea programada detecta que venció la licencia de un conductor, cambia a `RO` únicamente las asociaciones que aún no estaban restringidas y publica una notificación en RabbitMQ. El consumidor envía un correo con el conductor, los vehículos y las rutas relacionadas. Si el envío falla, RabbitMQ deriva el mensaje a `conductor.restriction.notifications.dlq`.

RabbitMQ se configura por defecto en `localhost:5672` con el usuario `guest`. Se puede ajustar mediante estas variables de entorno:

```properties
RABBITMQ_HOST=localhost
RABBITMQ_PORT=5672
RABBITMQ_USERNAME=guest
RABBITMQ_PASSWORD=
RABBITMQ_RESTRICTION_QUEUE=conductor.restriction.notifications
```

El envío de correo está desactivado hasta configurar un servidor SMTP. Para habilitarlo, define las credenciales fuera del repositorio y activa `EMAIL_NOTIFICATIONS_ENABLED`:

```properties
EMAIL_NOTIFICATIONS_ENABLED=true
MAIL_HOST=
MAIL_PORT=587
MAIL_USERNAME=
MAIL_PASSWORD=
MAIL_FROM=
MAIL_SMTP_AUTH=true
MAIL_SMTP_STARTTLS=true
```

No guardes contraseñas SMTP en `application.properties` ni en el repositorio. Con el correo desactivado, las notificaciones quedan en la cola hasta que se configure y active el consumidor.

### 5.2 Frontend (React + Vite)

Requisitos:

- Node.js 18 o superior
- npm
- Puerto 5500 disponible

Se recomienda instalar las dependencias con:

```bash
cd frontend
npm install
```

La aplicación queda configurada para ejecutarse con:

```bash
npm run dev
```

La interfaz queda disponible en:

- `http://127.0.0.1:5500`

## 6. Reglas de negocio principales

### 6.1 Personas

- `tipo_persona` puede ser:
  - `C` = Conductor
  - `A` = Administrativo
- `tipo_documento` válido: `CC`
- `numero_documento` debe ser único.
- `email` debe ser único.
- El documento de la licencia de conducción puede almacenarse en Base64.

### 6.2 Usuarios

- Un usuario solo puede existir si la persona asociada es de tipo administrativo (`A`).
- El login se genera automáticamente con la primera letra del nombre, la primera del apellido y el número de documento.
- La contraseña inicial es el número de documento.
- La API Key se genera automáticamente con UUID.
- La API Key puede regenerarse desde la ruta de usuario.

Ejemplo:

- Nombre: `Ana`
- Apellido: `García`
- Documento: `123456789`
- Login generado: `AG123456789`

### 6.2.1 Inicio del sistema y primer administrador

Cuando el sistema se inicia por primera vez, no existe un usuario administrador predefinido. El primer administrador se crea al registrar una persona de tipo administrativo (`A`). Al hacerlo, la API crea automáticamente el usuario asociado y genera el login y la contraseña inicial a partir del documento.

La regla de negocio es la siguiente:

- No existe ningún administrador inicial armado por defecto.
- Solo una persona creada con `tipoPersona: "A"` genera un usuario administrativo.
- El primer usuario administrativo debe crearse desde la aplicación o desde un request inicial del backend.
- Una vez creado, solo un usuario autenticado con permisos de administrador puede crear más personas y acceder a las rutas administrativas protegidas.

Ejemplo de creación del primer administrador con la API:

```bash
curl -X POST http://localhost:8080/persona \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Ana",
    "apellido": "García",
    "documento": "123456789",
    "tipoDocumento": "CC",
    "email": "ana@correo.com",
    "tipoPersona": "A"
  }'
```

Con ese registro, la API genera automáticamente:

- Login: `AG123456789`
- Contraseña inicial: `123456789`
- API Key: UUID aleatorio

Luego, el acceso administrativo se realiza con el login generado y la contraseña inicial:

```bash
curl -X POST http://localhost:8080/authenticate \
  -H "Content-Type: application/json" \
  -d '{
    "username": "AG123456789",
    "password": "123456789"
  }'
```

Después de autenticarse, el token JWT y la API Key del usuario administrador permiten crear nuevas personas, usuarios y gestionar las rutas protegidas del sistema.

### 6.3 Vehículos

- Tipos válidos:
  - `AUTOMOVIL`
  - `MOTOCICLETA`
- Las placas deben ser únicas y con formato válido.
- El vehículo debe tener al menos un documento asociado al crearse.
- Los documentos pueden estar en estados como:
  - `HABILITADO`
  - `VENCIDO`
  - `EN_VERIFICACION`

### 6.4 Documentos del vehículo

- Los documentos se asocian a un vehículo y a un documento base parametrizado.
- El archivo físico PDF se valida como Base64.
- El documento puede quedar en estados de vigencia o revisión.
- El sistema permite consultar vehículos con documentos vencidos o próximos a vencer.

### 6.5 Conductores asociados

- Solo personas con tipo `C` pueden ser conductores.
- Un vehículo no puede tener más de 5 conductores asociados.
- La asociación `vehiculo_persona` es única por vehículo + persona.
- El estado del conductor puede ser:
  - `PO` = Puede Operar
  - `EA` = Espera de Aprobación
  - `RO` = Restringido para Operar

### 6.6 Rutas y trayectos

- El sistema permite registrar trayectos asociados a un conductor y a un vehículo.
- Se pueden consultar rutas por código, por conductor y por placa.
- El módulo incluye la relación entre ruta, conductor, vehículo y paradas ordenadas.
- Se manejan consultas para detectar rutas con vehículo no habilitado o conductor restringido.
- La lógica de trayectos ya forma parte del comportamiento funcional actual del proyecto.

## 7. Seguridad de la API

La API implementa autenticación con JWT y validación adicional de API Key para rutas administrativas.

### 7.1 Autenticación

Endpoint de login:

- `POST /authenticate`

Body ejemplo:

```json
{
  "username": "AG123456789",
  "password": "123456789"
}
```

Respuesta esperada:

```json
{
  "jwtToken": "Bearer <jwt>"
}
```

### 7.2 Cabeceras requeridas para servicios protegidos

- `Authorization: Bearer <token>`
- `X-API-KEY: <apikey>`

El valor de `X-API-KEY` debe coincidir con el API Key del usuario autenticado.

## 8. Frontend y funcionalidades visibles en la aplicación

La aplicación frontend ya expone dos grupos principales de endpoints:

- Públicos: consultas sin autenticación.
- Privados: gestión administradora con autenticación.

### 8.1 Endpoints públicos

- `GET /persona/total-por-tipo`
- `GET /api/vehiculos/placa/:placa`
- `GET /api/vehiculos/estadoDocumento/:estadoDocumento`
- `GET /api/vehiculos/por-vencer?dias=30`
- `GET /api/vehiculos/vencidos`
- `GET /api/vehiculo-persona/conductores/pueden-operar`

Estas consultas permiten verificar rápidamente:

- total de personas por tipo,
- vehículos por placa,
- vehículos según estado documental,
- documentos próximos a vencer,
- vehículos con documentos vencidos,
- conductores habilitados para operar.

### 8.2 Endpoints privados

La capa privada cubre la gestión operativa administrativa, incluyendo:

#### Personas

- `GET /persona`
- `POST /persona`
- `PUT /persona/:id`

#### Usuarios

- `GET /usuario`
- `PUT /usuario/:login/password`
- `GET /usuario/:login/api-key/regenerar`

#### Vehículos

- `POST /api/vehiculos`
- `GET /api/vehiculos`
- `GET /api/vehiculos/:id`
- `GET /api/vehiculos/placa/:placa`
- `GET /api/vehiculos/tipo/:tipoVehiculo`
- `GET /api/vehiculos/tipoDocumento/:codigoDocumento`
- `POST /api/vehiculos/:vehiculoId/documentos`
- `PUT /api/vehiculos/:id`
- `DELETE /api/vehiculos/:id`

#### Documentos parametrizados

- `GET /api/documento`
- `POST /api/documento`
- `GET /api/documento/:id`
- `PUT /api/documento/:id`
- `DELETE /api/documento/:id`
- `GET /api/documento/tipoVehiculo?tipoVehiculo=A`

#### Asociación conductor-vehículo

- `POST /api/vehiculo-persona`
- `GET /api/vehiculo-persona`
- `GET /api/vehiculo-persona/vehiculo/:idVehiculo`
- `GET /api/vehiculo-persona/conductores/pueden-operar`
- `PUT /api/vehiculo-persona/:idAsociacion/estado?estado=PO`
- `DELETE /api/vehiculo-persona/:idAsociacion`

#### Rutas y trayectos

- `POST /api/trayectos`
- `GET /api/trayectos/ruta`
- `GET /api/trayectos/ruta/:codigoRuta`
- `GET /api/trayectos/trayectoPorConductorYVehiculo`
- `GET /api/trayectos/conductor/:documentoConductor`
- `GET /api/trayectos/CodigoRutaYConductorPorPlaca/:placa`

## 8. Endpoints principales de la API

### 8.1 Endpoints públicos (sin JWT)

#### Autenticación

- `POST /authenticate`

#### Personas

- `GET /persona/total-por-tipo`

#### Vehículos

- `GET /api/vehiculos/estadoDocumento/{estadoDocumento}`
- `GET /api/vehiculos/placa/{placa}`
- `GET /api/vehiculos/por-vencer?dias=30`
- `GET /api/vehiculos/vencidos`

#### Conductores

- `GET /api/vehiculo-persona/conductores/pueden-operar`

### 8.2 Endpoints protegidos (requieren JWT + API Key)

#### Personas

- `GET /persona`
- `POST /persona`
- `PUT /persona/{id}`

#### Usuarios

- `GET /usuario`
- `PUT /usuario/{login}/password`
- `GET /usuario/{login}/api-key/regenerar`

#### Vehículos

- `POST /api/vehiculos`
- `GET /api/vehiculos`
- `GET /api/vehiculos/{id}`
- `GET /api/vehiculos/tipo/{tipoVehiculo}`
- `GET /api/vehiculos/tipoDocumento/{codigoDocumento}`
- `POST /api/vehiculos/{vehiculoId}/documentos`
- `PUT /api/vehiculos/{id}`
- `DELETE /api/vehiculos/{id}`

#### Documentos

- `GET /api/documento`
- `POST /api/documento`
- `GET /api/documento/{id}`
- `PUT /api/documento/{id}`
- `DELETE /api/documento/{id}`
- `GET /api/documento/tipoVehiculo?tipoVehiculo=A`

#### Relaciones conductor-vehículo

- `POST /api/vehiculo-persona`
- `GET /api/vehiculo-persona`
- `GET /api/vehiculo-persona/vehiculo/{idVehiculo}`
- `PUT /api/vehiculo-persona/{idAsociacion}/estado?estado=PO`
- `DELETE /api/vehiculo-persona/{idAsociacion}`

#### Rutas y trayectos

- `POST /api/trayectos`
- `GET /api/trayectos/ruta`
- `GET /api/trayectos/ruta/{codigoRuta}`
- `GET /api/trayectos/trayectoPorConductorYVehiculo`
- `GET /api/trayectos/conductor/{documentoConductor}`
- `GET /api/trayectos/CodigoRutaYConductorPorPlaca/{placa}`

## 9. Ejecución local

### 9.1 Backend

Requisitos:

- Java 17
- Maven
- MySQL

Comandos:

```bash
mvn clean install
mvn spring-boot:run
```

La aplicación queda disponible en:

- `http://localhost:8080`

### 9.2 Frontend

```bash
cd frontend
npm install
npm run dev
```

La interfaz queda disponible en:

- `http://127.0.0.1:5500`

## 10. Ejemplos de payloads

### 10.1 Crear persona

```json
{
  "nombre": "Ana",
  "apellido": "García",
  "documento": "123456789",
  "tipoDocumento": "CC",
  "email": "ana@correo.com",
  "tipoPersona": "A"
}
```

### 10.2 Crear vehículo

```json
{
  "tipoVehiculo": "AUTOMOVIL",
  "tipoServicio": "PU",
  "tipoCombustible": "GASOLINA",
  "placa": "ABC123",
  "capacidadPasajeros": 4,
  "colorHexadecimal": "#FF0000",
  "modelo": 2024,
  "marca": "Toyota",
  "linea": "Corolla",
  "documentos": [
    {
      "documentoId": 1,
      "nombreArchivo": "soat.pdf",
      "archivoBase64": "JVBERi0xLjQK",
      "fechaExpedicion": "2024-01-01",
      "fechaVencimiento": "2025-01-01",
      "archivoPdfBase64": "JVBERi0xLjQK"
    }
  ]
}
```

### 10.3 Crear asociación conductor-vehículo

```json
{
  "vehiculoId": 1,
  "personaId": 2,
  "estadoConductor": "PO"
}
```

### 10.4 Registrar ruta o trayecto

```json
{
  "conductorId": 1,
  "vehiculoId": 1,
  "codigoRuta": "RUTA-UT",
  "ubicacion": "Universidad",
  "ordenParada": 1,
  "latitud": 4.601,
  "longitud": -74.066
}
```