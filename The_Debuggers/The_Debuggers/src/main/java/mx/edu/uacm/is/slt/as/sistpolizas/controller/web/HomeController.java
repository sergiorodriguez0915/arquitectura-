package mx.edu.uacm.is.slt.as.sistpolizas.controller.web;

import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping({"/", "/index", "/web"})
    public String index(Model model) {
        model.addAttribute("title", "Dashboard");
        model.addAttribute("appName", "Sistema de Pólizas");
        return "index";
    }
}
