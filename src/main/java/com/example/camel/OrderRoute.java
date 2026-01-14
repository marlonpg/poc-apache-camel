package com.example.camel;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class OrderRoute extends RouteBuilder {

    @Override
    public void configure() {
        
        // Error handling with retries
        onException(RuntimeException.class)
            .maximumRedeliveries(3)
            .redeliveryDelay(1000)
            .backOffMultiplier(2)
            .log("Error occurred: ${exception.message}. Retrying...")
            .retryAttemptedLogLevel(org.apache.camel.LoggingLevel.WARN)
            .handled(false); // Let it propagate if all retries fail

        // Main route: Process orders with routing logic
        from("direct:processOrder")
            .routeId("order-processing-route")
            .log("Received order: ${body}")
            .choice()
                .when(simple("${body.amount} > 1000"))
                    .log("High-value order detected: ${body.id}")
                    .to("direct:highValueOrder")
                .otherwise()
                    .log("Standard order: ${body.id}")
                    .to("direct:standardOrder")
            .end();

        // High-value order processing with retry
        from("direct:highValueOrder")
            .log("Processing high-value order...")
            .bean(PaymentService.class, "processPayment")
            .log("Result: ${body}")
            .to("direct:success");

        // Standard order processing
        from("direct:standardOrder")
            .log("Processing standard order...")
            .setBody(simple("Order ${body.id} processed without payment"))
            .to("direct:success");

        // Success handler
        from("direct:success")
            .log("✓ Successfully completed: ${body}");
    }
}
