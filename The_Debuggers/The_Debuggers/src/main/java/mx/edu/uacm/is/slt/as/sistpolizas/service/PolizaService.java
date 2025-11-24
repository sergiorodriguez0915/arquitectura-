package mx.edu.uacm.is.slt.as.sistpolizas.service;

import mx.edu.uacm.is.slt.as.sistpolizas.model.Cliente;
import mx.edu.uacm.is.slt.as.sistpolizas.model.Poliza;
import mx.edu.uacm.is.slt.as.sistpolizas.repository.PolizaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PolizaService {

    private final PolizaRepository polizaRepository;
    private final ClienteService clienteService;
    private final RestTemplate restTemplate = new RestTemplate();
    private final String REMOTE_API = "http://nachintoch.mx:8080";

    public PolizaService(PolizaRepository polizaRepository, ClienteService clienteService) {
        this.polizaRepository = polizaRepository;
        this.clienteService = clienteService;
    }

    public List<Poliza> getAllPolizas() {
        List<Poliza> locales = polizaRepository.findAll();
        if (!locales.isEmpty()) return locales;

        try {
            Poliza[] arr = restTemplate.getForObject(REMOTE_API + "/polizas", Poliza[].class);
            if (arr == null) return Collections.emptyList();
            List<Poliza> remotas = Arrays.asList(arr);

            List<Poliza> aGuardar = new ArrayList<>();

            for (Poliza p : remotas) {
                if (p.getCliente() == null || p.getCliente().getCurp() == null) {
                    System.err.println("Poliza remota sin cliente válido: " + p.getClave());
                    continue; // omitimos esta póliza
                }

                String curp = p.getCliente().getCurp();
                Cliente cliente;
                if (!clienteService.existePorCurp(curp)) {
                    cliente = fetchClienteRemoto(curp);
                    if (cliente == null) {
                        System.err.println("Cliente remoto no encontrado para póliza: " + p.getClave());
                        continue; // omitimos esta póliza
                    }
                    clienteService.crearCliente(cliente);
                } else {
                    cliente = clienteService.obtenerCliente(curp);
                }

                p.setCliente(cliente);
                aGuardar.add(p);
            }

            polizaRepository.saveAll(aGuardar);
            return aGuardar;

        } catch (HttpClientErrorException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "Error consultando polizas en sistema remoto");
        }
    }


    public Poliza getPorClave(UUID clave) {
        Optional<Poliza> local = polizaRepository.findById(clave);
        if (local.isPresent()) {
            return local.get();
        }

        try {
            Poliza remoto = restTemplate.getForObject(REMOTE_API + "/poliza/" + clave, Poliza.class);
            if (remoto == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Poliza no encontrada en remoto");
            }

            // Obtener cliente y asignarlo
            Cliente cliente = clienteService.obtenerCliente(remoto.getCliente().getCurp());
            remoto.setCliente(cliente);

            polizaRepository.save(remoto);
            return remoto;
        } catch (HttpClientErrorException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Poliza no encontrada en remoto");
        }
    }


    public Poliza crearPoliza(UUID clave, String tipo, double monto, String descripcion, String curpCliente) {
        Cliente cliente = clienteService.obtenerCliente(curpCliente);

        validarClaveNoExistente(clave);
        tipo = normalizarTipo(tipo);
        validarTipoValido(tipo);
        if (monto <= 0) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Monto debe ser > 0");

        Poliza poliza = new Poliza(clave, tipo, descripcion, monto, cliente);

        try {
            String url = String.format(REMOTE_API + "/poliza/%s/%s/%.2f/%s/%s",
                    clave, tipo, monto, urlEncode(descripcion), urlEncode(curpCliente));
            restTemplate.postForObject(url, null, Void.class);
        } catch (HttpClientErrorException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Error registrando poliza en remoto");
        }

        return polizaRepository.save(poliza);
    }

    public Poliza actualizarPoliza(UUID clave, String tipo, double monto, String descripcion, String curpCliente) {
        Poliza existente = polizaRepository.findById(clave)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Poliza no encontrada"));

        Cliente cliente = clienteService.obtenerCliente(curpCliente);

        tipo = normalizarTipo(tipo);
        validarTipoValido(tipo);
        if (monto <= 0) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Monto debe ser > 0");

        existente.setTipo(tipo);
        existente.setMonto(monto);
        existente.setDescripcion(descripcion);
        existente.setCliente(cliente);

        try {
            String url = String.format(REMOTE_API + "/poliza/%s/%s/%.2f/%s/%s",
                    clave, tipo, monto, urlEncode(descripcion), urlEncode(curpCliente));
            restTemplate.put(url, null);
        } catch (HttpClientErrorException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Error actualizando poliza en remoto");
        }

        return polizaRepository.save(existente);
    }

    public void borrarPorClave(UUID clave) {
        try {
            restTemplate.delete(REMOTE_API + "/poliza/" + clave);
        } catch (HttpClientErrorException.NotFound e) {
        } catch (HttpClientErrorException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Error borrando poliza en remoto");
        }

        polizaRepository.deleteById(clave);
    }

    private Cliente fetchClienteRemoto(String curp) {
        try {
            return restTemplate.getForObject(REMOTE_API + "/clientes/" + curp, Cliente.class);
        } catch (HttpClientErrorException.NotFound e) {
            return null;
        } catch (HttpClientErrorException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Error consultando cliente remoto");
        }
    }

    private void validarClaveNoExistente(UUID clave) {
        if (polizaRepository.existsById(clave))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe una poliza con esa clave");
    }

    private void validarTipoValido(String tipo) {
        Set<String> permitidos = new HashSet<>(Arrays.asList("auto", "vida", "médico", "medico"));
        if (!permitidos.contains(tipo.toLowerCase()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tipo de poliza inválido");
    }

    private String normalizarTipo(String tipo) {
        if (tipo == null) return null;
        switch (tipo.trim().toLowerCase()) {
            case "0": return "auto";
            case "1": return "vida";
            case "2": return "médico";
            case "medico": return "médico";
            default: return tipo;
        }
    }

    private String urlEncode(String s) {
        return s == null ? "" : s.replace(" ", "%20");
    }
}
