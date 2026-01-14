# Apache Camel Guide

## 1. What is Apache Camel?

Apache Camel is an open-source integration framework that acts as a **smart middleware/router** between different systems and applications.

### Core Purpose
- **Integration**: Connect different systems, protocols, and data formats
- **Routing**: Direct messages to different destinations based on rules
- **Mediation**: Transform and process data as it flows between systems

### Key Features
- **300+ connectors**: HTTP, Kafka, JMS, databases, file systems, cloud services, etc.
- **Enterprise Integration Patterns (EIPs)**: Built-in solutions for common integration challenges
- **Multiple DSLs**: Write integration logic in Java, XML, YAML, or Groovy
- **Lightweight**: Embeddable in Spring Boot, Quarkus, or standalone applications

### Simple Example
```java
from("file:input")
  .transform().body(String.class)
  .to("kafka:orders");
```
This reads files from a directory and sends them to a Kafka topic.

---

## 2. When Should I Use It?

### ✅ Use Apache Camel When:

**Complex Routing Logic**
```
Order → Camel → If amount > $1000 → Premium Service
              → If amount < $1000 → Standard Service
              → Also log to Kafka
              → Also save to database
```

**Protocol/Format Translation**
```
Legacy SOAP/XML System → Camel → Transform to JSON → Modern REST API
```

**Robust Error Handling & Retries**
```java
from("direct:payment")
    .to("http://payment-service")
    .onException(Exception.class)
        .maximumRedeliveries(3)
        .redeliveryDelay(2000)
        .to("jms:dead-letter-queue")
    .end();
```

**Multiple System Integration**
- Connecting 5+ different systems with different protocols
- Aggregating data from multiple sources
- Fan-out patterns (send to multiple destinations)

**Frequent Integration Changes**
- When integration requirements change often
- Need declarative configuration instead of imperative code

### ❌ Don't Use Apache Camel When:

**Simple Point-to-Point Calls**
```java
// Just use RestTemplate or WebClient
restTemplate.postForObject("http://service-b/api", data, Response.class);
```

**Simple Flow: A → B → Kafka**
```java
// Plain Spring Boot is fine
PaymentResult result = restTemplate.postForObject(...);
kafkaTemplate.send("payment-result", result);
```

**Your Team Doesn't Know Camel**
- Learning curve might not be worth it for simple use cases
- Stick with familiar tools (Spring Integration, plain HTTP clients)

### Real-World Decision Example

**Scenario**: Microservice A calls Microservice B, then sends result to Kafka

**Without Camel** (Recommended for simple cases):
```java
@Service
public class PaymentService {
    public void processPayment(Order order) {
        PaymentResult result = restTemplate.postForObject(
            "http://service-b/payment", order, PaymentResult.class);
        kafkaTemplate.send("payment-result", result);
    }
}
```

**With Camel** (When it grows complex):
```java
from("direct:payment")
    .log("Processing payment: ${body}")
    .choice()
        .when(simple("${body.amount} > 10000"))
            .to("jms:manual-review-queue")
        .otherwise()
            .to("http://service-b/payment")
            .to("kafka:payment-result")
            .to("sql:INSERT INTO payments...")
            .to("http://fraud-service/check")
    .end()
    .onException(HttpServerErrorException.class)
        .maximumRedeliveries(3)
        .redeliveryDelay(1000)
        .backOffMultiplier(2)
    .end();
```

---

## Summary

**Apache Camel is:**
- A smart integration framework, not just an orchestrator
- Best for complex routing, transformation, and multi-system integration
- Overkill for simple A→B calls

**Use it when integration becomes complex and repetitive. Avoid it for simple flows.**

---

## References

- [Apache Camel Official Website](https://camel.apache.org/)
- [Apache Camel Documentation](https://camel.apache.org/manual/)
- [Enterprise Integration Patterns](https://www.enterpriseintegrationpatterns.com/)
- [Camel Components List](https://camel.apache.org/components/latest/)
- [Camel Examples on GitHub](https://github.com/apache/camel-examples)
