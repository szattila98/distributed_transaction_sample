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
        products.add(new Product(1, "Thingy A"));
        products.add(new Product(2, "Thingy B"));
        products.add(new Product(3, "Thingy C"));
    }

    @GetMapping("/product/{productId}")
    public Product getProduct(@PathVariable int productId) {
        return products.stream().filter(product -> product.getId() == productId).findAny().orElseThrow();
    }

    @PostMapping("/deliver/{productId}")
    public void deliver(@PathVariable int productId) {
        products.remove(getProduct(productId));
    }

}
