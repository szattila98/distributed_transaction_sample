package hu.me.iit.orderservice;

import feign.FeignException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class MainControllerAdvice {

    @ExceptionHandler(FeignException.class)
    public String handle(FeignException e) {
        return "Exception occurred, not everything is running or not enough credits or missing product!";
    }
}
