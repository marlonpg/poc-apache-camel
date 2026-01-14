package com.example.camel;

import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private ProducerTemplate producerTemplate;

    @PostMapping
    public String createOrder(@RequestBody Order order) {
        try {
            Object result = producerTemplate.requestBody("direct:processOrder", order);
            return "Success: " + result;
        } catch (Exception e) {
            return "Failed after retries: " + e.getMessage();
        }
    }

    @GetMapping("/test-high-value")
    public String testHighValue() {
        Order order = new Order("ORD-001", 1500.00);
        return createOrder(order);
    }

    @GetMapping("/test-standard")
    public String testStandard() {
        Order order = new Order("ORD-002", 500.00);
        return createOrder(order);
    }
}
