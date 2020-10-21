package com.nuviosoftware.ibmjmsclient.model;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@ToString
@Builder
@Data
public class OrderRequest implements Serializable {
    private String name;
    private String identifier;
    private Long quantity;
}
