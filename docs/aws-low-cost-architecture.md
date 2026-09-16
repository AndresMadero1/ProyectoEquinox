# Arquitectura AWS de bajo costo

## Objetivo

Mantener el costo mensual lo mas bajo posible durante el MVP, evitando servidores dedicados y priorizando servicios con capa gratuita o cobro por uso.

## Propuesta MVP

```text
Android App
    |
    v
Amazon Cognito
    |
    v
API Gateway HTTP API ---- Lambda ---- DynamoDB
    |
    v
API Gateway WebSocket -- Lambda ---- DynamoDB Streams
```

## Servicios

| Necesidad | Servicio | Motivo |
| --- | --- | --- |
| Login y usuarios | Cognito | Evita construir autenticacion propia. |
| API de grupos | API Gateway HTTP API + Lambda | Bajo costo y sin servidores. |
| Chat | API Gateway WebSocket + Lambda | Tiempo real sin mantener instancias. |
| Base de datos | DynamoDB on-demand | Escala por uso y requiere poca operacion. |
| Imagenes | S3 | Barato para avatares y banners. |
| Logs | CloudWatch | Necesario, pero con retencion limitada. |

## Estrategia para gastar poco

- Usar DynamoDB on-demand al inicio.
- Limitar logs de CloudWatch a 7 o 14 dias.
- Evitar NAT Gateway en el MVP.
- No usar EC2, ECS ni RDS al principio.
- Comprimir imagenes antes de subirlas a S3.
- Usar presupuestos y alertas de AWS Budgets desde el dia uno.

## Evolucion

Cuando el producto tenga traccion:

- Migrar chat de WebSocket API a AppSync si se necesitan subscriptions GraphQL.
- Agregar OpenSearch solo si la busqueda de grupos supera filtros simples.
- Introducir ElastiCache solo si hay problemas reales de latencia o costo en lecturas repetidas.

