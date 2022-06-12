package com.endava.camel_demo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class Order implements Serializable {
    @JsonProperty
    private String tableNr;
    @JsonProperty
    private List<Product> products;
}
