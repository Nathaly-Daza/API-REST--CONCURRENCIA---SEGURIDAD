# E-Commerce API - Sistema de Órdenes e Inventario

Bienvenido al backend del sistema de E-Commerce. Este proyecto es una API REST profesional construida con **Java 21** y **Spring Boot 3**, diseñada para gestionar productos y ventas asegurando la integridad del inventario mediante control de concurrencia.
 ### En los adjuntos del proyecto encontraran el pdf con mayor información, para la tarea.
---

## Requisitos Previos
* **Java 21** o superior.
* **Docker Desktop** (Obligatorio para la base de datos).
* **IDE** (Spring Tool Suite 4, IntelliJ IDEA o Eclipse).

---

## Guía de Inicio Rápido

### 1. Clonar el Proyecto
```bash
git clone https://github.com
cd API-REST--CONCURRENCIA---SEGURIDAD
```

### 2. Ejecutar Base de Datos (Docker)
Ejecuta el siguiente comando para levantar el contenedor de PostgreSQL:
```bash
docker run -d \
  --name ecommerce_db \
  -p 5432:5432 \
  -e POSTGRES_DB=ecommerce \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=1234 \
  postgres
```

### 3. Configuración del Entorno
Verifica la configuración en el archivo `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/ecommerce
spring.datasource.username=postgres
spring.datasource.password=1234
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

### 4. Ejecutar la Aplicación
1. Haz clic derecho sobre **EcommerceApplication.java**.
2. Selecciona **Run As > Spring Boot App**.
3. La API estará disponible en `http://localhost:8080`.

---

## Documentación y Pruebas (Swagger)

Pueden probar todos los endpoints sin necesidad de herramientas externas a través de **Swagger UI**:

**URL:** [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

### Flujo de Uso:
1. **Login:** Obtén tu Token JWT en el endpoint `/auth/login`.
2. **Autorizar:** Haz clic en el botón **"Authorize"**, escribe `Bearer` seguido de un espacio y tu token.
3. **Operar:** Accede a las funciones de productos y órdenes.

---

## Credenciales de Prueba
El sistema inicializa automáticamente la siguiente cuenta:
* **Usuario:** `nathaly`
* **Contraseña:** `12345`
* **Rol:** `ADMIN`

---

## Características Técnicas
* **Control de Concurrencia Optimista:** Uso de `@Version` para evitar la sobreventa en pedidos simultáneos.
* **Seguridad:** Implementación de **JWT (JSON Web Token)** y roles de usuario.
* **Auditoría:** Registro automático de eventos relevantes en el sistema.
* **Soft Delete:** Eliminación lógica de productos para mantener la integridad histórica.
* **Arquitectura:** Diseño basado en capas (Controller, Service, Repository).

---

## Evidencia de Concurrencia
Para validar la regla de negocio central, puedes ejecutar el test de estrés incluido:
`GET /test/concurrency?productId=1&username=nathaly`
<img width="1528" height="844" alt="image" src="https://github.com/user-attachments/assets/025e6071-ca1f-4e36-a851-1f5b77c2f986" />
<img width="1123" height="176" alt="image" src="https://github.com/user-attachments/assets/a144234c-fd74-4f21-a409-2a6eb171b1b2" />

Este endpoint simula múltiples compras simultáneas. El éxito de la prueba se evidencia en la consola del IDE cuando el sistema bloquea transacciones que intentarían dejar el stock en negativo.

## DIAGRAMA DE ARQUITECTURA (CAPAS)
<img width="1408" height="768" alt="Gemini_Generated_Image_wn8g78wn8g78wn8g" src="https://github.com/user-attachments/assets/9c59a466-7869-44c0-b163-1f6c4361827a" />

El sistema está construido bajo una arquitectura en capas donde cada componente tiene una responsabilidad específica.

El cliente (Swagger o frontend) envía peticiones HTTP que son interceptadas por Spring Security, donde se valida el token JWT y se verifica el rol del usuario.

Luego, las solicitudes llegan a los controladores, que reciben y validan los datos de entrada.
Los controladores delegan la lógica de negocio a los servicios, donde se implementan reglas como validación de stock y creación de órdenes.

Los servicios interactúan con los repositorios, los cuales se encargan del acceso a la base de datos mediante JPA/Hibernate.

Finalmente, la información se almacena en PostgreSQL, ejecutándose dentro de un contenedor Docker.


