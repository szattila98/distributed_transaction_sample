package hu.me.iit.customerservice;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomerController {

    private final Customer customer = new Customer(1, "John Doe", 1000);

    @GetMapping("/customer/{customerId}")
    public Customer getCustomer(@PathVariable int customerId) {
        return customer;
    }

    @PostMapping("/charge/customer/{customerId}/amount/{amount}")
    public Customer charge(@PathVariable int customerId, @PathVariable double amount) {
        if (customer.getCredits() < amount) {
            throw new RuntimeException("Not enough credits!");
        }
        customer.charge(amount);
        return customer;
    }

    @GetMapping
    public Customer customers() {
        return customer;
    }
}
