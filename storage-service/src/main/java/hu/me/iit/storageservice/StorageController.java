package hu.me.iit.storageservice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@RestController
@RequiredArgsConstructor
public class StorageController {

    private final StorageRepository repository;
    private final ConcurrentMap<String, Integer> reservedProducts = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        repository.save(new Product(null, "Affordable Product Many", 10, 50));
        repository.save(new Product(null, "Affordable Product One", 10, 1));
                repository.save(new Product(null, "Not Affordable Product A", 10000, 1));
        repository.save(new Product(null, "Not Affordable Product B", 10000, 1));
        repository.save(new Product(null, "Not Affordable Product C", 10000, 1));
    }

    @GetMapping("/product/{productId}")
    public Product getProduct(@PathVariable int productId) {
        return repository.findById(productId).orElseThrow();
    }

    @GetMapping
    public List<Product> products() {
        return repository.findAll();
    }

    @PostMapping("/save")
    public Product saveProduct(@RequestBody Product product) {
        return repository.save(product);
    }

    @PostMapping("/deliver/{productId}")
    public Product deliver(@PathVariable int productId) {
        var product = getProduct(productId);
        if (product.getInStock() <= 0) throw new NoSuchElementException("No product left in stock");
        product.deliver();
        return repository.save(product);
    }

    // reserves the product, essentially locks it so others cannot modify it
    @PostMapping("/prepare/delivery/of/{productId}")
    public String prepareDelivery(@PathVariable int productId) {
        String uuid = UUID.randomUUID().toString();
        var opt = repository.findById(productId);
        if (opt.isPresent()) {
            var product = opt.get();
            long preparedProductIdCount = reservedProducts.values().stream().filter(x -> x == productId).count();
            if (product.getInStock() <= 0 || product.getInStock() <= preparedProductIdCount) {
                throw new NoSuchElementException("No products left in stock!");
            }
            reservedProducts.put(uuid, productId);
            return uuid;
        }
        throw new NoSuchElementException("No product with this ID found!");
    }

    // commits the delivery, so it makes the operation it was preparing for, and removes the product from the reservations
    @PostMapping("/commit/delivery/of/{preparedProductUuid}")
    public void commitDelivery(@PathVariable String preparedProductUuid) {
        if (reservedProducts.containsKey(preparedProductUuid)) {
            int productId = reservedProducts.get(preparedProductUuid);
            var product = repository.findById(productId).orElseThrow();
            product.deliver();
            repository.save(product);
            reservedProducts.remove(preparedProductUuid);
            return;
        }
        throw new NoSuchElementException("This product is not reserved, something has gone very wrong!");
    }

    // rolls back reservation, it deletes it from the map
    @PostMapping("/rollback/delivery/of/{preparedProductUuid}")
    public void rollbackDelivery(@PathVariable String preparedProductUuid) {
        reservedProducts.remove(preparedProductUuid);
    }

    @GetMapping("/reserved")
    public Map<String, Integer> listReservedProducts() {
        return reservedProducts;
    }
}
