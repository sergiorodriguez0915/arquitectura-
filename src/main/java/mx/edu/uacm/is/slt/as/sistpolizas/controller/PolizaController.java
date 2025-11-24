


package mx.edu.uacm.is.slt.as.sistpolizas.controller;

import jakarta.transaction.Transactional;
import mx.edu.uacm.is.slt.as.sistpolizas.model.Poliza;
import mx.edu.uacm.is.slt.as.sistpolizas.repository.BeneficiarioRepository;
import mx.edu.uacm.is.slt.as.sistpolizas.repository.ClienteRepository;
import mx.edu.uacm.is.slt.as.sistpolizas.repository.PolizaRepository;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PolizaController {


    /* private final PolizaRepository POLIZAS_REPO;
    private final ClienteRepository CLIENTES_REPO;
    private final BeneficiarioRepository BENEFICIARIOS_REPO;

    public PolizaController(PolizaRepository POLIZAS_REPO, ClienteRepository CLIENTES_REPO, BeneficiarioRepository BENEFICIARIOS_REPO) {
        this.POLIZAS_REPO = POLIZAS_REPO;
        this.CLIENTES_REPO = CLIENTES_REPO;
        this.BENEFICIARIOS_REPO = BENEFICIARIOS_REPO;
    } */


    //end-points GET
    @GetMapping("/pol" +
            "izas")
    public ResponseEntity<String> getPolizas() {
        return ResponseEntity.ok("Devuelve lista de Poliza");
    }

    @GetMapping("/poliza/{clave}")
    public ResponseEntity<String> getPorClave(@PathVariable UUID clave) {

        return ResponseEntity.ok("Devuelve una Poliza por clave");
    }

    @GetMapping("/polizas/{tipoOrCurp}")
    public ResponseEntity<String> getPorTipoOrCurp(@PathVariable String tipoOrCurp) {

        return ResponseEntity.ok("Devuelve una Poliza por tipo o curp");
    }

    @GetMapping(value = {//Porque existen dos rutas
            "/polizas/c/{nombres}/{primerApellido}/{segundoApellido}",//Los valores{} pueden ser diferentes a los nombres de los atributos de las clases de los modelos
            "/polizas/c/{nombres}/{primerApellido}"})
    public ResponseEntity<String> getPorNombreClientes(@PathVariable String nombres,
                                                       @PathVariable String primerApellido, @PathVariable(required = false)
                                                       String segundoApellido) {

        return ResponseEntity.ok("Devuelve una o todas las polizas de un cliente por su nombre completo");
    }

    @GetMapping(value = {
            "/polizas/b/{nombres}/{primerApellido}/{segundoApellido}",//Cambiar los atributos como los puso German?
            "/polizas/b/{nombres}/{primerApellido}"})
    public ResponseEntity<String> getPorNombreBeneficiarios(@PathVariable String nombres,
                                                            @PathVariable String primerApellido, @PathVariable(required = false)
                                                            String segundoApellido) {

        return ResponseEntity.ok("Devuelve todas las polizas de las personas beneficiarias");
    }

    @GetMapping("/polizas/b/{fechaNacimiento}")
    public ResponseEntity<String> getPorCumpleBeneficiarios(
            @PathVariable String fechaNacimiento) {

        return ResponseEntity.ok("Devuelve todas la polizas de las personas que son beneficiario por su fecha de nacimiento");
    }


    //end-points POST

    @PostMapping("/polizas/{clave}/{tipo}/{monto}/{descripcion}/{curpCliente}")
    public ResponseEntity<String> resgistraPoliza(@PathVariable UUID clave,
                                                  @PathVariable String tipo, @PathVariable double monto,
                                                  @PathVariable String descripcion, @PathVariable String curpCliente) {

        return ResponseEntity.ok("Resgitro de la poliza con los atributos dados");
    }

    //end-point PUT

    @PutMapping("/polizas/{clave}/{tipo}/{monto}/{descripcion}/{curpCliente}")
    public ResponseEntity<String> actualizaPolizaPorClave(@PathVariable String clave,
                                                          @PathVariable String tipo, @PathVariable Float monto,
                                                          @PathVariable String descripcion, @PathVariable String curpCliente) {
        return ResponseEntity.ok("Actializacion de cliente con los atributos dados");

    }


    //end-points DELETE

    @DeleteMapping("/polizas/{clave}")
    @Transactional
    public ResponseEntity<String> bajaPorClave(@PathVariable String clave) {
        return ResponseEntity.ok("Borra la poliza con la clave dado");
    }


}//PolizaController
