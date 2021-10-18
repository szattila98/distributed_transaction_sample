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
        repository.save(new Product(0, "Affordable product 1", 20));
        repository.save(new Product(0, "Affordable product 2", 30));
        repository.save(new Product(0, "Affordable product 3", 40));
        repository.save(new Product(0, "Affordable product 4", 50));
        repository.save(new Product(0, "Affordable product 5", 60));
        repository.save(new Product(0, "Not affordable product 1", 1500));
        repository.save(new Product(0, "Not affordable product 2", 2000));
        repository.save(new Product(0, "Not affordable product 3", 1500));
        repository.save(new Product(0, "Not affordable product 4", 2000));
        repository.save(new Product(0, "Not affordable product 5", 1500));
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
        repository.delete(product);
        return product;
    }

    @PostMapping("/prepare/delivery/of/{productId}")
    public String prepareDelivery(@PathVariable int productId) {
        String uuid = UUID.randomUUID().toString();
        if (repository.existsById(productId)) {
            if (reservedProducts.containsValue(productId)) {
                throw new NoSuchElementException("This product is already reserved, as such it does not exists!");
            }
            reservedProducts.put(uuid, productId);
            return uuid;
        }
        throw new NoSuchElementException("No product with this ID found!");
    }

    @PostMapping("/commit/delivery/of/{preparedProductUuid}")
    public void commitDelivery(@PathVariable String preparedProductUuid) {
        if (reservedProducts.containsKey(preparedProductUuid)) {
            int productId = reservedProducts.get(preparedProductUuid);
            repository.deleteById(productId);
            reservedProducts.remove(preparedProductUuid);
            return;
        }
        throw new NoSuchElementException("This product is not reserved, something has gone very wrong!");
    }

    @PostMapping("/rollback/delivery/of/{preparedProductUuid}")
    public void rollbackDelivery(@PathVariable String preparedProductUuid) {
        reservedProducts.remove(preparedProductUuid);
    }

    @GetMapping("/reserved")
    public Map<String, Integer> listReservedProducts() {
        return reservedProducts;
    }
}
