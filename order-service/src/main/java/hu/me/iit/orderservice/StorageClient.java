package hu.me.iit.orderservice;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient("storage-service")
public interface StorageClient {

    @GetMapping("/product/{productId}")
    Product getProduct(@PathVariable int productId);

    @PostMapping("/deliver/{productId}")
    Product deliver(@PathVariable int productId);
}
