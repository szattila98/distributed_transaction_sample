package hu.me.iit.storageservice;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class StorageController {

    private final StorageRepository repository;

    @PostConstruct
    public void init() {
        repository.save(new Product(1, "Thingy A", 20));
        repository.save(new Product(2, "Thingy B", 1500));
        repository.save(new Product(3, "Thingy C", 2));
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

}
