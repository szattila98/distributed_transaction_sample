package hu.me.iit.orderservice;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "orders")
public class Order {
    @Id
    @GeneratedValue
    private int id;
    private int customerId;
    private int productId;

    public Order(int customerId, int productId) {
        this.customerId = customerId;
        this.productId = productId;
    }
}
