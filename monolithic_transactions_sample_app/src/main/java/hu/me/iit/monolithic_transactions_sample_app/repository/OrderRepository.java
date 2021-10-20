package hu.me.iit.monolithic_transactions_sample_app.repository;

import hu.me.iit.monolithic_transactions_sample_app.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Integer> {
}
