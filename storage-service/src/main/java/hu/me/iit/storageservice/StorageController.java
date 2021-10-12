package hu.me.iit.storageservice;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@RestController
public class StorageController {

    private final List<Product> products = new ArrayList<>();

    @PostConstruct
    public void init() {
        products.add(new Product(1, "Thingy A", 20));
        products.add(new Product(2, "Thingy B", 1500));
        products.add(new Product(3, "Thingy C", 2));
    }

    @GetMapping("/product/{productId}")
    public Product getProduct(@PathVariable int productId) {
        return products.stream().filter(product -> product.getId() == productId).findAny().orElseThrow();
    }

    @GetMapping
    public List<Product> products() {
        return products;
    }

    @PostMapping("/deliver/{productId}")
    public Product deliver(@PathVariable int productId) {
        var product = getProduct(productId);
        products.remove(product);
        return product;
    }

}
