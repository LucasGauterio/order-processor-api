package order.processor.api.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.configuration.kafka.annotation.KafkaKey;
import io.micronaut.configuration.kafka.annotation.KafkaListener;
import io.micronaut.configuration.kafka.annotation.OffsetReset;
import io.micronaut.configuration.kafka.annotation.Topic;
import order.processor.api.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@KafkaListener(offsetReset = OffsetReset.EARLIEST)
public class OrderListener {

    private final Logger log = LoggerFactory.getLogger(OrderListener.class);

    public static final String FARE_AIR = "FARE_AIR";
    public static final String BOOKING_AIR = "BOOKING_AIR";
    public static final String INIT = "INIT";
    public static final String PAYMENT = "PAYMENT";
    public static final String RISK_ANALYSIS = "RISK_ANALYSIS";
    
    @Inject
    OrderProvider provider;

    @Inject
    ObjectMapper mapper;

    @Topic("orders")
    public void receive(@KafkaKey String key, Order orderState) {
        if(canExecuteState(INIT, orderState)) {
            process(key, orderState, INIT);
        }else if(canExecuteState(RISK_ANALYSIS, orderState)){
            Order order = normalizeOrder(orderState);
            process(key, order, RISK_ANALYSIS);
        }else if(canExecuteState(PAYMENT, orderState)){
            Order order = normalizeOrder(orderState);
            process(key, order, PAYMENT);
        }else{
            log.info("Ignored Order - " + orderState + " by " + key);
        }

    }

    private void process(String key, Order orderState, String init) {
        logReceived(key, init, orderState);
        sleep();
        logProcessed(key, init, orderState);
        orderState.getProcessedStates().add(init);
        provider.sendOrder(orderState.getId(), orderState);
    }

    private void sleep() {
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void logReceived(String key, String state, Order order){
        log.info("Received " + state + " Order - " + order + " by " + key);
    }

    private void logProcessed(String key, String state, Order order){
        log.info("Processed " + state + " - Order - " + order + " by " + key);
    }

    private Order normalizeOrder(Order orderState) {
        Order order = null;
        try {
            order = mapper.readValue(orderState.getMessage(), Order.class);
            order.setProcessedStates(orderState.getProcessedStates());
            order.setMessage(orderState.getMessage());
            order.setId(orderState.getId());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return order;
    }

    public boolean canExecuteState(String state, Order order){
        if(order.getProcessedStates().contains(state))
            return false;
        List<String> needed = new ArrayList<>();
        switch (state) {
            case INIT: needed = new ArrayList<>();break;
            case RISK_ANALYSIS: needed = Arrays.asList(INIT,FARE_AIR,BOOKING_AIR);break;
            case PAYMENT: needed = Arrays.asList(INIT,FARE_AIR,BOOKING_AIR,RISK_ANALYSIS);break;
        }
        return order.getProcessedStates().containsAll(needed);
    }

}