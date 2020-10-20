package com.nuviosoftware.ibmjmsclient.jms;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

@Slf4j
@Component
public class OrderResponseListener {

    @JmsListener(destination = "orders/requests")
    public void receive1(Message message) throws JMSException {
        TextMessage textMessage = (TextMessage) message;
        log.info("### 4a ### Order Service received message response : {} with correlation id: {}",
                textMessage.getText(), textMessage.getJMSCorrelationID());
    }

    @JmsListener(destination = "orders/requests")
    public void receive2(Message message) throws JMSException {
        TextMessage textMessage = (TextMessage) message;
        log.info("### 4b ### Order Service received message response : {} with correlation id: {}",
                textMessage.getText(), textMessage.getJMSCorrelationID());
    }
}
