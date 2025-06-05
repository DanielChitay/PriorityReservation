# 📋 Sistema de Reservas con Prioridad (PriorityReservation)
Este proyecto es un backend Spring Boot para un sistema de gestión de tareas que incluye un módulo completo de historial de cambios, implementando mejores prácticas de desarrollo y manejo de errores.

## 🚀 Tecnologías Principales
* Backend: Spring Boot -  (Java 17+)

* Base de datos: MySQL 8.0 (Dockerizado)

* Mensajería: RabbitMQ (Eventos asíncronos)

- Persistencia: Spring Data JPA + Hibernate

* Validación: Bean Validation 3.0

* Contenedores: Docker + Docker Compose

## Configuración del Proyecto

### Requisitos
* Java 17+

* Docker (para MySQL y RabbitMQ)

* Maven 3.8+

### Instalación
* Clonar el repositorio

* Configurar las variables de entorno (ver .env.example)

* Ejecutar servicios con Docker:
  -  docker-compose up -d

## Flujo de Trabajo del Historial de Cambios
### Modificación de Tarea:

* Usuario realiza cambio vía API

* Sistema captura estado anterior

* Valida cambios significativos

* Registra en historial

* Envía evento a RabbitMQ para notificaciones

### Procesamiento Asíncrono:

* Eventos de historial entran en cola

* Servicio de notificaciones consume eventos

* Genera alertas para usuarios interesados

## Diagrana de componentes
[Cliente] → [API REST] → [Servicio de Tareas] ↔ [Base de Datos]
                              ↓
                      [Servicio de Historial]
                              ↓
                      [Cola RabbitMQ] → [Procesador de Eventos]

## Operaciones Principales
| Endpoint                    | Método | Descripción                     |
|-----------------------------|--------|---------------------------------|
| `/api/tasks`                | POST   | Crear nueva tarea               |
| `/api/tasks/{id}`           | PUT    | Actualizar tarea existente      |
| `/api/tasks/{id}`           | DELETE | Eliminar tarea                  |
| `/api/tasks/{id}/status`    | PATCH  | Completar tarea                 |
| `/api/tasks/{id}/history`   | GET    | Obtener historial completo      |
| `/api/users/{id}`           | GET    | Búsqueda de usuario             |
