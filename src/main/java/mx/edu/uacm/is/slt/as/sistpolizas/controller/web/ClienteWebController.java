package mx.edu.uacm.is.slt.as.sistpolizas.controller.web;

import mx.edu.uacm.is.slt.as.sistpolizas.model.Cliente;
import mx.edu.uacm.is.slt.as.sistpolizas.service.PolizaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
public class ClienteWebController {

    private final PolizaService polizaService;

    public ClienteWebController(PolizaService polizaService) {  // Inyección de dependencia de PolizaService
        this.polizaService = polizaService;
    }

    // endpoint para obtener todos los clientes (locales + remotos)
    @GetMapping
    public List<Cliente> obtenerTodosLosClientes() {
        return polizaService.obtenerTodosLosClientes();
    }

    @GetMapping("/{curp}")
    public Cliente obtenerClientePorCurp(@PathVariable String curp) { // nuevo endpoint para buscar por CURP
        // Primero intentar con el sistema dueño
        try {
            Cliente clienteRemoto = polizaService.fetchClienteRemoto(curp);
            if (clienteRemoto != null) {
                return clienteRemoto;
            }
        } catch (Exception e) {
            System.err.println("[ERROR] No se pudo obtener cliente remoto: " + curp);
        }

        // Si no existe en remoto, buscar en local
        return polizaService.getClienteService().obtenerCliente(curp);
    }
}