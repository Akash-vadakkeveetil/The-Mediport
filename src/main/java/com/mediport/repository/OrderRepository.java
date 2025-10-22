package com.mediport.repository;

import com.mediport.entity.Order;
import com.mediport.entity.Pharmacy;
import com.mediport.entity.User;
import com.mediport.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByPharmacy(Pharmacy pharmacy);

    List<Order> findByPharmacyId(Long pharmacyId);

    List<Order> findBySupplier(User supplier);

    List<Order> findBySupplierId(Long supplierId);

    List<Order> findByStatus(OrderStatus status);

    List<Order> findByPharmacyAndStatus(Pharmacy pharmacy, OrderStatus status);
}
