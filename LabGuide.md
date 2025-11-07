# Guía de Laboratorio

## Implementación del Patrón Saga con RabbitMQ y Spring Boot (Java 21)

### 1. Información general

**Asignatura:** Desarrollo de Microservicios

**Tecnologías:**

- Java 21
- Spring Boot 3.x
- Spring Cloud
- RabbitMQ
- Maven
- Docker / Docker Compose

### 2. Contexto del problema

Una tienda en línea desea implementar un flujo robusto para la creación de órdenes de compra.

Cuando un cliente crea una orden, el sistema debe:

1.  Verificar y **reservar inventario**.
2.  **Procesar el pago**.
3.  Confirmar o cancelar la orden dependiendo del resultado de los pasos anteriores.

Dado que cada una de estas responsabilidades estará en **microservicios diferentes**, no es posible usar una transacción distribuida tradicional. En su lugar, se debe implementar una **transacción distribuida basada en el patrón Saga**, utilizando **RabbitMQ** como broker de mensajes.

La empresa desea que el sistema:

- Sea **asíncrono**, desacoplado y escalable.
- Implemente **acciones compensatorias** cuando algo falla (por ejemplo, liberar inventario cuando falla el pago).
- Permita rastrear el estado de la orden durante todo el flujo.

### 3. Objetivos de aprendizaje

Al finalizar el laboratorio, el estudiante será capaz de:

1.  Explicar el **patrón Saga por orquestación** y su aplicación en microservicios.
2.  Diseñar un flujo de **eventos y comandos** para una transacción distribuida.
3.  Configurar y utilizar **RabbitMQ** con Spring Boot (productor y consumidor).
4.  Implementar un **orquestador de Saga** dentro de un microservicio.
5.  Implementar **acciones compensatorias** para manejar fallos.
6.  Validar el comportamiento del sistema mediante pruebas funcionales básicas (con herramientas como curl, Postman, o similar).

### 4. Arquitectura a implementar

En este laboratorio se trabajará con al menos **3 microservicios**:

1.  **order-service (Orquestador de la Saga)**

    - Expone un endpoint REST para crear órdenes:
      - `POST /orders`
    - Al recibir una nueva orden:
      - Persiste la orden en base de datos con estado inicial `CREATED`.
      - Inicia la Saga enviando un **comando** a `inventory-service` para reservar inventario.
    - Escucha los **eventos** provenientes de `inventory-service` y `payment-service`.
    - Actualiza el estado de la orden según el progreso de la Saga:
      - `CREATED`, `PENDING_PAYMENT`, `COMPLETED`, `CANCELLED`, `REJECTED`.
    - Emite eventos finales:
      - `OrderCompletedEvent`, `OrderCancelledEvent`.

2.  **inventory-service**

    - Gestiona el inventario de productos.
    - Escucha el comando:
      - `ReserveInventoryCommand`
    - Si hay stock suficiente:
      - Reserva el inventario.
      - Emite `InventoryReservedEvent`.
    - Si no hay stock suficiente:
      - Emite `InventoryRejectedEvent`.
    - Escucha el comando de compensación:
      - `ReleaseInventoryCommand` (para liberar reservas si falla el pago).

3.  **payment-service**
    - Gestiona el procesamiento de pagos (simulado).
    - Escucha el comando:
      - `ProcessPaymentCommand`
    - Si el pago es exitoso:
      - Emite `PaymentCompletedEvent`.
    - Si el pago falla:
      - Emite `PaymentFailedEvent`.

### 5. Flujo de la Saga

#### 5.1. Caso exitoso

