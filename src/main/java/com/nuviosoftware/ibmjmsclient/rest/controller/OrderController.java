package com.nuviosoftware.ibmjmsclient.rest.controller;

import com.ibm.mq.jms.MQQueue;
import com.nuviosoftware.ibmjmsclient.rest.model.OrderRequest;
import com.nuviosoftware.ibmjmsclient.rest.model.OrderResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.*;

import javax.jms.JMSException;
import javax.jms.TextMessage;

@Slf4j
@RequestMapping("orders")
@RestController
public class OrderController {

    @Autowired
    private JmsTemplate jmsTemplate;

    @PostMapping
    public ResponseEntity<String> createOrder(@RequestBody OrderRequest order,
                                              @RequestHeader(name = "X-Correlation-ID") String correlationId) throws JMSException {
        log.info("Sending order message '{}' to the queue", order.getMessage());

        // request
        MQQueue orderRequestQueue = new MQQueue("ORDER.REQUEST");
        jmsTemplate.convertAndSend(orderRequestQueue, order.getMessage(), textMessage -> {
            textMessage.setJMSCorrelationID(correlationId);
            return textMessage;
        });

        return new ResponseEntity(correlationId, HttpStatus.ACCEPTED);
    }


    @GetMapping
    public ResponseEntity<OrderResponse> receiveOrder(@RequestParam String correlationId) throws JMSException {
        final String selectorExpression = String.format("JMSCorrelationID='ID:%s'", correlationId);
        final TextMessage responseMessage = (TextMessage) jmsTemplate.receiveSelected("ORDER.RESPONSE", selectorExpression);
        return new ResponseEntity(OrderResponse.builder().response(responseMessage.getText()).build(), HttpStatus.OK);
    }

}
