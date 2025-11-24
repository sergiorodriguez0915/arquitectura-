package mx.edu.uacm.is.slt.as.sistpolizas.service;

import mx.edu.uacm.is.slt.as.sistpolizas.model.Cliente;
import mx.edu.uacm.is.slt.as.sistpolizas.repository.ClienteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    private final String REMOTE_API = "http://nachintoch.mx:8080";

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    // ============================================================
    // OBTENER CLIENTES DEL SISTEMA DUEÑO (REMOTO)
    // ============================================================
    public List<Cliente> obtenerClientesSistemaDueno() {

        try {
            String url = REMOTE_API + "/clientes";

            Cliente[] arr = restTemplate.getForObject(url, Cliente[].class);

            if (arr == null) {
                return clienteRepository.findAll();
            }

            List<Cliente> remotos = Arrays.asList(arr);

            // Guardar en BD local solo si no existen
            for (Cliente c : remotos) {
                if (!clienteRepository.existsById(c.getCurp())) {
                    clienteRepository.save(c);
                }
            }

            return remotos;

        } catch (Exception e) {
            // Si el sistema dueño falla, mostramos lo local
            return clienteRepository.findAll();
        }
    }

    // ============================================================
    // CONSULTAR CLIENTE
    // ============================================================
    public Cliente obtenerCliente(String curp) {

        // 1. Buscar local
        Optional<Cliente> local = clienteRepository.findById(curp);
        if (local.isPresent()) {
            return local.get();
        }

        // 2. Buscar remoto
        try {
            String url = REMOTE_API + "/clientes/" + curp;

            Cliente remoto = restTemplate.getForObject(url, Cliente.class);

            if (remoto == null) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Cliente no encontrado en BD local ni remota."
                );
            }

            clienteRepository.save(remoto);
            return remoto;

        } catch (HttpClientErrorException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado");
        }
    }

    // ============================================================
    // CREAR CLIENTE
    // ============================================================
    public Cliente crearCliente(String curp,
                                String direccion,
                                String fechaNacimientoStr,
                                String nombres,
                                String pApellido,
                                String sApellido) {

        if (curp.length() != 18)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CURP inválida");

        Date fechaNacimiento = parsearFecha(fechaNacimientoStr);

        Cliente cliente = new Cliente(
                nombres,
                pApellido,
                sApellido,
                direccion,
                curp,
                fechaNacimiento
        );

        // Registrar en sistema dueño
        try {
            String url = REMOTE_API + String.format(
                    "/cliente/%s/%s/%s/%s/%s/%s",
                    curp,
                    direccion,
                    fechaNacimientoStr,
                    nombres,
                    pApellido,
                    (sApellido != null ? sApellido : "")
            );

            restTemplate.postForObject(url, null, Cliente.class);

        } catch (HttpClientErrorException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Error al registrar cliente en sistema remoto"
            );
        }

        return clienteRepository.save(cliente);
    }

    // ============================================================
    // ACTUALIZAR CLIENTE
    // ============================================================
    public Cliente actualizarCliente(String curp,
                                     String direccion,
                                     String fechaNacimientoStr,
                                     String nombres,
                                     String pApellido,
                                     String sApellido) {

        Cliente cliente = clienteRepository.findById(curp)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Cliente no encontrado")
                );

        Date fechaNacimiento = parsearFecha(fechaNacimientoStr);

        cliente.setDireccion(direccion);
        cliente.setFechaNacimiento(fechaNacimiento);
        cliente.setNombres(nombres);
        cliente.setPrimerApellido(pApellido);
        cliente.setSegundoApellido(sApellido);

        // Actualizar remoto
        try {
            String url = REMOTE_API + String.format(
                    "/cliente/%s/%s/%s/%s/%s/%s",
                    curp,
                    direccion,
                    fechaNacimientoStr,
                    nombres,
                    pApellido,
                    (sApellido != null ? sApellido : "")
            );

            restTemplate.put(url, null);

        } catch (HttpClientErrorException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Error al actualizar cliente en sistema remoto"
            );
        }

        return clienteRepository.save(cliente);
    }

    // ============================================================
    // UTILIDADES
    // ============================================================
    private Date parsearFecha(String fecha) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd").parse(fecha);
        } catch (ParseException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Formato de fecha inválido. Use yyyy-MM-dd"
            );
        }
    }

    public boolean existePorCurp(String curp) {
        return clienteRepository.existsById(curp);
    }

    public Cliente crearCliente(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    public List<Cliente> listarClientes() {
        return clienteRepository.findAll();
    }

}
