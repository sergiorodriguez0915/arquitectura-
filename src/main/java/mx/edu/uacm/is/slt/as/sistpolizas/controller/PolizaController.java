package mx.edu.uacm.is.slt.as.sistpolizas.controller;

import mx.edu.uacm.is.slt.as.sistpolizas.model.Poliza;
import mx.edu.uacm.is.slt.as.sistpolizas.service.PolizaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/polizas")
public class PolizaController {

    private final PolizaService polizaService;

    public PolizaController(PolizaService polizaService) {
        this.polizaService = polizaService;
    }


    @GetMapping
    public String verPolizas(@RequestParam(required = false) String clave,
                             @RequestParam(required = false) String curp,
                             @RequestParam(required = false) String nombre,
                             @RequestParam(required = false) String tipo,
                             @RequestParam(required = false) String nombreBenef,
                             @RequestParam(required = false) String fechaNacBenef,
                             @RequestParam(required = false, defaultValue = "0") Integer pagina,
                             @RequestParam(required = false, defaultValue = "50") Integer tam,
                             Model model) {

        List<Poliza> polizas = polizaService.buscarPolizas(
                clave, curp, nombre, tipo,
                nombreBenef, fechaNacBenef,
                pagina, tam
        );

        model.addAttribute("polizas", polizas);

        model.addAttribute("clave", clave);

        return "polizas";
    }


    @PostMapping("/eliminar")
    public String eliminarPoliza(@RequestParam String clave,
                                 RedirectAttributes redirectAttributes) {
        try {
            UUID id = UUID.fromString(clave);
            polizaService.borrarPorClave(id);
            redirectAttributes.addFlashAttribute("exito", "Póliza eliminada correctamente");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", "Clave inválida");
        }
        return "redirect:/polizas";
    }
}
