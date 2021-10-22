package hu.me.iit.orderservice;

import feign.FeignException;
import hu.me.iit.orderservice.feign.CustomerClient;
import hu.me.iit.orderservice.feign.StorageClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
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

    // case could exist here where order-service verifies whether there are enough credits for customers
    // it may not be a good solution if credits can be deducted by other transactions after the check happened

    // No transaction, faulty case
    // Rollback can happen because of not enough credits, but storage already delivered the product then
    @PostMapping("/noTransaction/order/for/{customerId}/product/{productId}")
    @Transactional
    public String orderNoTransaction(@PathVariable int customerId, @PathVariable int productId) {
        var order = new Order(customerId, productId);
        repository.save(order);
        var product = storageClient.getProduct(productId);
        storageClient.deliver(productId);
        customerClient.charge(customerId, product.getCost()); // rollback happens here, but storage already delivered the product
        var customer = customerClient.getCustomer(customerId);
        return "Order placed for " + customer + ", product " + product + " is in delivery!";
    }

    // Re-save solution case
    // Fixes problem where product is delivered because it saves the previous state back when problem arises
    // Introduces another problem on deletion, where if another transaction removes product after it queried it may save a product which was already
    // delivered successfully
    // Also id will not be the same, it will be incremented but this can be fixed with a generic generator
    @PostMapping("/saveOnError/order/for/{customerId}/product/{productId}")
    @Transactional
    public String orderReSaveOnError(@PathVariable int customerId, @PathVariable int productId) {
        var order = new Order(customerId, productId);
        repository.save(order);
        var product = storageClient.getProduct(productId);
        storageClient.deliver(productId);
        try {
            customerClient.charge(customerId, product.getCost()); // rollback happens here, but storage already delivered the product
        } catch (FeignException e) {
            storageClient.saveProduct(product);
            throw e;
        }
        var customer = customerClient.getCustomer(customerId);
        return "Order placed for " + customer + ", product " + product + " is in delivery!";
    }

    // Two Phase Commit - 2PC
    // It makes reservations (locks basically) so transactions can definitely happen and also checks if other transactions already made reservations
    // Solves problems
    //  - where product can be removed when transaction does not happen
    //  - where in the meantime other transactions modify entities which cause conflict or overwrite, as reserved products cannot be modified
    // if a simple map is used for reservations, then concurrency might be a problem, should use concurrency aware implementations
    // there could be multiple instances of a service so there should not be in-memory maps, but a central one, like store Redis
    @PostMapping("/twoPhaseLock/order/for/{customerId}/product/{productId}")
    @Transactional
    public String orderTwoPhaseLock(@PathVariable int customerId, @PathVariable int productId) {
        var order = new Order(customerId, productId);
        repository.save(order);
        String preparedProductUuid = "";
        try {
            // prepare
            preparedProductUuid = storageClient.prepareDelivery(productId); // must check if not already reserved by other transactions
            var product = storageClient.getProduct(productId);
            // error-prone operation
            customerClient.charge(customerId, product.getCost()); // throws exception, so delivery does not get committed
            // commit
            storageClient.commitDelivery(preparedProductUuid);
            var customer = customerClient.getCustomer(customerId);
            return "Order placed for " + customer + ", product " + product + " is in delivery!";
        } catch (FeignException e) {
            // rollback, these remove in memory reservations
            storageClient.rollbackDelivery(preparedProductUuid);
            throw e;
        }
    }
}
