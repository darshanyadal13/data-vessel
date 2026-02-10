#!/bin/bash

echo "Data Vessel API Test Script"
echo "=============================="
echo ""

echo "1️⃣  Testing User Registration..."
echo "--------------------------------"
REGISTER_RESPONSE=$(curl -s -w "\n%{http_code}" -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"demouser","email":"demo@test.com","password":"Test@123"}')

HTTP_CODE=$(echo "$REGISTER_RESPONSE" | tail -n 1)
BODY=$(echo "$REGISTER_RESPONSE" | sed '$d')

echo "Response: $BODY"
echo "HTTP Code: $HTTP_CODE"
echo ""

if [ "$HTTP_CODE" = "200" ] || [ "$HTTP_CODE" = "201" ]; then
    echo "✅ Registration successful!"
else
    echo "❌ Registration failed"
fi

echo ""
echo "2️⃣  Testing User Login..."
echo "--------------------------------"
LOGIN_RESPONSE=$(curl -s -w "\n%{http_code}" -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"prasad","password":"Root@123"}')

HTTP_CODE=$(echo "$LOGIN_RESPONSE" | tail -n 1)
BODY=$(echo "$LOGIN_RESPONSE" | sed '$d')

echo "Response: $BODY"
echo "HTTP Code: $HTTP_CODE"
echo ""

if [ "$HTTP_CODE" = "200" ]; then
    echo "✅ Login successful!"
    # Extract token if present
    TOKEN=$(echo "$BODY" | grep -o '"token":"[^"]*"' | cut -d'"' -f4)
    if [ -n "$TOKEN" ]; then
        echo "JWT Token: ${TOKEN:0:50}..."
    fi
else
    echo "❌ Login failed"
fi

echo ""
echo "=============================="
echo "Test Complete!"
