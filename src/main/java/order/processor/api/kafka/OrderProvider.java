package order.processor.api.kafka;

import io.micronaut.configuration.kafka.annotation.KafkaClient;
import io.micronaut.configuration.kafka.annotation.KafkaKey;
import io.micronaut.configuration.kafka.annotation.Topic;
import order.processor.api.model.Order;

@KafkaClient
public interface OrderProvider {

    @Topic("orders")
    void sendOrder(@KafkaKey String key, Order order);

}