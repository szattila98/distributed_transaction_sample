package hu.me.iit.orderservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("storage-service")
public interface StorageClient {

    @GetMapping("/product/{productId}")
    Product getProduct(@PathVariable int productId);

    @PostMapping("/save")
    Product saveProduct(@RequestBody Product product);

    @PostMapping("/deliver/{productId}")
    Product deliver(@PathVariable int productId);

    @PostMapping("/prepare/delivery/of/{productId}")
    String prepareDelivery(@PathVariable int productId);

    @PostMapping("/commit/delivery/of/{preparedProductUuid}")
    void commitDelivery(@PathVariable String preparedProductUuid);

    @PostMapping("/rollback/delivery/of/{preparedProductUuid}")
    void rollbackDelivery(@PathVariable String preparedProductUuid);
}
