#!/bin/bash

# MySQL Setup Guide for Data Vessel

echo "========================================="
echo "MySQL Setup for Data Vessel"
echo "========================================="
echo ""

# Check if MySQL is running
if ! pgrep -x "mysqld" > /dev/null; then
    echo "⚠️  MySQL is not running. Starting MySQL..."
    brew services start mysql
    sleep 3
fi

echo "Please enter your MySQL root password:"
echo "(If you don't know it, press Ctrl+C and reset it)"
echo ""

# Prompt for password
read -s -p "MySQL root password: " MYSQL_ROOT_PASSWORD
echo ""

# Test connection
if mysql -u root -p"$MYSQL_ROOT_PASSWORD" -e "SELECT 1;" > /dev/null 2>&1; then
    echo "✅ Connected to MySQL successfully!"
    echo ""
    
    # Create databases
    echo "Creating databases..."
    mysql -u root -p"$MYSQL_ROOT_PASSWORD" <<EOF
CREATE DATABASE IF NOT EXISTS user_service;
CREATE DATABASE IF NOT EXISTS file_service;
SHOW DATABASES;
SELECT 'Databases created successfully!' AS Status;
EOF
    
    echo ""
    echo "========================================="
    echo "✅ MySQL Setup Complete!"
    echo "========================================="
    echo ""
    echo "Databases created:"
    echo "  - user_service"
    echo "  - file_service"
    echo ""
    echo "Now update your application.properties files with:"
    echo "  username: root"
    echo "  password: (your MySQL root password)"
    echo ""
    
else
    echo "❌ Failed to connect to MySQL"
    echo ""
    echo "To reset your MySQL root password:"
    echo "1. Stop MySQL: brew services stop mysql"
    echo "2. Run: mysqld_safe --skip-grant-tables &"
    echo "3. Run: mysql -u root"
    echo "4. Run: ALTER USER 'root'@'localhost' IDENTIFIED BY 'NewPassword123';"
    echo "5. Run: FLUSH PRIVILEGES;"
    echo "6. Restart: brew services restart mysql"
fi
