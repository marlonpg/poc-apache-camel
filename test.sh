#!/bin/bash

BASE_URL="http://localhost:8080/api/orders"

echo "🧪 Testing Apache Camel Demo Endpoints"
echo "========================================"
echo ""

echo "1️⃣  Testing High-Value Order (with retries)..."
echo "   Endpoint: GET $BASE_URL/test-high-value"
echo ""
curl -s $BASE_URL/test-high-value
echo ""
echo ""

echo "2️⃣  Testing Standard Order (no retries)..."
echo "   Endpoint: GET $BASE_URL/test-standard"
echo ""
curl -s $BASE_URL/test-standard
echo ""
echo ""

echo "3️⃣  Testing Custom Order via POST..."
echo "   Endpoint: POST $BASE_URL"
echo ""
curl -s -X POST $BASE_URL \
  -H "Content-Type: application/json" \
  -d '{"id":"ORD-999","amount":2500}'
echo ""
echo ""

echo "✅ Tests completed! Check the application console for detailed logs."
