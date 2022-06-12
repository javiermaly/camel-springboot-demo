package com.endava.camel_demo.routes;

import com.endava.camel_demo.model.IndividualOrder;
import com.endava.camel_demo.model.Product;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.springframework.stereotype.Component;

@Component
public class DessertStationRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("activemq:dessert")
                .routeId("amqDessertStation")
                .marshal(new JacksonDataFormat(IndividualOrder.class))
                .log(body().toString())
        ;
    }
}
