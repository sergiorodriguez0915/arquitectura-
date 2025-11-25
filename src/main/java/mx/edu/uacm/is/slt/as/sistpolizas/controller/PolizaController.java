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

    // ==========================================
    // Redirección del formulario /polizas/buscar
    // ==========================================
    @GetMapping("/buscar")
    public String buscar(@RequestParam String filtro,
                         @RequestParam String valor) {

        return "redirect:/polizas?filtro=" + filtro + "&valor=" + valor;
    }

    // ==========================================
    // Ver todas / búsqueda con filtros
    // ==========================================
    @GetMapping
    public String verPolizas(@RequestParam(required = false) String filtro,
                             @RequestParam(required = false) String valor,
                             Model model) {

        List<Poliza> polizas;

        if (filtro != null && valor != null && !valor.isEmpty()) {
            switch (filtro) {
                case "clave" -> polizas = polizaService.buscarPorClave(valor);
                case "curp" -> polizas = polizaService.buscarPorCurp(valor);
                case "nombre" -> polizas = polizaService.buscarPorNombre(valor);
                case "tipo" -> polizas = polizaService.buscarPorTipo(valor);
                default -> polizas = polizaService.listarPolizas();
            }
        } else {
            polizas = polizaService.listarPolizas();
        }

        model.addAttribute("polizas", polizas);
        model.addAttribute("filtro", filtro);
        model.addAttribute("valor", valor);

        return "polizas";
    }

    // ==========================================
    // Eliminar póliza
    // ==========================================
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
