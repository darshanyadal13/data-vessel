-- MySQL Setup Script for Data Vessel
-- Run this script to create databases and users

-- Create databases
CREATE DATABASE IF NOT EXISTS user_service;
CREATE DATABASE IF NOT EXISTS file_service;

-- Show databases
SHOW DATABASES;

-- Grant privileges (if using root user, this is already done)
-- If you want to create a specific user:
-- CREATE USER IF NOT EXISTS 'datavessel'@'localhost' IDENTIFIED BY 'datavessel123';
-- GRANT ALL PRIVILEGES ON user_service.* TO 'datavessel'@'localhost';
-- GRANT ALL PRIVILEGES ON file_service.* TO 'datavessel'@'localhost';
-- FLUSH PRIVILEGES;

SELECT 'MySQL databases created successfully!' AS Status;
