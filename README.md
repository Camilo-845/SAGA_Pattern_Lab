# SAGA Patterm Lab

Implemetación del patron SAGA con RabbitMQ y Spring Boot (java 21), siguiendo guía [LabGuide](./LabGuide.md)

## Flujo de eventos y comandos

![img.png](img.png)

## Instalación y Uso

Clonar repositorio

```sh
git clone https://github.com/Camilo-845/SAGA_Pattern_Lab.git
```

Desde el directorio del proyecto

Ejecutar imagen de RabbitMQ

```sh
docker compose up -d
```

Hacer build de los servicios

```sh
# Order Service
./order-service/mvnw clean package --DskipTest
# Inventory Service
./inventory-service/mvnw clean package --DskipTest
# Payment Service
./payment-service/mvnw clean package --DskipTest
```

Ejecutar cada uno de los servicios

```sh
# Ejecución en sugundo plano, quitar el "&" si se quiere ejecutarlo en el mismo proceso
java -jar ./order-service/target/order-service-0.0.1-SNAPSHOT.jar &
java -jar ./inventory-service/target/inventory-service-0.0.1-SNAPSHOT.jar &
java -jar ./payment-service/target/payment-service-0.0.1-SNAPSHOT.jar &
```

## Test

Run test

```sh
./test.sh
```

### Test Results

```sh

======================================================
Prueba 1: Flujo exitoso
======================================================
Creando orden con los siguientes datos: {"productId": "prod-1", "quantity": 2}
Respuesta de la creación de la orden: {"id":"892995eb-5fc3-447f-b51e-38baf621c883","productId":"prod-1","quantity":2,"totalAmount":null,"status":"CREATED","createdAt":"2025-11-10T00:24:48.239+00:00"}
Orden creada con ID: 892995eb-5fc3-447f-b51e-38baf621c883.
Estado final de la orden:
Verificando el estado de la orden con ID: 892995eb-5fc3-447f-b51e-38baf621c883 (reintentando si es necesario)...
{
  "id": "892995eb-5fc3-447f-b51e-38baf621c883",
  "productId": "prod-1",
  "quantity": 2,
  "totalAmount": 59.98,
  "status": "COMPLETED",
  "createdAt": "2025-11-10T00:24:48.253+00:00"
}
Se espera que el estado sea COMPLETED.

======================================================
Prueba 2: Fallo de inventario
======================================================
Creando orden con los siguientes datos: {"productId": "prod-2", "quantity": 51}
Respuesta de la creación de la orden: {"id":"b7c543b2-1325-4b53-9f16-9b1b74878535","productId":"prod-2","quantity":51,"totalAmount":null,"status":"CREATED","createdAt":"2025-11-10T00:24:48.289+00:00"}
Orden creada con ID: b7c543b2-1325-4b53-9f16-9b1b74878535.
Estado final de la orden:
Verificando el estado de la orden con ID: b7c543b2-1325-4b53-9f16-9b1b74878535 (reintentando si es necesario)...
{
  "id": "b7c543b2-1325-4b53-9f16-9b1b74878535",
  "productId": "prod-2",
  "quantity": 51,
  "totalAmount": null,
  "status": "REJECTED",
  "createdAt": "2025-11-10T00:24:48.295+00:00"
}
Se espera que el estado sea REJECTED.

======================================================
Prueba 3: Fallo de pago
======================================================
Creando orden con los siguientes datos: {"productId": "prod-3", "quantity": 31}
Respuesta de la creación de la orden: {"id":"0ef24b40-ea72-4b91-8061-929d8cc6fdcc","productId":"prod-3","quantity":31,"totalAmount":null,"status":"CREATED","createdAt":"2025-11-10T00:24:48.336+00:00"}
Orden creada con ID: 0ef24b40-ea72-4b91-8061-929d8cc6fdcc.
Estado final de la orden:
Verificando el estado de la orden con ID: 0ef24b40-ea72-4b91-8061-929d8cc6fdcc (reintentando si es necesario)...
{
  "id": "0ef24b40-ea72-4b91-8061-929d8cc6fdcc",
  "productId": "prod-3",
  "quantity": 31,
  "totalAmount": 619.69,
  "status": "CANCELLED",
  "createdAt": "2025-11-10T00:24:48.348+00:00"
}
Se espera que el estado sea CANCELLED.

======================================================
Todas las pruebas han finalizado.
======================================================
```
