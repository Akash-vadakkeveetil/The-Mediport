package com.mediport.service;

import com.mediport.dto.OrderDto;
import com.mediport.entity.Medicine;
import com.mediport.entity.Order;
import com.mediport.entity.Pharmacy;
import com.mediport.entity.PharmacyInventory;
import com.mediport.entity.User;
import com.mediport.enums.OrderStatus;
import com.mediport.enums.UserRole;
import com.mediport.repository.MedicineRepository;
import com.mediport.repository.OrderRepository;
import com.mediport.repository.PharmacyInventoryRepository;
import com.mediport.repository.PharmacyRepository;
import com.mediport.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PharmacyRepository pharmacyRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MedicineRepository medicineRepository;

    @Autowired
    private PharmacyInventoryRepository inventoryRepository;

    public Order placeOrder(OrderDto dto, Long pharmacyId) {
        // Validate pharmacy
        Pharmacy pharmacy = pharmacyRepository.findById(pharmacyId)
                .orElseThrow(() -> new RuntimeException("Pharmacy not found"));

        // Validate supplier
        User supplier = userRepository.findByUsername(dto.getSupplierUsername())
                .orElseThrow(() -> new RuntimeException("Supplier not found"));

        if (supplier.getRole() != UserRole.SUPPLIER) {
            throw new RuntimeException("User is not a supplier");
        }

        // Validate medicine
        Medicine medicine = medicineRepository.findByMedicineCode(dto.getMedicineCode())
                .orElseThrow(() -> new RuntimeException("Medicine not found"));

        // Create order
        Order order = new Order();
        order.setPharmacy(pharmacy);
        order.setSupplier(supplier);
        order.setMedicine(medicine);
        order.setQuantity(dto.getQuantity());
        order.setStatus(OrderStatus.NOT_SUPPLIED);
        order.setOrderedDate(LocalDate.now());

        return orderRepository.save(order);
    }

    public List<Order> getPharmacyOrders(Long pharmacyId) {
        return orderRepository.findByPharmacyId(pharmacyId);
    }

    public List<Order> getSupplierOrders(Long supplierId) {
        return orderRepository.findBySupplierId(supplierId);
    }

    public Order updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setStatus(newStatus);

        if (newStatus == OrderStatus.SUPPLIED) {
            order.setSuppliedDate(LocalDate.now());
        } else if (newStatus == OrderStatus.RECEIVED) {
            order.setReceivedDate(LocalDate.now());

            // Update pharmacy inventory
            PharmacyInventory inventory = inventoryRepository
                    .findByPharmacyAndMedicine(order.getPharmacy(), order.getMedicine())
                    .orElseThrow(() -> new RuntimeException("Inventory entry not found"));

            inventory.setCurrentQuantity(inventory.getCurrentQuantity() + order.getQuantity());
            inventory.setLastUpdated(LocalDate.now());
            inventoryRepository.save(inventory);
        }

        return orderRepository.save(order);
    }
}
