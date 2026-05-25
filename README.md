```markdown
# E-Commerce API - Sistema de Ordenes e Inventario con Patron Saga

Bienvenido al backend del sistema de E-Commerce. Este proyecto es una API REST profesional construida con **Java 21** y **Spring Boot 3**, disenada para gestionar productos y ventas asegurando la integridad del inventario mediante control de concurrencia. Para el Taller 2, el sistema evoluciono hacia una **arquitectura orientada a eventos** con el **patron Saga** y un microservicio de pagos independiente (Opcion B).

### En los adjuntos del proyecto encontraran el pdf con mayor informacion, para la tarea.

---

## Requisitos Previos
* **Java 21** o superior.
* **Docker Desktop** (Obligatorio para la base de datos, RabbitMQ y Grafana).
* **IDE** (Spring Tool Suite 4, IntelliJ IDEA o Eclipse).

---

## Guia de Inicio Rapido

### 1. Clonar los Repositorios
```bash
# Repositorio principal (Ecommerce Service)
git clone https://github.com/Nathaly-Daza/API-REST--CONCURRENCIA---SEGURIDAD.git
cd API-REST--CONCURRENCIA---SEGURIDAD

# Microservicio de Pagos (en carpeta separada)
git clone https://github.com/Nathaly-Daza/microservicio-de-Pagos-integrado.git
```

### 2. Ejecutar Infraestructura (Docker)
Ejecuta el siguiente comando para levantar PostgreSQL, RabbitMQ y Grafana LGTM:

```bash
docker-compose up -d
```

### 3. Configuracion del Entorno
Verifica la configuracion en el archivo `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/ecommerce
spring.datasource.username=admin
spring.datasource.password=admin
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# RabbitMQ
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest

# Configuracion de Eventos
app.rabbitmq.exchange=order.exchange
app.rabbitmq.routing-key.order-created=order.created
app.rabbitmq.routing-key.payment-result=payment.result
app.rabbitmq.queue.payment-result=ecommerce.payment.result.queue
```

### 4. Ejecutar la Aplicacion

**Ecommerce Service (Puerto 8080):**
1. Haz clic derecho sobre **EcommerceApplication.java**.
2. Selecciona **Run As > Run Configurations**.
3. En la pestana **Arguments > VM arguments**, pega:
```
-javaagent:opentelemetry-javaagent.jar -Dotel.service.name=ecommerce-service -Dotel.traces.exporter=otlp -Dotel.metrics.exporter=otlp -Dotel.logs.exporter=otlp -Dotel.exporter.otlp.protocol=http/protobuf -Dotel.exporter.otlp.endpoint=http://localhost:4318
```
4. Haz clic en **Run**.

**Payment Service (Puerto 8081):**
Ejecuta el proyecto `microservicio-de-Pagos-integrado` con los mismos VM arguments pero cambiando el nombre del servicio:
```
-javaagent:opentelemetry-javaagent.jar -Dotel.service.name=payment-service -Dotel.traces.exporter=otlp -Dotel.metrics.exporter=otlp -Dotel.logs.exporter=otlp -Dotel.exporter.otlp.protocol=http/protobuf -Dotel.exporter.otlp.endpoint=http://localhost:4318
```

---

## Servicios Disponibles

| Servicio | URL |
|----------|-----|
| API Ecommerce | http://localhost:8080 |
| Swagger UI | http://localhost:8080/swagger-ui/index.html |
| RabbitMQ UI | http://localhost:15672 (guest/guest) |
| Grafana | http://localhost:3000 |

---

## Documentacion y Pruebas (Swagger)

Pueden probar todos los endpoints sin necesidad de herramientas externas a traves de **Swagger UI**:

**URL:** [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

### Flujo de Uso:
1. **Login:** Obten tu Token JWT en el endpoint `/auth/login`.
2. **Autorizar:** Haz clic en el boton **"Authorize"**, escribe `Bearer` seguido de un espacio y tu token.
3. **Operar:** Accede a las funciones de productos y ordenes.

---

## Credenciales de Prueba
El sistema inicializa automaticamente la siguiente cuenta:
* **Usuario:** `nathaly`
* **Contrasena:** `12345`
* **Rol:** `ADMIN`

---

## Arquitectura del Sistema (Taller 2 - Opcion B)

```
CLIENTE (Postman/Swagger)
       |
       | HTTP REST
       v
ECOMMERCE SERVICE (Puerto 8080)
  - Usuarios + JWT + Roles
  - Productos + Inventario + Soft Delete
  - Ordenes (PENDING -> CONFIRMED / COMPENSATED)
  - Auditoria
       |
       | Eventos asincronos (RabbitMQ)
       v
