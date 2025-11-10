#!/bin/bash

# Función para hacer una solicitud POST y extraer el ID de la orden
create_order() {
  local data=$1
  echo "Creando orden con los siguientes datos: $data" >&2
  response=$(curl -s -X POST http://localhost:8081/orders/ \
    -H "Content-Type: application/json" \
    -d "$data")
  echo "Respuesta de la creación de la orden: $response" >&2
  echo "$response" | jq -r '.id'
}

# Función para verificar el estado de una orden con reintentos
check_order_status() {
  local order_id=$1
  echo "Verificando el estado de la orden con ID: $order_id (reintentando si es necesario)..." >&2
  for i in {1..5}; do
    # Usamos -f para que curl falle si hay un error HTTP (ej. 404)
    # Usamos -s para mantener la salida limpia
    response=$(curl -fs http://localhost:8081/orders/$order_id)
    if [ -n "$response" ]; then
      echo "$response"
      return
    fi
    # No es necesario mostrar este mensaje cada vez, para mantener la salida limpia.
    # echo "Intento $i: La orden aún no está lista. Esperando 1 segundo..." >&2
    sleep 1
  done
  echo "Error: No se pudo obtener el estado de la orden $order_id después de 5 intentos." >&2
}

echo "======================================================"
echo "Prueba 1: Flujo exitoso"
echo "======================================================"
order_id_1=$(create_order '{"productId": "prod-1", "quantity": 2}')

if [ -z "$order_id_1" ] || [ "$order_id_1" == "null" ]; then
  echo "Error: No se pudo crear la orden para el flujo exitoso."
else
  echo "Orden creada con ID: $order_id_1."
  echo "Estado final de la orden:"
  final_status_json=$(check_order_status $order_id_1)
  if [ -n "$final_status_json" ]; then
    echo "$final_status_json" | jq .
    echo "Se espera que el estado sea COMPLETED."
  fi
fi

echo ""
echo "======================================================"
echo "Prueba 2: Fallo de inventario"
echo "======================================================"
order_id_2=$(create_order '{"productId": "prod-2", "quantity": 51}')

if [ -z "$order_id_2" ] || [ "$order_id_2" == "null" ]; then
  echo "Error: No se pudo crear la orden para el fallo de inventario."
else
  echo "Orden creada con ID: $order_id_2."
  echo "Estado final de la orden:"
  final_status_json=$(check_order_status $order_id_2)
  if [ -n "$final_status_json" ]; then
    echo "$final_status_json" | jq .
    echo "Se espera que el estado sea REJECTED."
  fi
fi

echo ""
echo "======================================================"
echo "Prueba 3: Fallo de pago"
echo "======================================================"
order_id_3=$(create_order '{"productId": "prod-3", "quantity": 31}')

if [ -z "$order_id_3" ] || [ "$order_id_3" == "null" ]; then
  echo "Error: No se pudo crear la orden para el fallo de pago."
else
  echo "Orden creada con ID: $order_id_3."
  echo "Estado final de la orden:"
  final_status_json=$(check_order_status $order_id_3)
  if [ -n "$final_status_json" ]; then
    echo "$final_status_json" | jq .
    echo "Se espera que el estado sea CANCELLED."
  fi
fi

echo ""
echo "======================================================"
echo "Todas las pruebas han finalizado."
echo "======================================================"
