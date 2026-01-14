#!/bin/bash

echo "Building Apache Camel Demo..."
./mvnw clean install

if [ $? -eq 0 ]; then
    echo "Build successful!"
else
    echo "Build failed!"
    exit 1
fi
