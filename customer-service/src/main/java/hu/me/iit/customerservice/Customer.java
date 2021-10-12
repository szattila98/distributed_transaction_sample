package hu.me.iit.customerservice;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Customer {
    private int id;
    private String name;
    private double credits;

    public void charge(double amount) {
        this.credits -= amount;
    }
}
