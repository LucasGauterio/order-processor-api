package order.processor.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.validation.Validated;
import order.processor.api.kafka.OrderProvider;
import order.processor.api.model.Order;

import javax.inject.Inject;
import javax.validation.Valid;

@Validated
@Controller("/api")
public class ApiController {

    @Inject
    OrderProvider provider;

    @Inject
    ObjectMapper mapper;

    @Post(value="/orders", consumes = {MediaType.APPLICATION_JSON}, produces = {MediaType.APPLICATION_JSON})
    public HttpResponse<?> postOrder(@Body String orderMessage) throws JsonProcessingException {
        orderMessage = orderMessage.replaceAll("\\n","");
        Order order = mapper.readValue(orderMessage, Order.class);
        order.setMessage(orderMessage);
        if(order.getId() == null || "".equals(order.getId()))
            order.setId(String.valueOf(order.hashCode()));
        provider.sendOrder(order.getId(),order);
        return HttpResponse.status(HttpStatus.CREATED).body(order);
    }

    @Get(value="/orders/{id}", produces = {MediaType.APPLICATION_JSON})
    public HttpResponse<?> getOrder(String id) {
        return HttpResponse.status(HttpStatus.OK).body("GET ORDER "+id);
    }

}