1.  El cliente llama a `POST /orders` en `order-service`.
2.  `order-service` crea una orden con estado `CREATED`.
3.  `order-service` envía un **comando** `ReserveInventoryCommand` a través de RabbitMQ.
4.  `inventory-service` recibe el comando, valida stock, reserva inventario y emite `InventoryReservedEvent`.
5.  `order-service` escucha `InventoryReservedEvent`, actualiza la orden a `PENDING_PAYMENT` y envía `ProcessPaymentCommand`.
6.  `payment-service` procesa el pago (simulado) y emite `PaymentCompletedEvent`.
7.  `order-service` escucha `PaymentCompletedEvent`, cambia el estado de la orden a `COMPLETED` y emite `OrderCompletedEvent`.

#### 5.2. Caso de fallo en inventario

1.  Pasos 1-3 iguales al caso exitoso.
2.  `inventory-service` detecta que **no hay stock suficiente** y emite `InventoryRejectedEvent`.
3.  `order-service` escucha `InventoryRejectedEvent`, cambia el estado de la orden a `REJECTED` y finaliza la Saga (no se intenta el pago).

#### 5.3. Caso de fallo en pago (requiere compensación)

1.  Pasos 1-5 iguales al caso exitoso.
2.  `payment-service` simula un **fallo de pago** (por ejemplo, según un valor del monto o de la tarjeta) y emite `PaymentFailedEvent`.
3.  `order-service` escucha `PaymentFailedEvent`, cambia el estado a `CANCELLED` y envía el comando `ReleaseInventoryCommand` a `inventory-service`.
4.  `inventory-service` libera la reserva de inventario. (Opcional: emite `InventoryReleasedEvent` para logging).
5.  La Saga finaliza, dejando la orden en estado `CANCELLED`.

### 6. Modelo de datos mínimo

Puedes adaptar la base de datos (PostgreSQL, etc.). Modelo sugerido:

**En order-service:**

Entidad `Order` (ejemplo simplificado):

- `id` (UUID)
- `productId` (String)
- `quantity` (int)
- `totalAmount` (BigDecimal) – opcional (puede venir del inventario)
- `status` (enum: `CREATED`, `PENDING_PAYMENT`, `COMPLETED`, `CANCELLED`, `REJECTED`)
- `createdAt` (timestamp)

**En inventory-service:**

Entidad `InventoryItem`:

- `id` (UUID)
- `productId` (String)
- `availableQuantity` (int)
- `price` (BigDecimal) – para calcular el monto total.

**En payment-service:**

Entidad `Payment` (opcional, para registro):

- `id` (UUID)
- `orderId` (UUID/String)
- `amount` (BigDecimal)
- `status` (SUCCESS, FAILED)
- `createdAt` (timestamp)

### 7. Mensajes a definir (Comandos y Eventos)

El estudiante debe definir estos mensajes como Java records o clases simples.

#### 7.1. Comandos

- `ReserveInventoryCommand`
- `ReleaseInventoryCommand`
- `ProcessPaymentCommand`

Ejemplo:

```java
public record ReserveInventoryCommand(
    String orderId,
    String productId,
    int quantity
) {}
```

```java
public record ProcessPaymentCommand(
    String orderId,
    java.math.BigDecimal amount
) {}
```

#### 7.2. Eventos

- `InventoryReservedEvent`
- `InventoryRejectedEvent`
- `PaymentCompletedEvent`
- `PaymentFailedEvent`
- `OrderCompletedEvent`
- `OrderCancelledEvent`

Ejemplo:

```java
public record InventoryReservedEvent(
    String orderId,
    String productId,
    int quantity,
    java.math.BigDecimal totalAmount
) {}
```

### 8. Requerimientos técnicos

#### 8.1. Configuración de RabbitMQ (Docker Compose)

Los estudiantes deben levantar RabbitMQ localmente. Proporcione el siguiente `docker-compose.yml`:

```yaml
version: "3.8"
services:
  rabbitmq:
    image: rabbitmq:3-management
    ports:
      - "5672:5672"
      - "15672:15672"
    environment:
      RABBITMQ_DEFAULT_USER: admin
      RABBITMQ_DEFAULT_PASS: admin
```

#### 8.2. Dependencias básicas (en cada microservicio)

