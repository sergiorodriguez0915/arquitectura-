package mx.edu.uacm.is.slt.as.sistpolizas.service;

import mx.edu.uacm.is.slt.as.sistpolizas.model.Beneficiario;
import mx.edu.uacm.is.slt.as.sistpolizas.model.IdBeneficiarioPoliza;
import mx.edu.uacm.is.slt.as.sistpolizas.repository.BeneficiarioRepository;
import mx.edu.uacm.is.slt.as.sistpolizas.repository.ClienteRepository;
import mx.edu.uacm.is.slt.as.sistpolizas.repository.PolizaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
public class BeneficiarioService {

    private final BeneficiarioRepository beneficiarioRepository;
    private final ClienteRepository clienteRepository;
    private final PolizaRepository polizaRepository;

    private final RestTemplate restTemplate = new RestTemplate();
    private final String REMOTE_API = "http://nachintoch.mx:8080";

    public BeneficiarioService(BeneficiarioRepository beneficiarioRepository,
                               ClienteRepository clienteRepository,
                               PolizaRepository polizaRepository) {
        this.beneficiarioRepository = beneficiarioRepository;
        this.clienteRepository = clienteRepository;
        this.polizaRepository = polizaRepository;
    }

    // ==============================
    // NUEVO MÉTODO: Obtener por ID
    // ==============================
    public Optional<Beneficiario> obtenerPorId(String curp, UUID clavePoliza) {
        IdBeneficiarioPoliza id = new IdBeneficiarioPoliza(curp, clavePoliza);
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
            String curp,
            UUID clavePoliza,
            String nombres,
            String primerApellido,
            String segundoApellido,
            int porcentaje
    ) {

        if (!clienteRepository.existsById(curp))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no existe");

        if (!polizaRepository.existsById(clavePoliza))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Póliza no existe");

        validarPorcentajeTotal(clavePoliza, porcentaje);

        IdBeneficiarioPoliza id = new IdBeneficiarioPoliza(
                curp, clavePoliza, nombres, primerApellido, segundoApellido, null
        );

        Beneficiario nuevo = new Beneficiario(id, porcentaje);

        try {
            String url = String.format(
                    REMOTE_API + "/beneficiario/%s/%s/%s/%s/%s/%d",
                    encode(curp),
                    clavePoliza.toString(),
                    encode(nombres),
                    encode(primerApellido),
                    encode(segundoApellido),
                    porcentaje
            );

            restTemplate.postForObject(url, null, Void.class);

        } catch (HttpClientErrorException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "Error al registrar beneficiario en el sistema remoto");
        }

        return beneficiarioRepository.save(nuevo);
    }

    // ==============================
    // Actualizar beneficiario
    // ==============================
    public Beneficiario actualizarBeneficiario(
            String curp,
            UUID clavePoliza,
            String nombres,
            String primerApellido,
            String segundoApellido,
            int porcentaje
    ) {

        IdBeneficiarioPoliza id = new IdBeneficiarioPoliza(curp, clavePoliza);

        Beneficiario existente = beneficiarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Beneficiario no encontrado"));

        validarPorcentajeTotalActualizar(clavePoliza, existente, porcentaje);

        existente.getId().setNombres(nombres);
        existente.getId().setPrimerApellido(primerApellido);
        existente.getId().setSegundoApellido(segundoApellido);
        existente.setPorcentaje(porcentaje);

        try {
            String url = String.format(
                    REMOTE_API + "/beneficiario/%s/%s/%s/%s/%s/%d",
                    encode(curp),
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
    // Borrar beneficiario
    // ==============================
    public void borrarBeneficiario(String curp, UUID clavePoliza) {
        IdBeneficiarioPoliza id = new IdBeneficiarioPoliza(curp, clavePoliza);
        try {
            restTemplate.delete(REMOTE_API + "/beneficiario/" + curp + "/" + clavePoliza);
        } catch (Exception ignored) {}
        beneficiarioRepository.deleteById(id);
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
    private void persistirLocales(List<Beneficiario> lista) {
        for (Beneficiario b : lista) {
            if (!beneficiarioRepository.existsById(b.getId())) {
                beneficiarioRepository.save(b);
            }
        }
    }

    private String encode(String s) {
        return s == null ? "" : s.replace(" ", "%20");
    }
}
