package hu.me.iit.monolithic_transactions_sample_app.repository;

import hu.me.iit.monolithic_transactions_sample_app.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {
}
