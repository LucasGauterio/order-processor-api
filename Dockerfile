FROM openjdk:14-alpine
COPY target/order-processor-api-*.jar order-processor-api.jar
EXPOSE 8080
CMD ["java", "-Dcom.sun.management.jmxremote", "-Xmx128m", "-jar", "order-processor-api.jar"]