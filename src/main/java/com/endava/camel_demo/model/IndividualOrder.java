package com.endava.camel_demo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class IndividualOrder implements Serializable {
    @JsonProperty
    private String tableNr;
    @JsonProperty
    private Long id;
    @JsonProperty
    private String name;
}
