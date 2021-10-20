package hu.me.iit.monolithic_transactions_sample_app.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "products")
public class Product {
    @Id
    @GeneratedValue
    private Integer id;
    private String name;
    private double cost;
    private int inStock;
}
