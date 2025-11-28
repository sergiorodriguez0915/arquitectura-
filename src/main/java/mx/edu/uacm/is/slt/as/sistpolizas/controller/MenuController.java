package mx.edu.uacm.is.slt.as.sistpolizas.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MenuController {
    @GetMapping("/menu")
    public String mostrarMenu() {
        return "menu"; // Spring busca menu.html en /templates
    }

}
