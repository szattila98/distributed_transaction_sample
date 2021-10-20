package hu.me.iit.monolithic_transactions_sample_app.repository;

import hu.me.iit.monolithic_transactions_sample_app.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Integer> {
}