En el `pom.xml` (fragmento orientativo):

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-amqp</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>

    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <scope>runtime</scope>
    </dependency>

    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
    <!-- Otros según necesidad -->
</dependencies>
```

#### 8.3. Configuración mínima de RabbitMQ en `application.yml`

Ejemplo para `order-service`:

```yaml
spring:
  application:
    name: order-service
rabbitmq:
  host: localhost
  port: 5672
  username: admin
  password: admin
server:
  port: 8081
```

Adaptar el port para cada microservicio.

### Actividad 1: Configuración de proyectos

1.  Crear 3 proyectos Spring Boot (Maven) con Java 21:
    - `order-service`
    - `inventory-service`
    - `payment-service`
2.  Configurar:
    - Paquetes base (por ejemplo: `com.ecommerce.order`, `com.ecommerce.inventory`, etc.).
    - `application.yml` con configuración de RabbitMQ.
3.  Levantar RabbitMQ con Docker y verificar acceso al panel de administración.

**Entregable:** Proyectos iniciales configurados, repositorios creados.

### Actividad 2: Implementación de la Saga – Parte 1 (Inventario)

1.  Implementar en `order-service`:
    - Endpoint `POST /orders` que:
      - Reciba un `CreateOrderRequest` (productId, quantity).
      - Cree una entidad `Order` con estado `CREATED`.
      - Envíe un `ReserveInventoryCommand` mediante RabbitMQ.
2.  Implementar en `inventory-service`:
    - Una cola y listener que reciba `ReserveInventoryCommand`.
    - Lógica que:
      - Verifique si hay cantidad suficiente.
      - Si hay stock → actualiza inventario, emite `InventoryReservedEvent`.
      - Si no hay stock → emite `InventoryRejectedEvent`.
3.  Implementar en `order-service`:
    - Listeners para:
      - `InventoryReservedEvent` → cambiar estado a `PENDING_PAYMENT` y enviar `ProcessPaymentCommand`.
      - `InventoryRejectedEvent` → cambiar estado a `REJECTED`.

**Entregable:** Flujo completo entre `order-service` e `inventory-service` funcionando.

### Actividad 3: Implementación de la Saga – Parte 2 (Pago y compensación)

1.  Implementar en `payment-service`:
    - Listener para `ProcessPaymentCommand`.
    - Lógica que simule:
      - Éxito de pago bajo ciertas condiciones.
      - Fallo de pago (por ejemplo, si el monto es mayor a cierto valor).
    - Emisión de eventos:
      - `PaymentCompletedEvent`.
      - `PaymentFailedEvent`.
2.  Implementar en `order-service`:
    - Listener para `PaymentCompletedEvent`:
      - Cambiar estado a `COMPLETED`.
      - Emitir `OrderCompletedEvent`.
    - Listener para `PaymentFailedEvent`:
      - Cambiar estado a `CANCELLED`.
      - Enviar `ReleaseInventoryCommand` al `inventory-service`.
3.  Implementar en `inventory-service`:
    - Listener para `ReleaseInventoryCommand`:
      - Liberar el inventario previamente reservado.

**Entregable:** Saga completa funcionando, incluyendo el mecanismo de compensación.

### Actividad 4: Pruebas y validación

1.  Probar el flujo exitoso:
    - Crear orden con cantidad y condiciones para que:
      - Haya inventario suficiente.
      - El pago se realice con éxito.
    - Verificar:
      - Estado final de la orden = `COMPLETED`.
      - Cambios en inventario.
      - Logs de eventos.
2.  Probar el flujo de **fallo de inventario**:
    - Crear orden con cantidad superior a la disponible.
    - Verificar:
      - Estado final de la orden = `REJECTED`.
3.  Probar el flujo de **fallo de pago**:
    - Configurar escenario para que falle el pago.
    - Verificar:
      - Estado final de la orden = `CANCELLED`.
      - Inventario liberado correctamente.

