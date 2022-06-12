package com.endava.camel_demo.routes;

import com.endava.camel_demo.model.IndividualOrder;
import com.endava.camel_demo.model.Order;
import com.endava.camel_demo.model.Product;
import com.endava.camel_demo.model.ProuctType;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.stereotype.Component;


@Component
public class MainRoute extends RouteBuilder {

    public static final String ENDAVA_TABLE_NR = "ENDAVA.tableNr";
    public static final String DIRECT_BAR = "direct:bar";
    public static final String DIRECT_DESSERT_STATION = "direct:dessertStation";
    public static final String DIRECT_MEAL_STATION = "direct:mealStation";
    public static final String DIRECT_BARBACUE = "direct:barbacue";
    public static final String DIRECT_OTHERS = "direct:others";
    public static final String DIRECT_MAIN = "direct:main";
    public static final String ACTIVEMQ_BAR = "activemq:bar";
    public static final String ACTIVEMQ_DESSERT = "activemq:dessert";
    public static final String ACTIVEMQ_MEAL = "activemq:meal";
    public static final String ACTIVEMQ_BARBACUE = "activemq:barbacue";
    public static final String ACTIVEMQ_OTHERS = "activemq:others";

    @Override
    public void configure() throws Exception {

       onException(Exception.class)
                .handled(true)
                .log("ERROR");

        from("file:orders")
                .routeId("files")
                .log("Incoming File: ${file:onlyname}") // logs the file name
                .unmarshal(new JacksonDataFormat(Order.class))   // unmarshal JSON to Order class
                .to(DIRECT_MAIN)
              ;

        from(DIRECT_MAIN)
                .routeId("main")
                .log("${body}")
                .log("TableNr: ${body.tableNr}")
                .setHeader(ENDAVA_TABLE_NR, simple("${body.tableNr}")) // sets the tableNr as a header
                .split().simple("${body.products}")   // split list to process products one by one
                .log("Product: ${body}")// logs the product

                .choice() // inspects the body in order to route based on content

                .when().simple("${body.productType} == '" + ProuctType.BAR.name() + "'")
                .process(convertProcessorProductToIndividualOrder()) // uses the processor to map to an individual order
                .to(DIRECT_BAR)

                .when().simple("${body.productType} == '" + ProuctType.DESSERT.name() + "'")
                .process(convertProcessorProductToIndividualOrder())
                .to(DIRECT_DESSERT_STATION)

                .when().simple("${body.productType} == '" + ProuctType.MEAL.name() + "'")
                .process(convertProcessorProductToIndividualOrder())
                .to(DIRECT_MEAL_STATION)

                .when().simple("${body.productType} == '" + ProuctType.BARBACUE.name() + "'")
                .process(convertProcessorProductToIndividualOrder())
                .to(DIRECT_BARBACUE)

                .otherwise()
                .to(DIRECT_OTHERS);

        from(DIRECT_BAR)
                .routeId("bar")
                .log("Handling Drink")
                .to(ACTIVEMQ_BAR);
        from(DIRECT_DESSERT_STATION)
                .routeId("dessertStation")
                .log("Handling Dessert")
                .to(ACTIVEMQ_DESSERT);
        from(DIRECT_MEAL_STATION)
                .routeId("mealStation")
                .log("Handling Meal")
                .to(ACTIVEMQ_MEAL);
        from(DIRECT_BARBACUE)
                .routeId("barbacueStation")
                .log("Handling Barbacue")
                .to(ACTIVEMQ_BARBACUE);
        from(DIRECT_OTHERS)
                .routeId("others")
                .log("Handling Something Other")
                .to(ACTIVEMQ_OTHERS);

        // Creating a Rest endpoint that also consumes the same camel route to based content routing
        // http://localhost:8080/camel/api/orders
        rest("/api/")
                .id("api-route")
                .consumes("application/json")
                .post("/orders")
                .route()
                .unmarshal(new JacksonDataFormat(Order.class))   // unmarshal JSON to Order class
                .to(DIRECT_MAIN)
                .removeHeaders("ENDAVA*");
    }


    private Processor convertProcessorProductToIndividualOrder() {
       return exchange -> {
           Product product = exchange.getIn().getBody(Product.class);
           IndividualOrder indOrder = IndividualOrder.builder()
                   .tableNr(String.valueOf(exchange.getIn().getHeader(ENDAVA_TABLE_NR)))
                   .id(product.getId())
                   .name(product.getName())
                   .build();
           exchange.getIn().setBody(indOrder, IndividualOrder.class);
       };
    }

}
