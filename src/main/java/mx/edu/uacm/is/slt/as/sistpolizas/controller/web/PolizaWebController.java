package mx.edu.uacm.is.slt.as.sistpolizas.controller.web;

import mx.edu.uacm.is.slt.as.sistpolizas.model.Poliza; // Importa la clase Poliza
import mx.edu.uacm.is.slt.as.sistpolizas.service.ClienteService;
import mx.edu.uacm.is.slt.as.sistpolizas.service.PolizaService;
import org.springframework.stereotype.Controller; // Cambiado a Controller
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;  // binde todas las anotaciones web

import java.util.ArrayList; // Importa la clase ArrayList
import java.util.List;   // Importa la clase List
import java.util.UUID;  // Importa la clase UUID

@Controller
@RequestMapping("/web/polizas")  //endpoint base para vistas web
public class PolizaWebController {

    private final PolizaService polizaService;  // Servicio de pólizas
    private final ClienteService clienteService;  // Servicio de clientes

    public PolizaWebController(PolizaService polizaService, ClienteService clienteService) {
        this.polizaService = polizaService;
        this.clienteService = clienteService;
    }

    @GetMapping
    public String list( // Obtener lista de pólizas con filtros
            @RequestParam(required = false) String filtro,
            @RequestParam(required = false) String valor,
            @RequestParam(required = false) String nombreBenef,
            @RequestParam(required = false) String fechaNacBenef,
            @RequestParam(required = false, defaultValue = "0") Integer pagina,
            @RequestParam(required = false, defaultValue = "50") Integer tam,
            Model model) {

        String clave = null;
        String curp = null;
        String nombre = null;
        String tipo = null;

        if (valor != null && !valor.isBlank()) {  // Asignar valor al filtro correspondiente
            switch (filtro) {  // switch para determinar el filtro
                case "clave": clave = valor; break;
                case "curp": curp = valor; break;
                case "nombre": nombre = valor; break;
                case "tipo": tipo = valor; break;
            }
        }

        List<Poliza> polizas = polizaService.buscarPolizas(  // Buscar pólizas usando los filtros
                clave, curp, nombre, tipo,
                nombreBenef, fechaNacBenef, pagina, tam
        );

        if (polizas == null) polizas = new ArrayList<>();  // revisa que la lista no sea null

        // Inicializar nueva póliza con cliente
        Poliza nuevaPoliza = new Poliza();
        model.addAttribute("polizas", polizas);
        model.addAttribute("clientes", polizaService.obtenerTodosLosClientes());
        model.addAttribute("title", "Pólizas");
        model.addAttribute("nuevaPoliza", nuevaPoliza);

        model.addAttribute("filtro", filtro);
        model.addAttribute("valor", valor);

        return "polizas";                                // Retorna la vista "polizas"
    }

    @PostMapping("/crear")
    public String crear(@ModelAttribute("nuevaPoliza") Poliza poliza) {          // Crear nueva póliza
        System.out.println("[DEBUG] Iniciando creación de póliza...");
        System.out.println("[DEBUG] Clave: " + poliza.getClave());
        System.out.println("[DEBUG] Tipo: " + poliza.getTipo());
        System.out.println("[DEBUG] Monto: " + poliza.getMonto());
        System.out.println("[DEBUG] Descripción: " + poliza.getDescripcion());
        System.out.println("[DEBUG] Cliente: " + (poliza.getCliente() != null ? poliza.getCliente().getCurp() : "null"));
        System.out.println("[DEBUG] CURP Cliente: " + poliza.getCurpCliente());

        if (poliza.getClave() == null) {     // Generar clave si no existe
            poliza.setClave(UUID.randomUUID());
            System.out.println("[DEBUG] Clave generada: " + poliza.getClave());
        }

        try {
            polizaService.crearPoliza(poliza, null);   // Crear póliza usando el servicio
            System.out.println("[SUCCESS] Póliza creada exitosamente: " + poliza.getClave());
            return "redirect:/web/polizas";
        } catch (Exception e) {
            System.err.println("[ERROR] Error al crear póliza: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/web/polizas?error=" + e.getMessage();
        }
    }

    @PostMapping("/actualizar")
    public String actualizar(@ModelAttribute Poliza poliza) {  // Actualizar póliza existente
        System.out.println("[DEBUG] Actualizando póliza: " + poliza.getClave());

        Poliza existente = polizaService.buscarPolizas(  // Buscar póliza existente por clave
                poliza.getClave().toString(),
                null, null, null,
                null, null, 0, 1
        ).stream().findFirst().orElse(null);

        if (existente != null) {   // Actualizar póliza existente
            existente.setTipo(poliza.getTipo());
            existente.setDescripcion(poliza.getDescripcion());
            existente.setMonto(poliza.getMonto());
            existente.setCliente(poliza.getCliente());

            polizaService.actualizarPoliza(existente, null);
            System.out.println("[SUCCESS] Póliza actualizada: " + poliza.getClave());
        } else {
            System.err.println("[ERROR] No se encontró póliza para actualizar: " + poliza.getClave());
        }
        return "redirect:/web/polizas";
    }

    @PostMapping("/borrar")
    public String borrar(@RequestParam UUID clave) {
        System.out.println("[DEBUG] Eliminando póliza: " + clave);
        polizaService.borrarPorClave(clave);
        return "redirect:/web/polizas";
    }
}