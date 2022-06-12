package com.endava.camel_demo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product implements Serializable {
    @JsonProperty
    private Long id;
    @JsonProperty
    private ProuctType productType;
    @JsonProperty
    private String name;
    @JsonProperty
    private String description;
    @JsonProperty
    private float price;
}
