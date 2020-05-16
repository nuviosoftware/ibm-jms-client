FROM openjdk:8-jdk-alpine
EXPOSE 8080
WORKDIR /app
COPY target/ibm-jms-client-1.0.0.jar /app/ibm-jms-client-1.0.0.jar
ADD https://github.com/ufoscout/docker-compose-wait/releases/download/2.2.1/wait /wait
RUN chmod +x /wait

ENTRYPOINT /wait && exec java $JVM_OPTIONS -XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap -XX:MaxRAMFraction=2 -jar -Djava.awt.headless=true  ibm-jms-client-1.0.0.jar