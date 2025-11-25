package mx.edu.uacm.is.slt.as.sistpolizas.controller.web;

import mx.edu.uacm.is.slt.as.sistpolizas.model.Beneficiario;
import mx.edu.uacm.is.slt.as.sistpolizas.service.BeneficiarioService;
import mx.edu.uacm.is.slt.as.sistpolizas.service.PolizaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequestMapping("/web/beneficiarios")
public class BeneficiarioWebController {

    private final BeneficiarioService beneficiarioService;
    private final PolizaService polizaService;

    public BeneficiarioWebController(BeneficiarioService beneficiarioService,
                                     PolizaService polizaService) {
        this.beneficiarioService = beneficiarioService;
        this.polizaService = polizaService;
    }

    // ================================
    // Listar todos los beneficiarios
    // ================================
    @GetMapping
    public String list(Model model) {
        model.addAttribute("beneficiarios", beneficiarioService.obtenerTodos());
        model.addAttribute("title", "Beneficiarios");
        return "beneficiarios";
    }

    // ====================================
    // Formulario para crear nuevo beneficiario
    // ====================================
    @GetMapping("/nuevo")
    public String nuevoForm(Model model) {
        model.addAttribute("beneficiario", new Beneficiario());
        model.addAttribute("polizas", polizaService.obtenerTodasCombinadas()); // CORREGIDO
        model.addAttribute("title", "Nuevo Beneficiario");
        return "beneficiario-form";
    }

    // ================================
    // Guardar nuevo beneficiario
    // ================================
    @PostMapping("/crear")
    public String crear(@RequestParam String fechaNacimiento,
                        @RequestParam UUID clavePoliza,
                        @RequestParam String nombres,
                        @RequestParam String primerApellido,
                        @RequestParam(required = false) String segundoApellido,
                        @RequestParam int porcentaje) {

        beneficiarioService.crearBeneficiario(
                fechaNacimiento,
                clavePoliza,
                nombres,
                primerApellido,
                segundoApellido,
                porcentaje
        );

        return "redirect:/web/beneficiarios";
    }

    // ====================================
    // Formulario para editar beneficiario
    // ====================================
    @GetMapping("/editar/{id}/{p}")
    public String editarForm(@PathVariable String id,
                             @PathVariable UUID p,
                             Model model) {

        Beneficiario beneficiario = beneficiarioService.obtenerPorId(id, p)
                .orElseThrow(() -> new IllegalArgumentException("Beneficiario no encontrado"));

        model.addAttribute("beneficiario", beneficiario);
        model.addAttribute("polizas", polizaService.obtenerTodasCombinadas()); // CORREGIDO
        model.addAttribute("contentFragment", "beneficiario-form :: beneficiarioFormContent");
        model.addAttribute("title", "Editar Beneficiario");

        return "fragments/layout";
    }

    // ================================
    // Actualizar beneficiario
    // ================================
    @PostMapping("/actualizar")
    public String actualizar(@RequestParam String curp,
                             @RequestParam UUID clavePoliza,
                             @RequestParam String nombres,
                             @RequestParam String primerApellido,
                             @RequestParam(required = false) String segundoApellido,
                             @RequestParam int porcentaje) {

        beneficiarioService.actualizarBeneficiario(
                curp,
                clavePoliza,
                nombres,
                primerApellido,
                segundoApellido,
                porcentaje
        );

        return "redirect:/web/beneficiarios";
    }

    // ================================
    // Eliminar beneficiario
    // ================================
    @GetMapping("/eliminar/{id}/{p}")
    public String eliminar(@PathVariable String id,
                           @PathVariable UUID p) {
        beneficiarioService.borrarBeneficiario(id, p);
        return "redirect:/web/beneficiarios";
    }
}
