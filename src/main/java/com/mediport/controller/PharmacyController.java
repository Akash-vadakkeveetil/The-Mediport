package com.mediport.controller;

import com.mediport.dto.InventoryDto;
import com.mediport.dto.OrderDto;
import com.mediport.entity.Pharmacy;
import com.mediport.entity.User;
import com.mediport.enums.OrderStatus;
import com.mediport.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@Controller
@RequestMapping("/pharmacy")
public class PharmacyController {

    @Autowired
    private PharmacyService pharmacyService;

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    @Autowired
    private SupplierService supplierService;

    private Pharmacy getCurrentPharmacy() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userService.findByUsername(username);
        return pharmacyService.findByUser(user);
    }

    @GetMapping({"/home", "/pharome-details", "/pharome.html"})
    public String home(Model model) {
        try {
            Pharmacy pharmacy = getCurrentPharmacy();
            model.addAttribute("pharmacyDetails", pharmacy);
            model.addAttribute("username", pharmacy.getUser().getUsername());
            return "pharmacy/home";
        } catch (Exception e) {
            return "redirect:/pharprof";
        }
    }

    @GetMapping({"/stock", "/pharstock-details", "/pharstock.html"})
    public String stock(Model model) {
        Pharmacy pharmacy = getCurrentPharmacy();
        model.addAttribute("stockData", inventoryService.getPharmacyStock(pharmacy.getId()));
        model.addAttribute("usernameconst1", pharmacy.getUser().getUsername());
        return "pharmacy/stock";
    }

    @PostMapping("/newstock")
    public String addStock(@RequestParam("med_id") String medId,
                          @RequestParam(value = "descrip", required = false) String description,
                          @RequestParam(value = "price", required = false) BigDecimal price,
                          @RequestParam(value = "min_count", required = false) Integer minCount,
                          @RequestParam("quantity") Integer quantity) {
        Pharmacy pharmacy = getCurrentPharmacy();

        InventoryDto dto = new InventoryDto();
        dto.setMedicineCode(medId);
        dto.setDescription(description);
        dto.setPrice(price);
        dto.setMinimumQuantity(minCount != null ? minCount : 0);
        dto.setCurrentQuantity(quantity);

        inventoryService.addOrUpdateStock(dto, pharmacy.getId());

        return "redirect:/pharmacy/stock";
    }

    @GetMapping({"/order", "/pharorder-details", "/pharorder.html"})
    public String orders(Model model) {
        Pharmacy pharmacy = getCurrentPharmacy();
        model.addAttribute("orderData", orderService.getPharmacyOrders(pharmacy.getId()));
        model.addAttribute("suplistData1", supplierService.getAllAvailableSuppliers());
        model.addAttribute("usernameconst1", pharmacy.getUser().getUsername());
        return "pharmacy/order";
    }

    @PostMapping("/orderstock")
    public String placeOrder(@RequestParam("med_id") String medId,
                            @RequestParam("quantity") Integer quantity,
                            @RequestParam("sup_id") String supId) {
        Pharmacy pharmacy = getCurrentPharmacy();

        OrderDto dto = new OrderDto();
        dto.setMedicineCode(medId);
        dto.setQuantity(quantity);
        dto.setSupplierUsername(supId);

        orderService.placeOrder(dto, pharmacy.getId());

        return "redirect:/pharmacy/order";
    }

    @PostMapping("/orderpharstatus")
    public String updateOrderStatus(@RequestParam("order_id") Long orderId,
                                   @RequestParam("status") String status) {
        if ("received".equalsIgnoreCase(status)) {
            orderService.updateOrderStatus(orderId, OrderStatus.RECEIVED);
        }
        return "redirect:/pharmacy/order";
    }

    @GetMapping({"/message", "/pharmessage-details", "/pharmessage.html"})
    public String messages(Model model) {
        Pharmacy pharmacy = getCurrentPharmacy();
        model.addAttribute("pharmsgData", messageService.getPharmacyMessages(pharmacy.getId()));
        model.addAttribute("usernameconst1", pharmacy.getUser().getUsername());
        return "pharmacy/message";
    }
}
