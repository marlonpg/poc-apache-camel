# Apache Camel Demo - Spring Boot Application

A simple Spring Boot application demonstrating Apache Camel with retry logic and error handling.

## What This Demo Shows

- **Routing Logic**: Routes orders based on amount (high-value vs standard)
- **Retry Mechanism**: Automatically retries failed operations with exponential backoff
- **Error Handling**: Graceful error handling with logging
- **Integration**: REST API triggers Camel routes

## Project Structure

```
src/main/java/com/example/camel/
├── CamelDemoApplication.java   # Main Spring Boot app
├── Order.java                  # Order model
├── OrderRoute.java             # Camel routes with retry logic
├── OrderController.java        # REST API endpoints
└── PaymentService.java         # Service that simulates failures
```

## How It Works

1. **REST Endpoint** receives an order
2. **Camel Route** processes the order:
   - If amount > $1000 → High-value processing (with retries)
   - If amount ≤ $1000 → Standard processing
3. **PaymentService** simulates failures (fails 2 times, succeeds on 3rd attempt)
4. **Retry Logic** automatically retries with exponential backoff:
   - 1st retry: 1 second delay
   - 2nd retry: 2 seconds delay
   - 3rd retry: 4 seconds delay

## Running the Application

### Prerequisites
- Java 17+
- Maven Wrapper (included)

### Build and Run

```bash
# Using Maven Wrapper (recommended)
./mvnw clean install
./mvnw spring-boot:run

# Or use the provided script
./run.sh
```

The application will start on `http://localhost:8080`

## Testing the Routes

### Test High-Value Order (with retries)
```bash
curl http://localhost:8080/api/orders/test-high-value
```

**Expected behavior:**
- First 2 attempts will fail
- Camel will retry with delays
- 3rd attempt succeeds
- Check console logs to see retry attempts

### Test Standard Order (no retries needed)
```bash
curl http://localhost:8080/api/orders/test-standard
```

**Expected behavior:**
- Processes immediately without calling PaymentService
- No retries needed

### Custom Order via POST
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"id":"ORD-123","amount":2000}'
```

## Key Camel Concepts Demonstrated

### 1. Route Definition
```java
from("direct:processOrder")
    .log("Received order: ${body}")
    .to("direct:nextStep");
```

### 2. Content-Based Routing
```java
.choice()
    .when(simple("${body.amount} > 1000"))
        .to("direct:highValueOrder")
    .otherwise()
        .to("direct:standardOrder")
.end();
```

### 3. Error Handling with Retries
```java
onException(RuntimeException.class)
    .maximumRedeliveries(3)
    .redeliveryDelay(1000)
    .backOffMultiplier(2)
    .log("Error occurred. Retrying...");
```

### 4. Bean Integration
```java
.bean(PaymentService.class, "processPayment")
```

## Console Output Example

When testing high-value order, you'll see:
```
Received order: Order{id='ORD-001', amount=1500.0}
High-value order detected: ORD-001
Processing high-value order...
PaymentService called - Attempt #1 for Order{id='ORD-001', amount=1500.0}
Error occurred: Payment service temporarily unavailable. Retrying...
PaymentService called - Attempt #2 for Order{id='ORD-001', amount=1500.0}
Error occurred: Payment service temporarily unavailable. Retrying...
PaymentService called - Attempt #3 for Order{id='ORD-001', amount=1500.0}
Result: Payment processed successfully for order: ORD-001
✓ Successfully completed: Payment processed successfully for order: ORD-001
```

## Next Steps

To extend this demo:
- Add Kafka integration for event publishing
- Add database persistence
- Add circuit breaker pattern
- Add dead letter queue for failed messages
- Add monitoring with Camel metrics
