-- init.sql
CREATE DATABASE IF NOT EXISTS taskdb;
USE taskdb;

-- Tabla de usuarios (app_user como especificaste en la anotación @Table)
CREATE TABLE IF NOT EXISTS app_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255),
    email VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla de tareas (tasks)
CREATE TABLE IF NOT EXISTS tasks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    priority VARCHAR(20),
    assigned_user_id BIGINT,
    created_at DATETIME,
    updated_at DATETIME,
    FOREIGN KEY (assigned_user_id) REFERENCES app_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Datos iniciales de ejemplo
INSERT IGNORE INTO app_user (username, email) VALUES
('admin', 'admin@example.com'),
('user1', 'user1@example.com'),
('user2', 'user2@example.com');

INSERT IGNORE INTO tasks (title, description, status, priority, assigned_user_id, created_at, updated_at) VALUES
('Configurar BD', 'Configurar la base de datos con Docker', 'IN_PROGRESS', 'HIGH', 1, NOW(), NOW()),
('Desarrollar API', 'Implementar endpoints REST', 'PENDING', 'MEDIUM', 2, NOW(), NOW()),
('Pruebas unitarias', 'Escribir pruebas para los servicios', 'COMPLETED', 'LOW', 3, NOW(), NOW());