package com.endava.camel_demo.routes;

import com.endava.camel_demo.model.IndividualOrder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.springframework.stereotype.Component;

@Component
public class MealStationRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("activemq:meal")
                .routeId("amqMealStation")
                .marshal(new JacksonDataFormat(IndividualOrder.class))
                .log(body().toString())
        ;
    }
}
