-- Script de inicialización de base de datos ShopSmart
-- Se ejecuta automáticamente al crear el contenedor PostgreSQL

-- Extensiones útiles
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pg_trgm"; -- Para búsquedas de texto eficientes

-- Índice adicional para búsqueda full-text en nombres
-- (Hibernate crea las tablas, este script agrega optimizaciones extra)

-- Datos iniciales: usuario administrador (se crea tras primer arranque de la app)
-- password: Admin123 (encriptado con BCrypt, se genera en runtime)

\echo 'Base de datos shopsmart_usuarios inicializada correctamente'
