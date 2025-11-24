package mx.edu.uacm.is.slt.as.sistpolizas.controller;

import mx.edu.uacm.is.slt.as.sistpolizas.model.Cliente;
import mx.edu.uacm.is.slt.as.sistpolizas.model.Poliza;
import mx.edu.uacm.is.slt.as.sistpolizas.repository.ClienteRepository;
import mx.edu.uacm.is.slt.as.sistpolizas.repository.PolizaRepository;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@RestController
public class ClienteController {
    //Repositorios
    private final ClienteRepository clienteRepository;


    public ClienteController(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @GetMapping("/prueba")
    public Cliente regresarCliente() {

        Cliente cliente = new Cliente("Juan", "Monje", "Malvaez", "Direccion Falsa", "CURP", new Date());
        return clienteRepository.save(cliente);

    }


    @GetMapping("/clientes/copiar")
    public List<Cliente> copiarClientesLocal() {
        RestClient restClient = RestClient.create("http://nachintoch.mx:8080");

        //Traer clientes del sistema remoto
        List<Cliente> clientes = restClient.get()
                .uri("/clientes", new Object[]{})            //End point remoto o del sistema dueño
                .retrieve()
                .body(new ParameterizedTypeReference<>(){});

        // Guardar cada cliente en la base local
        for (Cliente cliente : clientes) {
            clienteRepository.save(cliente);
        }

        //Devuelve la lista guardada
        return clientes;
    }
/*
*/

    //Servicio REST GET
    @GetMapping("/cliente/{curp}")
    public ResponseEntity<String> getCliente(@PathVariable String curp){
        //Aqui va la implementacion
        //Cliente cliente = ClienteRepository.findByCurp(curp);

        //Cliente cliente = new Cliente();
        return ResponseEntity.ok("Cliente devuelto");
    }

    //Servicio REST  POST
    @PostMapping(value = {"/cliente/{curp}/{direccion}/{fecha_nacimiento}/{nombre}/{p_apellido}/{s_apellido}",
                          "/cliente/{curp}/{direccion}/{fecha_nacimiento}/{nombre}/{p_apellido}"})
    public ResponseEntity<String> createCliente(
            @PathVariable String curp,
            @PathVariable String direccion,
            @PathVariable String fecha_nacimiento,
            @PathVariable String nombre,
            @PathVariable String p_apellido,
            @PathVariable (required = false)String s_apellido) {
        //Aquí va la implementación
        //Cliente cliente = new Cliente();
        return ResponseEntity.ok("Cliente registrado");
    }

    //Servicio REST PUT
    @PutMapping(value={"/cliente/{curp}/{direccion}/{fecha_nacimiento}/{nombre}/{p_apellido}/{s_apellido}",
                       "/cliente/{curp}/{direccion}/{fecha_nacimiento}/{nombre}/{p_apellido}"})
    public ResponseEntity<String> updateCliente(
            @PathVariable String curp,
            @PathVariable String direccion,
            @PathVariable String fecha_nacimiento,
            @PathVariable String nombre,
            @PathVariable String p_apellido,
            @PathVariable (required = false)String s_apellido) {
        //Aquí va la implementación
        //Mensaje de cliente actualizado
        return ResponseEntity.ok("Cliente actualizado");
    }
}
