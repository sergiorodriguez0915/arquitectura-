package mx.edu.uacm.is.slt.as.sistpolizas.controller.web;

import mx.edu.uacm.is.slt.as.sistpolizas.model.Poliza;
import mx.edu.uacm.is.slt.as.sistpolizas.service.ClienteService;
import mx.edu.uacm.is.slt.as.sistpolizas.service.PolizaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/web/polizas")
public class PolizaWebController {

    private final PolizaService polizaService;
    private final ClienteService clienteService;

    public PolizaWebController(PolizaService polizaService, ClienteService clienteService) {
        this.polizaService = polizaService;
        this.clienteService = clienteService;
    }

    // ======================
    // Listar y buscar pólizas
    // ======================
    @GetMapping
    public String list(@RequestParam(required = false) String clave,
                       @RequestParam(required = false) String curp,
                       @RequestParam(required = false) String nombre,
                       @RequestParam(required = false) String tipo,
                       @RequestParam(required = false) String beneficiario,
                       @RequestParam(required = false) String fechaNacBenef,
                       @RequestParam(required = false) Integer pagina,
                       @RequestParam(required = false) Integer tam,
                       Model model) {

        List<Poliza> polizas = polizaService.buscarPolizas(
                clave, curp, nombre, tipo,
                beneficiario, fechaNacBenef, pagina, tam
        );

        if (polizas == null) polizas = new ArrayList<>();

        model.addAttribute("polizas", polizas);
        model.addAttribute("clientes", clienteService.listarClientes());
        model.addAttribute("title", "Pólizas");
        model.addAttribute("nuevaPoliza", new Poliza());
        return "polizas";
    }

    // ======================
    // Crear póliza
    // ======================
    @PostMapping("/crear")
    public String crear(@ModelAttribute("nuevaPoliza") Poliza poliza) {
        if (poliza.getClave() == null) poliza.setClave(UUID.randomUUID());
        polizaService.guardarPoliza(poliza);
        return "redirect:/web/polizas";
    }

    // ======================
    // Editar póliza
    // ======================
    @PostMapping("/actualizar")
    public String actualizar(@ModelAttribute Poliza poliza) {
        Poliza existente = polizaService.obtenerPorClave(poliza.getClave());
        if (existente != null) {
            existente.setTipo(poliza.getTipo());
            existente.setDescripcion(poliza.getDescripcion());
            existente.setMonto(poliza.getMonto());
            existente.setCliente(poliza.getCliente());
            polizaService.guardarPoliza(existente); // guardar cambios
        }
        return "redirect:/web/polizas";
    }

    // ======================
    // Eliminar póliza
    // ======================
    @PostMapping("/borrar")
    public String borrar(@RequestParam UUID clave) {
        polizaService.borrarPorClave(clave);
        return "redirect:/web/polizas";
    }
}