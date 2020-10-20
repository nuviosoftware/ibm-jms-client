package com.nuviosoftware.ibmjmsclient;

import org.junit.Rule;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.FixedHostPortGenericContainer;
import org.testcontainers.containers.GenericContainer;

import java.util.HashMap;
import java.util.Map;

public class MQTestContainer {

    public static GenericContainer setupMqContainer() {
        Map<String, String> environmentVariables = new HashMap<String, String>() {{
            put("LICENSE", "accept");
            put("MQ_QMGR_NAME", "QM1");
        }};

        GenericContainer mqContainer =
                new FixedHostPortGenericContainer("nuviosoftware-mq-local")
                        .withFixedExposedPort(1414, 1414)
                        .withExtraHost("localhost", "0.0.0.0")
                        .withEnv(environmentVariables);

        return mqContainer;
    }

}
