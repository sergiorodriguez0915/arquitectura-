package mx.edu.uacm.is.slt.as.sistpolizas.service;

import mx.edu.uacm.is.slt.as.sistpolizas.model.Poliza;
import mx.edu.uacm.is.slt.as.sistpolizas.repository.PolizaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PolizaService {

    private final PolizaRepository polizaRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final String API_REMOTA = "http://nachintoch.mx:8080/polizas"; // URL corregida

    public PolizaService(PolizaRepository polizaRepository) {
        this.polizaRepository = polizaRepository;
    }

    // Traer todas las pólizas locales + remotas
    public List<Poliza> obtenerTodasCombinadas() {
        List<Poliza> locales = polizaRepository.findAll();
        List<Poliza> remotas = new ArrayList<>();

        try {
            ResponseEntity<Poliza[]> response = restTemplate.getForEntity(API_REMOTA, Poliza[].class);
            if (response.getBody() != null) {
                remotas = Arrays.asList(response.getBody());
            }
        } catch (Exception e) {
            System.out.println("No se pudo traer las pólizas remotas: " + e.getMessage());
        }

        List<Poliza> combinadas = new ArrayList<>();
        combinadas.addAll(locales);
        combinadas.addAll(remotas);
        return combinadas;
    }

    // Buscar pólizas filtrando por cualquier campo, con paginación
    public List<Poliza> buscarPolizas(String clave, String curp, String nombre, String tipo,
                                      String beneficiario, String fechaNacBenef,
                                      Integer pagina, Integer tam) {

        List<Poliza> todas = obtenerTodasCombinadas();

        List<Poliza> filtradas = todas.stream().filter(p -> {
            boolean coincide = true;

            if (clave != null && !clave.isBlank())
                coincide = p.getClave() != null && p.getClave().toString().contains(clave);

            if (coincide && curp != null && !curp.isBlank())
                coincide = p.getCliente() != null && p.getCliente().getCurp() != null &&
                        p.getCliente().getCurp().toLowerCase().contains(curp.toLowerCase());

            if (coincide && nombre != null && !nombre.isBlank()) {
                String completo = p.getCliente() != null
                        ? (p.getCliente().getNombres() + " " + p.getCliente().getPrimerApellido() + " " +
                        (p.getCliente().getSegundoApellido() != null ? p.getCliente().getSegundoApellido() : ""))
                        : "";
                coincide = completo.toLowerCase().contains(nombre.toLowerCase());
            }

            if (coincide && tipo != null && !tipo.isBlank())
                coincide = p.getTipo() != null && p.getTipo().toLowerCase().contains(tipo.toLowerCase());

            return coincide;
        }).collect(Collectors.toList());

        // Paginación
        int pag = (pagina == null || pagina < 0) ? 0 : pagina;
        int tamaño = (tam == null || tam <= 0) ? 50 : tam;
        int inicio = pag * tamaño;
        if (inicio >= filtradas.size()) return Collections.emptyList();
        int fin = Math.min(inicio + tamaño, filtradas.size());
        return filtradas.subList(inicio, fin);
    }

    // Métodos locales CRUD
    public Poliza obtenerPorClave(UUID clave) {
        return polizaRepository.findById(clave)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Póliza no encontrada"));
    }

    public void guardarPoliza(Poliza poliza) {
        polizaRepository.save(poliza);
    }

    public void borrarPorClave(UUID clave) {
        polizaRepository.deleteById(clave);
    }

    public Object getAllPolizas() {
        return null;
    }
    // ===============================
// BÚSQUEDAS SIMPLIFICADAS
// ===============================

    public List<Poliza> buscarPorClave(String clave) {
        return buscarPolizas(clave, null, null, null, null, null, 0, 50);
    }

    public List<Poliza> buscarPorCurp(String curp) {
        return buscarPolizas(null, curp, null, null, null, null, 0, 50);
    }

    public List<Poliza> buscarPorNombre(String nombre) {
        return buscarPolizas(null, null, nombre, null, null, null, 0, 50);
    }

    public List<Poliza> buscarPorTipo(String tipo) {
        return buscarPolizas(null, null, null, tipo, null, null, 0, 50);
    }

    public List<Poliza> listarPolizas() {
        return obtenerTodasCombinadas();
    }

}