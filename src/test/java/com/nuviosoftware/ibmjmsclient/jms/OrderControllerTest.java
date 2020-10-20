package com.nuviosoftware.ibmjmsclient.jms;

import com.ibm.mq.jms.MQQueue;
import com.nuviosoftware.ibmjmsclient.MQTestContainer;
import com.nuviosoftware.ibmjmsclient.model.OrderRequest;
import com.nuviosoftware.ibmjmsclient.rest.OrderController;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.GenericContainer;

import javax.jms.JMSException;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OrderControllerTest extends MQTestContainer {

    @ClassRule
    public static GenericContainer mqContainer = setupMqContainer();

    @Autowired
    private OrderController orderController;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Test
    public void createOrder() throws JMSException {
        // Given
        OrderRequest orderRequest = OrderRequest.builder().message("TEST123").identifier("123456789").build();

        // When
        orderController.createOrder(orderRequest);

        // Then
        String response = (String) jmsTemplate.receiveAndConvert("ORDER.REQUEST");
        assertEquals(orderRequest.getMessage(), response);
    }

}