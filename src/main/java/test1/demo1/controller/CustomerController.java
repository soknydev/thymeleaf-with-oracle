package test1.demo1.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import test1.demo1.service.CustomerService;

@Controller
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping("")
    public String viewCustomers(Model model) {
        model.addAttribute("customers", customerService.getAllCustomers());
        return "customers"; // Assuming you have a Thymeleaf template named 'customers.html'
    }

}
