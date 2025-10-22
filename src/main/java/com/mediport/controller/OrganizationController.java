package com.mediport.controller;

import com.mediport.entity.Pharmacy;
import com.mediport.entity.User;
import com.mediport.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/organization")
public class OrganizationController {

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private PharmacyService pharmacyService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    @GetMapping({"/home", "/organization-details", "/orgome.html"})
    public String home(Model model) {
        model.addAttribute("organData", inventoryService.getAllLowStockItems());
        return "organization/home";
    }

    @PostMapping("/changemin")
    public String updateMinimum(@RequestParam("username") String username,
                               @RequestParam("med_id") String medId,
                               @RequestParam("min_count") Integer minCount) {
        User user = userService.findByUsername(username);
        Pharmacy pharmacy = pharmacyService.findByUser(user);

        inventoryService.updateMinimumQuantity(pharmacy.getId(), medId, minCount);

        return "redirect:/organization/home";
    }

    @GetMapping({"/view", "/orgview-details", "/orgview.html"})
    public String view(Model model, HttpSession session) {
        model.addAttribute("pharmacyData", pharmacyService.findAllPharmacies());

        Long selectedPharmacyId = (Long) session.getAttribute("selectedPharmacyId");
        if (selectedPharmacyId != null) {
            model.addAttribute("orgstockData", inventoryService.getPharmacyStock(selectedPharmacyId));
        }

        return "organization/view";
    }

    @PostMapping("/orgshowstock")
    public String showStock(@RequestParam("username") String username, HttpSession session) {
        User user = userService.findByUsername(username);
        Pharmacy pharmacy = pharmacyService.findByUser(user);

        session.setAttribute("selectedPharmacyId", pharmacy.getId());

        return "redirect:/organization/view";
    }

    @GetMapping({"/order", "/orgorder-details", "/orgorder.html"})
    public String orders(Model model, HttpSession session) {
        Long selectedPharmacyId = (Long) session.getAttribute("selectedPharmacyId");
        if (selectedPharmacyId != null) {
            model.addAttribute("orgorderData", orderService.getPharmacyOrders(selectedPharmacyId));
        }

        return "organization/order";
    }

    @PostMapping("/orgshoworder")
    public String showOrders(@RequestParam("username") String username, HttpSession session) {
        User user = userService.findByUsername(username);
        Pharmacy pharmacy = pharmacyService.findByUser(user);

        session.setAttribute("selectedPharmacyId", pharmacy.getId());

        return "redirect:/organization/order";
    }

    @GetMapping({"/message", "/orgmessage-details", "/orgmessage.html"})
    public String messages(Model model) {
        model.addAttribute("orgmsgData", messageService.getAllMessages());
        return "organization/message";
    }

    @PostMapping("/orgmsg")
    public String sendMessage(@RequestParam("username") String username,
                             @RequestParam("med_id") String medId,
                             @RequestParam("msg") String msg) {
        User user = userService.findByUsername(username);
        Pharmacy pharmacy = pharmacyService.findByUser(user);

        messageService.sendMessage(pharmacy.getId(), medId, msg);

        return "redirect:/organization/message";
    }
}
