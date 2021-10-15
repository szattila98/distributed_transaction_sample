package hu.me.iit.orderservice.feign;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Customer {
    private int id;
    private String name;
    private double credits;
}
