package mx.edu.uacm.is.slt.as.sistpolizas.controller.web;

import mx.edu.uacm.is.slt.as.sistpolizas.model.Cliente;
import mx.edu.uacm.is.slt.as.sistpolizas.model.Poliza;
import mx.edu.uacm.is.slt.as.sistpolizas.service.ClienteService;
import mx.edu.uacm.is.slt.as.sistpolizas.service.PolizaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequestMapping("/web/polizas")
public class PolizaWebController {

    private final PolizaService polizaService;
    private final ClienteService clienteService;

    public PolizaWebController(PolizaService polizaService,
                               ClienteService clienteService) {
        this.polizaService = polizaService;
        this.clienteService = clienteService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("polizas", polizaService.getAllPolizas());
        model.addAttribute("title", "Pólizas");
        return "polizas";
    }

    @GetMapping("/nuevo")
    public String nuevoForm(Model model) {
        model.addAttribute("poliza", new Poliza());
        model.addAttribute("clientes", clienteService.listarClientes());
        return "poliza-form";
    }

    @PostMapping("/crear")
    public String crear(@ModelAttribute Poliza poliza) {
        polizaService.crearPoliza(
                poliza.getClave(),
                poliza.getTipo(),
                poliza.getMonto(),
                poliza.getDescripcion(),
                poliza.getCliente().getCurp() // CURP del cliente seleccionado
        );
        return "redirect:/web/polizas";
    }

    @GetMapping("/editar/{clave}")
    public String editarForm(@PathVariable UUID clave, Model model) {
        model.addAttribute("poliza", polizaService.getPorClave(clave));
        model.addAttribute("clientes", clienteService.listarClientes());
        return "poliza-form";
    }

    @PostMapping("/actualizar")
    public String actualizar(@ModelAttribute Poliza poliza) {
        polizaService.actualizarPoliza(
                poliza.getClave(),
                poliza.getTipo(),
                poliza.getMonto(),
                poliza.getDescripcion(),
                poliza.getCliente().getCurp()
        );
        return "redirect:/web/polizas";
    }

    @PostMapping("/borrar/{clave}")
    public String borrar(@PathVariable UUID clave) {
        polizaService.borrarPorClave(clave);
        return "redirect:/web/polizas";
    }
}
