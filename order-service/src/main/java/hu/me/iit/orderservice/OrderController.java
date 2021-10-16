package hu.me.iit.orderservice;

import feign.FeignException;
import hu.me.iit.orderservice.feign.CustomerClient;
import hu.me.iit.orderservice.feign.StorageClient;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderRepository repository;
    private final CustomerClient customerClient;
    private final StorageClient storageClient;

    @GetMapping
    public List<Order> orders() {
        return repository.findAll();
    }

    // case where order-service verifies whether there are enough credits for customers
    // it may not be a good solution if credits can be deducted by other transactions after the check

    // No transaction, faulty case
    // Rollback happens because of not enough credits, but storage already delivered the product
    @GetMapping("/noTransaction/order/for/{customerId}/product/{productId}")
    public String orderNoTransaction(@PathVariable int customerId, @PathVariable int productId) {
        var product = storageClient.getProduct(productId);
        storageClient.deliver(productId);
        customerClient.charge(customerId, product.getCost()); // rollback happens here, but storage already delivered the product
        var customer = customerClient.getCustomer(customerId);
        var order = new Order(customerId, productId);
        repository.save(order);
        return "Order placed for " + customer + ", product " + product + " is in delivery!";
    }

    // Optimistic like transaction locking
    // Fixes problem where product is delivered because it saves the previous state back when problem arises
    // Introduces another problem, where if another transaction removes product after it queried it may save a product which was already delivered
    // successfully
    @GetMapping("/saveOnError/order/for/{customerId}/product/{productId}")
    public String orderReSaveOnError(@PathVariable int customerId, @PathVariable int productId) {
        var product = storageClient.getProduct(productId);
        storageClient.deliver(productId);
        try {
            customerClient.charge(customerId, product.getCost()); // rollback happens here, but storage already delivered the product
        } catch (FeignException e) {
            storageClient.saveProduct(product);
            return "Order could not be placed, not enough credits, product returned to storage!";
        }
        var customer = customerClient.getCustomer(customerId);
        var order = new Order(customerId, productId);
        repository.save(order);
        return "Order placed for " + customer + ", product " + product + " is in delivery!";
    }
}
