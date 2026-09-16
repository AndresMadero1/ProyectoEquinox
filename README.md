# GamerRooms

Aplicacion Android nativa para que usuarios gamer puedan descubrir grupos, unirse a salas y conversar en chats tematicos.

## Estado inicial

- Android nativo con Kotlin y Jetpack Compose.
- Pantalla principal con busqueda de grupos.
- Listado de comunidades gamer por juego, plataforma y actividad.
- Vista de sala de chat con mensajes de ejemplo.
- Repositorio Git inicializado.

## Vision del producto

GamerRooms busca resolver un problema simple: encontrar rapido con quien jugar. La primera version se enfoca en descubrir grupos por juego o plataforma, entrar a una sala y conversar con otros jugadores.

## AWS con bajo costo

Para una primera version, la arquitectura recomendada es serverless:

- Amazon Cognito para registro e inicio de sesion.
- Amazon API Gateway para exponer endpoints HTTPS.
- AWS Lambda para logica de negocio.
- Amazon DynamoDB para usuarios, grupos, salas y mensajes.
- API Gateway WebSocket o AWS AppSync para chat en tiempo real.
- Amazon S3 para avatares, banners e imagenes de grupo.
- Amazon CloudWatch con retencion corta de logs para controlar costos.

Esta ruta evita servidores siempre encendidos y permite pagar principalmente por uso. Para un MVP, tambien se puede empezar con mensajes via HTTP polling y migrar a WebSocket cuando haya usuarios activos.

## Modelo de datos sugerido

- `users`: perfil, gamertag, avatar, plataformas.
- `groups`: nombre, juego, plataforma, region, etiquetas, miembros.
- `rooms`: grupo asociado, nombre de sala, tipo de sala.
- `messages`: sala, autor, texto, fecha.
- `memberships`: usuario, grupo, rol, fecha de union.

## Como abrir el proyecto

1. Abrir esta carpeta en Android Studio.
2. Dejar que Android Studio sincronice Gradle.
3. Ejecutar la app en un emulador o dispositivo Android.

## Siguientes pasos recomendados

1. Agregar autenticacion con Cognito.
2. Conectar busqueda de grupos a DynamoDB mediante API Gateway y Lambda.
3. Implementar union real a grupos.
4. Activar chat en tiempo real con WebSocket/AppSync.
5. Agregar moderacion basica: reportes, bloqueo de usuario y roles.

