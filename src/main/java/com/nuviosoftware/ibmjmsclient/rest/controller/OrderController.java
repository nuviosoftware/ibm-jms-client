package com.nuviosoftware.ibmjmsclient.rest.controller;

import com.ibm.mq.jms.MQQueue;
import com.nuviosoftware.ibmjmsclient.rest.model.OrderRequest;
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
    public ResponseEntity<String> createOrder(@RequestBody OrderRequest order) throws JMSException {
        log.info("Sending order message '{}' to the queue", order.getMessage());

        MQQueue orderRequestQueue = new MQQueue("ORDER.REQUEST");
        jmsTemplate.convertAndSend(orderRequestQueue, order.getMessage(), textMessage -> {
            textMessage.setJMSCorrelationID(order.getIdentifier());
            return textMessage;
        });

        return new ResponseEntity(order, HttpStatus.ACCEPTED);
    }


    @GetMapping
    public ResponseEntity<OrderRequest> findOrder(@RequestParam String correlationId) throws JMSException {
        log.info("Looking for message '{}'", correlationId);
        String convertedId = convertCorrelationId(correlationId.getBytes());
        final String selectorExpression = String.format("JMSCorrelationID='ID:%s'", convertedId);
        final TextMessage responseMessage = (TextMessage) jmsTemplate.receiveSelected("ORDER.REQUEST", selectorExpression);
        OrderRequest response = OrderRequest.builder()
                .message(responseMessage.getText())
                .identifier(correlationId)
                .build();
        return new ResponseEntity(response, HttpStatus.OK);
    }


    public String convertCorrelationId(byte[] bytes) {
        char[] hexArray = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
}
