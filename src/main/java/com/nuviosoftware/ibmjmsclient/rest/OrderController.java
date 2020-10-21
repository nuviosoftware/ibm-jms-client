package com.nuviosoftware.ibmjmsclient.rest;

import com.ibm.mq.jms.MQQueue;
import com.nuviosoftware.ibmjmsclient.model.OrderRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.*;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;
import java.nio.charset.StandardCharsets;

@Slf4j
@RequestMapping("orders")
@RestController
public class OrderController {

    @Autowired
    private JmsTemplate jmsTemplate;

    @PostMapping
    public ResponseEntity<String> createOrder(@RequestBody OrderRequest order) throws JMSException {
        log.info("### 1 ### Order Service sending order message '{}' to the queue", order.getMessage());

        MQQueue orderRequestQueue = new MQQueue("ORDER.REQUEST");

        jmsTemplate.convertAndSend(orderRequestQueue, order, item -> {
            item.setJMSCorrelationID(order.getIdentifier());
            return item;
        });

        return new ResponseEntity(order, HttpStatus.ACCEPTED);
    }


    @GetMapping
    public ResponseEntity<OrderRequest> findObjectMessage() throws JMSException {
        final ObjectMessage responseMessage = (ObjectMessage) jmsTemplate.receive("ORDER.REQUEST");
        OrderRequest objectMessage = (OrderRequest) responseMessage.getObject();
        return new ResponseEntity(objectMessage, HttpStatus.OK);
    }

}
