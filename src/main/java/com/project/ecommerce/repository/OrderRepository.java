package com.project.ecommerce.repository;
import com.project.ecommerce.model.Order;
import com.project.ecommerce.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);
}
