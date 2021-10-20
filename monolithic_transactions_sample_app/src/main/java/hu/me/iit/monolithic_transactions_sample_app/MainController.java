package hu.me.iit.monolithic_transactions_sample_app;

import hu.me.iit.monolithic_transactions_sample_app.model.Customer;
import hu.me.iit.monolithic_transactions_sample_app.model.Order;
import hu.me.iit.monolithic_transactions_sample_app.model.Product;
import hu.me.iit.monolithic_transactions_sample_app.repository.CustomerRepository;
import hu.me.iit.monolithic_transactions_sample_app.repository.OrderRepository;
import hu.me.iit.monolithic_transactions_sample_app.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class MainController {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    @PostConstruct
    public void init() {
        customerRepository.save(new Customer(null, "John Doe", 1000));
        productRepository.save(new Product(null, "Affordable Product Many", 10, 50));
        productRepository.save(new Product(null, "Affordable Product One", 10, 1));
        productRepository.save(new Product(null, "Not Affordable Product", 10000, 1));
    }

    @GetMapping("/order")
    public List<Order> getOrders() {
        return orderRepository.findAll();
    }

    @GetMapping("/customer")
    public List<Customer> getCustomers() {
        return customerRepository.findAll();
    }

    @GetMapping("/product")
    public List<Product> getProducts() {
        return productRepository.findAll();
    }

    // there is no chance for inconsistency, if an error happens the transaction rolls back and locking is automatic
    @PostMapping("/order/for/{customerId}/product/{productId}")
    @Transactional
    public Order order(@PathVariable int customerId, @PathVariable int productId) {
        var product = productRepository.findById(productId).orElseThrow();
        var customer = customerRepository.findById(customerId).orElseThrow();
        if (product.getInStock() < 1) throw new RuntimeException("Not enough products!");
        product.setInStock(product.getInStock() - 1);
        productRepository.save(product);
        if (customer.getCredits() < product.getCost()) throw new RuntimeException("Not enough credits!");
        customer.setCredits(customer.getCredits() - product.getCost());
        customerRepository.save(customer);
        return orderRepository.save(new Order(null, customer, product));
    }

}
