package com.nuviosoftware.ibmjmsclient.rest.controller;

import com.ibm.mq.jms.MQQueue;
import com.nuviosoftware.ibmjmsclient.rest.model.MessageRequest;
import com.nuviosoftware.ibmjmsclient.rest.model.MessageResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.*;

import javax.jms.JMSException;
import javax.jms.TextMessage;

@Slf4j
@RequestMapping("jms")
@RestController
public class JmsController {

    @Autowired
    private JmsTemplate jmsTemplate;

    @PostMapping("/send-message")
    public ResponseEntity<String> sendMessage(@RequestBody MessageRequest message,
                                              @RequestHeader(name = "X-Correlation-ID") String correlationId) throws JMSException {
        log.info("Sending message '{}' to the queue", message.getMessage());

        // request
        MQQueue requestQueue = new MQQueue("ORDER.REQUEST");
        jmsTemplate.convertAndSend(requestQueue, message.getMessage(), textMessage -> {
            textMessage.setJMSCorrelationID(correlationId);
            return textMessage;
        });

        return new ResponseEntity(correlationId, HttpStatus.ACCEPTED);
    }


    @GetMapping("/receive-message")
    public ResponseEntity<MessageResponse> receiveMessage(@RequestParam String correlationId) throws JMSException {
        final String selectorExpression = String.format("JMSCorrelationID='ID:%s'", correlationId);
        final TextMessage responseMessage = (TextMessage) jmsTemplate.receiveSelected("ORDER.RESPONSE", selectorExpression);
        return new ResponseEntity(MessageResponse.builder().response(responseMessage.getText()).build(), HttpStatus.OK);
    }

}
