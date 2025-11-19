package mx.edu.uacm.is.slt.as.sistpolizas.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class BeneficiarioController {

    /*
    //Repositorios
    private final ClienteRepository clienteRepository;

    public ClienteController(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }
    */

    //Servicio REST GET
    @GetMapping(value = {"/beneficiario/{fecha_nacimiento}/{clave_poliza}/{nombres}/{primer_apellido}/{segundo_apellido}",
            "/beneficiario/{fecha_nacimiento}/{clave_poliza}/{nombres}/{primer_apellido}"})
    public ResponseEntity<String> getBeneficiario(
            @PathVariable String fecha_nacimiento,
            @PathVariable UUID clave_poliza,
            @PathVariable String nombres,
            @PathVariable String primer_apellido,
            @PathVariable (required = false) String segundo_apellido){
        //Aqui va la implementacion

        return ResponseEntity.ok("Beneficiario devuelto");
    }

    //Servicio REST  POST
    @PostMapping(value = {"/beneficiario/{fecha_nacimiento}/{clave_poliza}/{porcentaje}/{nombres}/{primer_apellido}/{segundo_apellido}",
            "/beneficiario/{fecha_nacimiento}/{clave_poliza}/{porcentaje}/{nombres}/{primer_apellido}"})
    public ResponseEntity<String> createBeneficiario(
            @PathVariable String fecha_nacimiento,
            @PathVariable UUID clave_poliza,
            @PathVariable double porcentaje,
            @PathVariable String nombres,
            @PathVariable String primer_apellido,
            @PathVariable (required = false) String segundo_apellido){
        //Aqui va la implementacion

        return ResponseEntity.ok("Beneficiario registrado");
    }
}