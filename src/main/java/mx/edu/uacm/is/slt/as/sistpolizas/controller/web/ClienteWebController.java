package mx.edu.uacm.is.slt.as.sistpolizas.controller.web;

import mx.edu.uacm.is.slt.as.sistpolizas.model.Cliente;
import mx.edu.uacm.is.slt.as.sistpolizas.service.ClienteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/web/clientes")
public class ClienteWebController {

    private final ClienteService clienteService;

    public ClienteWebController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping
    public String list(Model model) {

        // 🔥 1. Obtener y sincronizar clientes desde el sistema dueño
        clienteService.obtenerClientesSistemaDueno();

        // 🔥 2. Ya sincronizados, los obtienes de tu BD local
        model.addAttribute("title", "Clientes");
        model.addAttribute("clientes", clienteService.listarClientes());

        return "clientes";
    }


    @GetMapping("/nuevo")
    public String nuevoForm(Model model) {
        model.addAttribute("cliente", new Cliente());
        return "cliente-form";
    }

    @PostMapping("/crear")
    public String crear(@ModelAttribute Cliente cliente,
                        @RequestParam String fechaNacimiento) {

        clienteService.crearCliente(
                cliente.getCurp(),
                cliente.getDireccion(),
                fechaNacimiento,
                cliente.getNombres(),
                cliente.getPrimerApellido(),
                cliente.getSegundoApellido()
        );

        return "redirect:/web/clientes";
    }

    @GetMapping("/buscar")
    public String buscarPorCurp(@RequestParam String curp, Model model) {
        model.addAttribute("clienteEncontrado", clienteService.obtenerCliente(curp));
        model.addAttribute("clientes", clienteService.listarClientes());
        return "clientes";
    }
}
