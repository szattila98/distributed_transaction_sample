package hu.me.iit.customerservice;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Customer {
    @Id
    @GeneratedValue
    private int id;
    private String name;
    private double credits;

    public void charge(double amount) {
        this.credits -= amount;
    }
}
