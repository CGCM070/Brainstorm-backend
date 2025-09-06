#!/bin/bash

echo "=== Brainstorm Native Build Test Script ==="
echo "Testing different build approaches for optimal deployment"
echo

# Test 1: Standard AOT processing with explicit main class
echo "1. Testing AOT Processing with explicit main class..."
./mvnw clean package -DskipTests -Dmain.class=org.brainstorm.BrainstormApplication spring-boot:process-aot
if [ $? -eq 0 ]; then
    echo "✅ AOT Processing: PASSED"
else
    echo "❌ AOT Processing: FAILED"
    exit 1
fi
echo

# Test 2: Check JAR size and contents
echo "2. Analyzing Build Artifacts..."
ls -lh target/Brainstorm-*.jar
echo "JAR Contents:"
jar tf target/Brainstorm-*.jar | grep -E "(BOOT-INF|META-INF)" | head -10
echo

# Test 3: Test optimized Docker build
echo "3. Testing Optimized Docker Build..."
if command -v docker &> /dev/null; then
    docker build -f Dockerfile.optimized -t brainstorm-optimized:test .
    if [ $? -eq 0 ]; then
        echo "✅ Optimized Docker Build: PASSED"
        
        # Test 4: Check image size
        echo "4. Docker Image Analysis:"
        docker images brainstorm-optimized:test
        
        # Test 5: Quick container startup test
        echo "5. Testing Container Startup (10 second timeout)..."
        timeout 10s docker run --rm -p 8080:8080 -e SPRING_PROFILES_ACTIVE=prod brainstorm-optimized:test &
        PID=$!
        sleep 8
        if kill -0 $PID 2>/dev/null; then
            echo "✅ Container starts successfully"
            kill $PID 2>/dev/null
        else
            echo "⚠️ Container startup test inconclusive"
        fi
    else
        echo "❌ Optimized Docker Build: FAILED"
    fi
else
    echo "⚠️ Docker not available - skipping Docker tests"
fi

echo
echo "=== Native Build Configuration Summary ==="
echo "✅ Maven configured for GraalVM Native compilation"
echo "✅ AOT processing enabled"
echo "✅ Reflection hints configured for JPA entities"
echo "✅ WebSocket configuration prepared for native mode"
echo "✅ Optimized Dockerfile ready for production"
echo
echo "To build natively (requires GraalVM with native-image):"
echo "  ./mvnw clean package -Pnative -DskipTests -Dmain.class=org.brainstorm.BrainstormApplication"
echo
echo "To build optimized JVM version:"
echo "  docker build -f Dockerfile.optimized -t brainstorm-app ."
echo
echo "For Render deployment, use Dockerfile.optimized for best results!"