package com.nuviosoftware.ibmjmsclient.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("jms")
@RestController
public class JmsController {

    @Autowired
    private JmsTemplate jmsTemplate;

}
