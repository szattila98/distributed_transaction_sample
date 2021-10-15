package hu.me.iit.customerservice;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Not enough credits on this customer account!")
public class NotEnoughCreditsException extends RuntimeException {
}
