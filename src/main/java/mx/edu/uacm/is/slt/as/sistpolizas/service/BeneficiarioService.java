package mx.edu.uacm.is.slt.as.sistpolizas.service;

import mx.edu.uacm.is.slt.as.sistpolizas.model.Beneficiario;
import mx.edu.uacm.is.slt.as.sistpolizas.model.IdBeneficiarioPoliza;
import mx.edu.uacm.is.slt.as.sistpolizas.repository.BeneficiarioRepository;
import mx.edu.uacm.is.slt.as.sistpolizas.repository.PolizaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
public class BeneficiarioService {

    private final BeneficiarioRepository beneficiarioRepository;
    private final PolizaRepository polizaRepository;

    private final RestTemplate restTemplate = new RestTemplate();
    private final String REMOTE_API = "http://nachintoch.mx:8080";

    public BeneficiarioService(BeneficiarioRepository beneficiarioRepository,
                               PolizaRepository polizaRepository) {
        this.beneficiarioRepository = beneficiarioRepository;
        this.polizaRepository = polizaRepository;
    }

    // ==============================
    // Obtener beneficiario por ID
    // ==============================
    public Optional<Beneficiario> obtenerPorId(UUID clavePoliza, String nombres,
                                               String primerApellido, String segundoApellido,
                                               Date fechaNacimiento) {
        IdBeneficiarioPoliza id = new IdBeneficiarioPoliza(
                clavePoliza, nombres, primerApellido, segundoApellido, fechaNacimiento
        );
        return beneficiarioRepository.findById(id);
    }

    // ==============================
    // Obtener todos los beneficiarios
    // ==============================
    public List<Beneficiario> obtenerTodos() {
        return beneficiarioRepository.findAll();
    }

    // ==============================
    // Crear beneficiario
    // ==============================
    public Beneficiario crearBeneficiario(
            UUID clavePoliza,
            String nombres,
            String primerApellido,
            String segundoApellido,
            Date fechaNacimiento,
            int porcentaje
    ) {
        if (!polizaRepository.existsById(clavePoliza))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Póliza no existe");

        validarPorcentajeTotal(clavePoliza, porcentaje);

        IdBeneficiarioPoliza id = new IdBeneficiarioPoliza(
                clavePoliza, nombres, primerApellido, segundoApellido, fechaNacimiento
        );

        Beneficiario nuevo = new Beneficiario(id, porcentaje);

        // Registrar en sistema remoto
        try {
            String url = String.format(
                    REMOTE_API + "/beneficiario/%s/%s/%s/%s/%d",
                    clavePoliza,
                    encode(nombres),
                    encode(primerApellido),
                    encode(segundoApellido),
                    porcentaje
            );
            restTemplate.postForObject(url, null, Void.class);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "Error al registrar beneficiario en el sistema remoto");
        }

        return beneficiarioRepository.save(nuevo);
    }

    // ==============================
    // Actualizar beneficiario
    // ==============================
    public Beneficiario actualizarBeneficiario(
            UUID clavePoliza,
            String nombres,
            String primerApellido,
            String segundoApellido,
            Date fechaNacimiento,
            int porcentaje
    ) {
        IdBeneficiarioPoliza id = new IdBeneficiarioPoliza(
                clavePoliza, nombres, primerApellido, segundoApellido, fechaNacimiento
        );

        Beneficiario existente = beneficiarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Beneficiario no encontrado"));

        validarPorcentajeTotalActualizar(clavePoliza, existente, porcentaje);

        existente.setPorcentaje(porcentaje);

        try {
            String url = String.format(
                    REMOTE_API + "/beneficiario/%s/%s/%s/%s/%d",
                    clavePoliza,
                    encode(nombres),
                    encode(primerApellido),
                    encode(segundoApellido),
                    porcentaje
            );
            restTemplate.put(url, null);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "Error actualizando beneficiario en sistema remoto");
        }

        return beneficiarioRepository.save(existente);
    }

    // ==============================
    // Validaciones
    // ==============================
    private void validarPorcentajeTotal(UUID clavePoliza, int nuevo) {
        List<Beneficiario> b = beneficiarioRepository.findByIdClavePoliza(clavePoliza);
        int total = b.stream().mapToInt(Beneficiario::getPorcentaje).sum();
        if (total + nuevo > 100) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El porcentaje total supera el 100%");
        }
    }

    private void validarPorcentajeTotalActualizar(UUID clavePoliza,
                                                  Beneficiario actualizar,
                                                  int nuevo) {
        List<Beneficiario> b = beneficiarioRepository.findByIdClavePoliza(clavePoliza);
        int total = b.stream()
                .filter(x -> !x.getId().equals(actualizar.getId()))
                .mapToInt(Beneficiario::getPorcentaje)
                .sum();
        if (total + nuevo > 100) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El porcentaje total supera el 100%");
        }
    }

    // ==============================
    // Utilidades
    // ==============================
    private String encode(String s) {
        return s == null ? "" : s.replace(" ", "%20");
    }
}
