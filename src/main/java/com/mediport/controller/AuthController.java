package com.mediport.controller;

import com.mediport.dto.PharmacyProfileDto;
import com.mediport.dto.UserRegistrationDto;
import com.mediport.entity.User;
import com.mediport.enums.UserRole;
import com.mediport.service.PharmacyService;
import com.mediport.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private PharmacyService pharmacyService;

    @GetMapping({"/", "/login"})
    public String loginPage(@RequestParam(required = false) String error,
                           @RequestParam(required = false) String logout,
                           Model model) {
        if (error != null) {
            model.addAttribute("error", "Invalid username or password");
        }
        if (logout != null) {
            model.addAttribute("message", "You have been logged out");
        }
        return "login";
    }

    @GetMapping("/signup")
    public String signupPage(Model model) {
        return "signup";
    }

    @PostMapping("/signup")
    public String processSignup(@RequestParam String username,
                               @RequestParam(required = false) String email,
                               @RequestParam String password,
                               @RequestParam String category,
                               RedirectAttributes redirectAttributes) {
        try {
            // Check if username or email exists
            if (userService.existsByUsername(username)) {
                redirectAttributes.addFlashAttribute("error", "Username already exists");
                return "redirect:/signup";
            }

            if (email != null && !email.isEmpty() && userService.existsByEmail(email)) {
                redirectAttributes.addFlashAttribute("error", "Email already exists");
                return "redirect:/signup";
            }

            // Create user
            UserRegistrationDto dto = new UserRegistrationDto();
            dto.setUsername(username);
            dto.setEmail(email);
            dto.setPassword(password);
            dto.setRole(UserRole.valueOf(category.toUpperCase()));

            userService.registerUser(dto);

            // Redirect based on role
            if (dto.getRole() == UserRole.PHARMACY) {
                return "redirect:/pharprof";
            } else {
                return "redirect:/login";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/signup";
        }
    }

    @GetMapping("/pharprof")
    public String pharmacyProfilePage(Model model) {
        return "pharprof";
    }

    @PostMapping("/pharprof")
    public String savePharmacyProfile(@RequestParam String pharmacyname,
                                     @RequestParam String location,
                                     @RequestParam Integer pinno,
                                     @RequestParam Long contactnumber,
                                     @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate established,
                                     RedirectAttributes redirectAttributes) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            User user = userService.findByUsername(username);

            PharmacyProfileDto dto = new PharmacyProfileDto();
            dto.setPharmacyName(pharmacyname);
            dto.setLocation(location);
            dto.setPinCode(pinno);
            dto.setContactNumber(contactnumber);
            dto.setEstablishedDate(established);

            pharmacyService.createPharmacyProfile(dto, user);

            return "redirect:/pharmacy/home";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/pharprof";
        }
    }
}
