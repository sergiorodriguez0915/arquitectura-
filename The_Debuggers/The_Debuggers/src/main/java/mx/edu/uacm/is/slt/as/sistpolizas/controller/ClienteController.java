package mx.edu.uacm.is.slt.as.sistpolizas.controller;

import mx.edu.uacm.is.slt.as.sistpolizas.model.Cliente;
import mx.edu.uacm.is.slt.as.sistpolizas.model.Poliza;
import mx.edu.uacm.is.slt.as.sistpolizas.repository.ClienteRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
public class ClienteController {


    //Repositorios
    private final ClienteRepository clienteRepository;
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

    public ClienteController(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @GetMapping("/prueba")
    public Cliente regresarCliente() {

        Cliente cliente = new Cliente("Juan", "Monje", "Malvaez", "Direccion Falsa", "CURP", new Date());
        return clienteRepository.save(cliente);
    }

    @GetMapping("/clientes")
    public List<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }

    //Servicio REST GET
    @GetMapping("/clientes/{curp}")
    public ResponseEntity<Cliente> getCliente(@PathVariable String curp) {
        return clienteRepository.findById(curp)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    //Servicio REST  POST
    @PostMapping(value = {"/cliente/{curp}/{direccion}/{fecha_nacimiento}/{nombre}/{p_apellido}/{s_apellido}",
            "/cliente/{curp}/{direccion}/{fecha_nacimiento}/{nombre}/{p_apellido}"})
    public ResponseEntity<Cliente> createCliente(
            @PathVariable String curp,
            @PathVariable String direccion,
            @PathVariable String fecha_nacimiento,
            @PathVariable String nombre,
            @PathVariable String p_apellido,
            @PathVariable(required = false) String s_apellido) {

        Date fecha;
        try {
            fecha = sdf.parse(fecha_nacimiento);
        } catch (ParseException ex) {
            return ResponseEntity.badRequest().build();
        }

        Cliente c = new Cliente(nombre, p_apellido, s_apellido, direccion, curp, fecha);
        Cliente guardado = clienteRepository.save(c);
        return ResponseEntity.ok(guardado);
    }

    //Servicio REST PUT
    @PutMapping(value = {"/cliente/{curp}/{direccion}/{fecha_nacimiento}/{nombre}/{p_apellido}/{s_apellido}",
            "/cliente/{curp}/{direccion}/{fecha_nacimiento}/{nombre}/{p_apellido}"})
    public ResponseEntity<Cliente> updateCliente(
            @PathVariable String curp,
            @PathVariable String direccion,
            @PathVariable String fecha_nacimiento,
            @PathVariable String nombre,
            @PathVariable String p_apellido,
            @PathVariable(required = false) String s_apellido) {

        Optional<Cliente> maybe = clienteRepository.findById(curp);
        if (maybe.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Date fecha;
        try {
            fecha = sdf.parse(fecha_nacimiento);
        } catch (ParseException ex) {
            return ResponseEntity.badRequest().build();
        }

        Cliente cliente = maybe.get();
        cliente.setDireccion(direccion);
        cliente.setFechaNacimiento(fecha);
        cliente.setNombres(nombre);
        cliente.setPrimerApellido(p_apellido);
        cliente.setSegundoApellido(s_apellido);

        Cliente actualizado = clienteRepository.save(cliente);
        return ResponseEntity.ok(actualizado);
    }
}