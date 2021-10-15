package hu.me.iit.customerservice;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerRepository repository;

    @PostConstruct
    public void init() {
        repository.save(new Customer(1, "John Doe", 1000));
    }

    @GetMapping("/customer/{customerId}")
    public Customer getCustomer(@PathVariable int customerId) {
        return repository.findById(customerId).orElseThrow();
    }

    @PostMapping("/charge/customer/{customerId}/amount/{amount}")
    public Customer charge(@PathVariable int customerId, @PathVariable double amount) {
        var customer = repository.findById(customerId).orElseThrow();
        if (customer.getCredits() < amount) {
            throw new NotEnoughCreditsException();
        }
        customer.charge(amount);
        return repository.save(customer);
    }

    @GetMapping
    public List<Customer> customers() {
        return repository.findAll();
    }
}
