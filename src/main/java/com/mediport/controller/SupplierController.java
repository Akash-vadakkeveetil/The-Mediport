package com.mediport.controller;

import com.mediport.entity.User;
import com.mediport.enums.Availability;
import com.mediport.enums.OrderStatus;
import com.mediport.service.OrderService;
import com.mediport.service.PharmacyService;
import com.mediport.service.SupplierService;
import com.mediport.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/supplier")
public class SupplierController {

    @Autowired
    private SupplierService supplierService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private PharmacyService pharmacyService;

    @Autowired
    private UserService userService;

    private User getCurrentSupplier() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        return userService.findByUsername(username);
    }

    @GetMapping({"/home", "/supplier-details", "/supome.html"})
    public String home(Model model) {
        User supplier = getCurrentSupplier();
        model.addAttribute("supplierData", orderService.getSupplierOrders(supplier.getId()));
        model.addAttribute("pharmacyData", pharmacyService.findAllPharmacies());
        model.addAttribute("supplierconst1", supplier.getUsername());
        return "supplier/home";
    }

    @PostMapping("/ordersupstatus")
    public String updateOrderStatus(@RequestParam("order_id") Long orderId,
                                   @RequestParam("status") String status) {
        if ("supplied".equalsIgnoreCase(status)) {
            orderService.updateOrderStatus(orderId, OrderStatus.SUPPLIED);
        }
        return "redirect:/supplier/home";
    }

    @GetMapping({"/list", "/suplist-details", "/suplist.html"})
    public String catalog(Model model) {
        User supplier = getCurrentSupplier();
        model.addAttribute("suplistData", supplierService.getSupplierCatalog(supplier.getId()));
        model.addAttribute("supplierconst1", supplier.getUsername());
        return "supplier/list";
    }

    @PostMapping("/listsup")
    public String addCatalogItem(@RequestParam("med_id") String medId,
                                @RequestParam(value = "descrip", required = false) String description,
                                @RequestParam("availability") String availability) {
        User supplier = getCurrentSupplier();

        Availability avail = "available".equalsIgnoreCase(availability) ?
                Availability.AVAILABLE : Availability.NOT_AVAILABLE;

        supplierService.addOrUpdateCatalogItem(supplier.getId(), medId, description, avail);

        return "redirect:/supplier/list";
    }
}
