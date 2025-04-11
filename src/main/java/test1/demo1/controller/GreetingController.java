package test1.demo1.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GreetingController {

    @GetMapping("/greeting")  // This maps to http://localhost:8080/greeting
    public String greeting(Model model) {
        model.addAttribute("name", "World");  // Passes "World" to the template
        return "greeting";  // Refers to src/main/resources/templates/greeting.html
    }
}