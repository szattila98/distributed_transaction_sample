package hu.me.iit.orderservice;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient("customer-service")
public interface CustomerClient {

    @GetMapping("/customer/{customerId}")
    Customer getCustomer(@PathVariable int customerId);

    @PostMapping("/charge/customer/{customerId}/amount/{amount}")
    Customer charge(@PathVariable int customerId, @PathVariable double amount);
}
