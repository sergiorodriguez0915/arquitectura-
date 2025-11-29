package mx.edu.uacm.is.slt.as.sistpolizas.controller.web;

import mx.edu.uacm.is.slt.as.sistpolizas.model.Beneficiario;
import mx.edu.uacm.is.slt.as.sistpolizas.service.BeneficiarioService;
import mx.edu.uacm.is.slt.as.sistpolizas.service.PolizaService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
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

    // ==============================
    // Listar todos los beneficiarios
    // ==============================
    @GetMapping
    public String list(Model model) {
        List<Beneficiario> todos = beneficiarioService.obtenerTodos();
        model.addAttribute("beneficiarios", todos);
        model.addAttribute("title", "Beneficiarios");
        return "beneficiarios";
    }

    // ==============================
    // Formulario nuevo beneficiario
    // ==============================
    @GetMapping("/nuevo")
    public String nuevoForm(Model model) {
        model.addAttribute("beneficiario", new Beneficiario());
        model.addAttribute("polizas", polizaService.buscarPolizas(null, null, null, null, null, null, 0, 20));
        model.addAttribute("title", "Nuevo Beneficiario");
        return "beneficiario-form";
    }

    // ==============================
    // Crear beneficiario
    // ==============================
    @PostMapping("/crear")
    public String crear(@RequestParam UUID clavePoliza,
                        @RequestParam String nombres,
                        @RequestParam String primerApellido,
                        @RequestParam(required = false) String segundoApellido,
                        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaNacimiento,
                        @RequestParam int porcentaje) {

        beneficiarioService.crearBeneficiario(clavePoliza, nombres, primerApellido, segundoApellido, fechaNacimiento, porcentaje);
        return "redirect:/web/beneficiarios";
    }

    // ==============================
    // Formulario para editar beneficiario
    // ==============================
    @GetMapping("/editar/{clavePoliza}/{nombres}/{primerApellido}/{segundoApellido}/{fecha}")
    public String editarForm(@PathVariable UUID clavePoliza,
                             @PathVariable String nombres,
                             @PathVariable String primerApellido,
                             @PathVariable String segundoApellido,
                             @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date fecha,
                             Model model) {

        Beneficiario b = beneficiarioService.obtenerPorId(clavePoliza, nombres, primerApellido, segundoApellido, fecha)
                .orElseThrow(() -> new IllegalArgumentException("Beneficiario no encontrado"));

        model.addAttribute("beneficiario", b);
        model.addAttribute("polizas", polizaService.buscarPolizas(null, null, null, null, null, null, 0, 20));
        model.addAttribute("title", "Editar Beneficiario");
        return "beneficiario-form";
    }

    // ==============================
    // Actualizar beneficiario
    // ==============================
    @PostMapping("/actualizar")
    public String actualizar(@RequestParam UUID clavePoliza,
                             @RequestParam String nombres,
                             @RequestParam String primerApellido,
                             @RequestParam(required = false) String segundoApellido,
                             @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaNacimiento,
                             @RequestParam int porcentaje) {

        beneficiarioService.actualizarBeneficiario(clavePoliza, nombres, primerApellido, segundoApellido, fechaNacimiento, porcentaje);
        return "redirect:/web/beneficiarios";
    }


}