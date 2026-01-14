#!/bin/bash

echo "🔨 Building Apache Camel Demo..."
./mvnw clean install

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ Build successful!"
    echo ""
    echo "🚀 Starting application..."
    echo "   Application will be available at: http://localhost:8080"
    echo ""
    ./mvnw spring-boot:run
else
    echo "❌ Build failed!"
    exit 1
fi
