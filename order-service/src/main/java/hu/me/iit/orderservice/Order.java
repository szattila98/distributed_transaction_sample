package hu.me.iit.orderservice;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Order {
    private int customerId;
    private int productId;
}
