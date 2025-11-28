package mx.edu.uacm.is.slt.as.sistpolizas.controller.web;

import mx.edu.uacm.is.slt.as.sistpolizas.model.Cliente;
import mx.edu.uacm.is.slt.as.sistpolizas.model.Poliza;
import mx.edu.uacm.is.slt.as.sistpolizas.repository.ClienteRepository;
import mx.edu.uacm.is.slt.as.sistpolizas.service.ClienteService;
import mx.edu.uacm.is.slt.as.sistpolizas.service.PolizaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/web/polizas")
public class PolizaWebController {

    private final PolizaService polizaService;
    private final ClienteService clienteService;

    @Autowired
    private ClienteRepository clienteRepositorio;


    public PolizaWebController(PolizaService polizaService, ClienteService clienteService) {
        this.polizaService = polizaService;
        this.clienteService = clienteService;
    }

    // ======================
    // Listar y buscar pólizas (LÓGICA AJUSTADA para SELECT/INPUT)
    // ======================
    @GetMapping
    public String list(
            @RequestParam(required = false) String filtro,
            @RequestParam(required = false) String valor,

            @RequestParam(required = false, defaultValue = "0") Integer pagina,
            @RequestParam(required = false, defaultValue = "50") Integer tam,
            Model model) {

        String clave = null;
        String curp = null;
        String nombre = null;
        String tipo = null;
        String nombreBeneficiario = null;
        String fechaN = null;

        if (valor != null && !valor.isBlank()) {
            switch (filtro) {
                case "clave": clave = valor; break;
                case "curp": curp = valor; break;
                case "nombre": nombre = valor; break;
                case "tipo": tipo = valor; break;
                case "nombreBeneficiario": nombreBeneficiario = valor; break;
                case "fechaN": fechaN = valor; break;
            }
        }

        List<Poliza> polizas = polizaService.buscarPolizas(
                clave, curp, nombre, tipo,
                nombreBeneficiario, fechaN, pagina, tam
        );

        if (polizas == null) polizas = new ArrayList<>();

        model.addAttribute("polizas", polizas);
        model.addAttribute("clientes", clienteService.listarClientes());
        model.addAttribute("title", "Pólizas");
        model.addAttribute("nuevaPoliza", new Poliza());

        model.addAttribute("filtro", filtro);
        model.addAttribute("valor", valor);

        return "polizas";
    }


    @PostMapping("/crear")
    public String crear(@ModelAttribute("nuevaPoliza") Poliza poliza,
                        @RequestParam("cliente") String curpCliente) {

        if (poliza.getClave() == null) {
            poliza.setClave(UUID.randomUUID());
        }

        // Buscar el cliente por CURP
        clienteRepositorio.findByCurp(curpCliente).ifPresent(poliza::setCliente);

        polizaService.crearPoliza(poliza, null); // Beneficiarios los puedes pasar si los manejas en el form

        return "redirect:/web/polizas";
    }




    @PostMapping("/actualizar")
    public String actualizar(@ModelAttribute Poliza poliza) {
        Poliza existente = polizaService.buscarPolizas(
                poliza.getClave().toString(),
                null, null, null,
                null, null, 0, 1
        ).stream().findFirst().orElse(null);

        if (existente != null) {
            existente.setTipo(poliza.getTipo());
            existente.setDescripcion(poliza.getDescripcion());
            existente.setMonto(poliza.getMonto());
            existente.setCliente(poliza.getCliente());

            polizaService.actualizarPoliza(existente, null); // null si no hay beneficiarios por ahora
        }
        return "redirect:/web/polizas";
    }


    @PostMapping("/borrar")
    public String borrar(@RequestParam UUID clave) {
        polizaService.borrarPorClave(clave);
        return "redirect:/web/polizas";
    }
}