RABBITMQ (Puerto 5672)
  Exchange: order.exchange
  Colas: payment.queue, ecommerce.payment.result.queue
       |
       | Eventos
       v
PAYMENT SERVICE (Puerto 8081)
  - Sin API REST (solo eventos)
  - Simula pagos (70% exito, 30% fallo)
  - Publica PaymentApproved o PaymentFailed
       |
       | OTLP (Trazas, Metricas, Logs)
       v
GRAFANA LGTM (Puerto 3000)
  Loki + Grafana + Tempo + Mimir
```

---

## Caracteristicas Tecnicas

### Taller 1
* **Control de Concurrencia Optimista:** Uso de `@Version` para evitar la sobreventa en pedidos simultaneos.
* **Seguridad:** Implementacion de **JWT (JSON Web Token)** y roles de usuario (ADMIN/CLIENTE).
* **Auditoria:** Registro automatico de eventos relevantes en la tabla `audit_log`.
* **Soft Delete:** Eliminacion logica de productos para mantener la integridad historica.
* **Arquitectura:** Diseno basado en capas (Controller, Service, Repository).

### Taller 2
* **Patron Saga:** Flujo de eventos con compensacion. Orden creada en estado PENDING, confirmada o compensada segun resultado del pago.
* **Eventos Asincronos:** Comunicacion entre Ecommerce y Payment mediante RabbitMQ. Sin HTTP directo entre servicios.
* **Observabilidad:** Trazas distribuidas con OpenTelemetry. Stack Grafana LGTM (Loki, Tempo, Mimir) desplegado con Docker.
* **Consistencia Eventual:** Si el pago falla, se publica evento de compensacion que libera el stock reservado.

---

## Flujo Saga

### Happy Path (Todo sale bien)
1. Cliente crea orden via `POST /orders`
2. Ecommerce descuenta stock y guarda orden con estado **PENDING**
3. Ecommerce publica evento `OrderCreated` en RabbitMQ
4. Payment Service recibe el evento y simula el pago
5. Si el pago es exitoso, publica `PaymentApproved`
6. Ecommerce recibe `PaymentApproved` y confirma la orden (**CONFIRMED**)

### Flujo de Compensacion (Pago falla)
1. Payment Service detecta fallo en el pago
2. Publica evento `PaymentFailed` con el motivo del rechazo
3. Ecommerce recibe `PaymentFailed`
4. Libera el stock reservado (compensacion)
5. Orden pasa a estado **COMPENSATED**

---

## Evidencia de Concurrencia
Para validar la regla de negocio central, puedes ejecutar el test de estres incluido:
`GET /test/concurrency?productId=1&username=nathaly`
<img width="1528" height="844" alt="image" src="https://github.com/user-attachments/assets/025e6071-ca1f-4e36-a851-1f5b77c2f986" />
<img width="1123" height="176" alt="image" src="https://github.com/user-attachments/assets/a144234c-fd74-4f21-a409-2a6eb171b1b2" />

Este endpoint simula multiples compras simultaneas. El exito de la prueba se evidencia en la consola del IDE cuando el sistema bloquea transacciones que intentarian dejar el stock en negativo.

---

## Observabilidad (Grafana)

Accede a Grafana en `http://localhost:3000`:
1. Menu lateral > **Explore**
2. Seleccionar datasource **Tempo**
3. Buscar trazas de `ecommerce-service` o `payment-service`
4. Se visualiza el flujo completo de cada solicitud entre microservicios

---

## DIAGRAMA DE ARQUITECTURA (CAPAS)
<img width="1408" height="768" alt="Gemini_Generated_Image_wn8g78wn8g78wn8g" src="https://github.com/user-attachments/assets/9c59a466-7869-44c0-b163-1f6c4361827a" />

El sistema esta construido bajo una arquitectura en capas donde cada componente tiene una responsabilidad especifica.

El cliente (Swagger o frontend) envia peticiones HTTP que son interceptadas por Spring Security, donde se valida el token JWT y se verifica el rol del usuario.

Luego, las solicitudes llegan a los controladores, que reciben y validan los datos de entrada. Los controladores delegan la logica de negocio a los servicios, donde se implementan reglas como validacion de stock y creacion de ordenes.

Los servicios interactuan con los repositorios, los cuales se encargan del acceso a la base de datos mediante JPA/Hibernate.

Para el flujo de pagos, el servicio de ordenes publica eventos en RabbitMQ que son consumidos por el Payment Service, el cual responde con eventos de exito o fallo, implementando el patron Saga.

Finalmente, la informacion se almacena en PostgreSQL, ejecutandose dentro de un contenedor Docker. OpenTelemetry exporta trazas, metricas y logs a Grafana LGTM para observabilidad distribuida.
```
