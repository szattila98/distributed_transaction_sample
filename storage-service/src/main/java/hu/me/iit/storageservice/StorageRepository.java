package hu.me.iit.storageservice;

import org.springframework.data.jpa.repository.JpaRepository;

public interface StorageRepository extends JpaRepository<Product, Integer> {
}
