package hu.me.iit.orderservice;

import hu.me.iit.orderservice.feign.CustomerClient;
import hu.me.iit.orderservice.feign.StorageClient;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderRepository repository;
    private final CustomerClient customerClient;
    private final StorageClient storageClient;

    @GetMapping("/order/for/{customerId}/product/{productId}")
    @Transactional
    public String order(@PathVariable int customerId, @PathVariable int productId) {
        var order = new Order( customerId, productId);
        repository.save(order);
        var product = storageClient.getProduct(productId);
        storageClient.deliver(productId);
        customerClient.charge(customerId, product.getCost());
        var customer = customerClient.getCustomer(customerId);
        return "Order placed for " + customer + ", product " + product + " is in delivery!";
    }

    @GetMapping
    public List<Order> orders() {
        return repository.findAll();
    }

}
