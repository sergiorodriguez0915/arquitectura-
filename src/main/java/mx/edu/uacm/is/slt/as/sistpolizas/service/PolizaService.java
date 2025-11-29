package mx.edu.uacm.is.slt.as.sistpolizas.service;

import mx.edu.uacm.is.slt.as.sistpolizas.model.Beneficiario;
import mx.edu.uacm.is.slt.as.sistpolizas.model.IdBeneficiarioPoliza;
import mx.edu.uacm.is.slt.as.sistpolizas.model.Cliente;
import mx.edu.uacm.is.slt.as.sistpolizas.model.Poliza;
import mx.edu.uacm.is.slt.as.sistpolizas.repository.BeneficiarioRepository;
import mx.edu.uacm.is.slt.as.sistpolizas.repository.PolizaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PolizaService {

    private final PolizaRepository polizaRepository;
    private final BeneficiarioRepository beneficiarioRepository;
    private final ClienteService clienteService;
    private final RestTemplate restTemplate = new RestTemplate();

    // Los paths específicos (/polizas, /poliza, /clientes) se concatenan en los métodos.
    private final String API_REMOTA = "http://nachintoch.mx:8080";

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public PolizaService(PolizaRepository polizaRepository,
                         BeneficiarioRepository beneficiarioRepository,
                         ClienteService clienteService) {
        this.polizaRepository = polizaRepository;
        this.beneficiarioRepository = beneficiarioRepository;
        this.clienteService = clienteService;
    }


    @Transactional
    public Poliza crearPoliza(Poliza poliza, List<Beneficiario> beneficiarios) {
        if (poliza.getClave() == null) poliza.setClave(UUID.randomUUID());

        //  cliente
        Cliente cliente = poliza.getCliente();
        if (cliente != null && cliente.getCurp() != null) {
            cliente = clienteService.existePorCurp(cliente.getCurp()) ?
                    clienteService.obtenerCliente(cliente.getCurp()) :
                    Optional.ofNullable(fetchClienteRemoto(cliente.getCurp())).orElse(cliente);

            clienteService.crearCliente(cliente);
            poliza.setCliente(cliente);
        }

        // Guardar local
        polizaRepository.save(poliza);
        guardarBeneficiariosLocal(poliza.getClave(), beneficiarios);

        // Guardar remoto
        try {
            postPolizaRemota(poliza);
            if (beneficiarios != null) beneficiarios.forEach(b -> postBeneficiarioRemoto(poliza.getClave(), b));
        } catch (RestClientException e) {
            System.err.println("[REMOTE ERROR] No se pudo enviar la póliza o beneficiarios a remoto: " + e.getMessage());
        }

        return poliza;
    }

    public List<Poliza> buscarPolizas(String clave, String curp, String nombre, String tipo,
                                      String nombreBenef, String fechaNacBenef,
                                      Integer pagina, Integer tam) {



        List<Poliza> polizasLocales = polizaRepository.findAll();
        List<Poliza> polizasRemotas = fetchPolizasRemotas(); // Llama a la ruta base /polizas

        Map<UUID, Poliza> mapa = new HashMap<>();
        Map<String, Cliente> cacheClientes = new HashMap<>(); // Cache para evitar llamadas duplicadas

        polizasRemotas.forEach(p -> {
            // paso 1) p.getCurpCliente - Obtener el CURP del cliente desde la póliza remota
            String curpCliente = p.getCurpCliente();

            // Solo procesar si tenemos un CURP válido
            if (curpCliente != null && !curpCliente.trim().isEmpty()) {
                // paso 2) preguntar al dueño por el cliente con esa curp
                Cliente clienteCompleto = cacheClientes.get(curpCliente);

                // Si no está en cache, hacer la consulta al sistema remoto
                if (clienteCompleto == null) {
                    try {
                        clienteCompleto = fetchClienteRemoto(curpCliente);
                        if (clienteCompleto != null) {
                            // Guardar en cache para reutilizar
                            cacheClientes.put(curpCliente, clienteCompleto);
                            System.out.println("[INFO] Cliente obtenido y cacheado: " + curpCliente);
                        }
                    } catch (Exception e) {
                        System.err.println("[ERROR] No se pudo obtener cliente para CURP: " + curpCliente + " - " + e.getMessage());
                    }
                }

                // paso 3) p.setCliente que nos regresa el dueño
                if (clienteCompleto != null) {
                    p.setCliente(clienteCompleto);
                    System.out.println("[INFO] Póliza " + p.getClave() + " enriquecida con cliente: " + curpCliente);
                } else {
                    System.err.println("[WARNING] No se pudo obtener cliente para CURP: " + curpCliente + " en póliza: " + p.getClave());
                }
            } else {
                System.err.println("[WARNING] Póliza " + p.getClave() + " no tiene CURP de cliente válido");
            }

            mapa.putIfAbsent(p.getClave(), p);
        });

        polizasLocales.forEach(p -> mapa.put(p.getClave(), p));

        List<Poliza> todas = new ArrayList<>(mapa.values());

        Date fechaBenef = parseFecha(fechaNacBenef);
        List<Poliza> filtradas = todas.stream()
                .filter(p -> filtraPoliza(p, clave, curp, nombre, tipo))
                .filter(p -> filtraBeneficiarios(p, nombreBenef, fechaBenef))
                .collect(Collectors.toList());

        return paginar(filtradas, pagina != null ? pagina : 0, tam != null ? tam : 50);
    }


    @Transactional
    public void actualizarPoliza(Poliza poliza, List<Beneficiario> beneficiarios) {
        if (poliza.getClave() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Clave requerida");

        Cliente cliente = poliza.getCliente();
        if (cliente != null && cliente.getCurp() != null) {
            cliente = clienteService.existePorCurp(cliente.getCurp()) ?
                    clienteService.obtenerCliente(cliente.getCurp()) :
                    Optional.ofNullable(fetchClienteRemoto(cliente.getCurp())).orElse(cliente);

            clienteService.crearCliente(cliente);
            poliza.setCliente(cliente);
        }

        polizaRepository.save(poliza);

        List<Beneficiario> existentes = beneficiarioRepository.findAll().stream()
                .filter(b -> b.getId().getClavePoliza().equals(poliza.getClave()))
                .collect(Collectors.toList());
        beneficiarioRepository.deleteAll(existentes);
        guardarBeneficiariosLocal(poliza.getClave(), beneficiarios);

        try {
            putPolizaRemota(poliza);
        } catch (RestClientException ignored) {}
    }

    //ocupar para eliminar
    @Transactional
    public void borrarPorClave(UUID clave) {
        // Borrar remoto
        try {
            restTemplate.delete(API_REMOTA + "/poliza/" + clave);
        } catch (RestClientException ignored) {}

        // Beneficiarios locales
        List<Beneficiario> b = beneficiarioRepository.findAll().stream()
                .filter(x -> x.getId().getClavePoliza().equals(clave))
                .collect(Collectors.toList());
        beneficiarioRepository.deleteAll(b);

        polizaRepository.deleteById(clave);
    }


    private void guardarBeneficiariosLocal(UUID clavePoliza, List<Beneficiario> beneficiarios) {
        if (beneficiarios == null) return;
        beneficiarios.forEach(b -> {
            IdBeneficiarioPoliza id = new IdBeneficiarioPoliza(
                    clavePoliza,
                    b.getId().getNombres(),
                    b.getId().getPrimerApellido(),
                    b.getId().getSegundoApellido(),
                    b.getId().getFechaNacimiento()
            );
            b.setId(id);
            beneficiarioRepository.save(b);
        });
    }

    private List<Poliza> fetchPolizasRemotas() {
        // RUTA: http://nachintoch.mx:8080/polizas
        String urlBusqueda = API_REMOTA + "/polizas";
        try {
            Poliza[] array = restTemplate.getForObject(urlBusqueda, Poliza[].class);
            return array != null ? Arrays.asList(array) : Collections.emptyList();
        } catch (RestClientException e) {
            System.err.println("[REMOTE ERROR] No se pudieron obtener pólizas remotas de: " + urlBusqueda + ". Error: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    private Cliente fetchClienteRemoto(String curp) {
        // RUTA: http://nachintoch.mx:8080/clientes/{curp}
        String url = API_REMOTA + "/cliente/" + curp;
        try {
            return restTemplate.getForObject(url, Cliente.class);
        } catch (RestClientException e) {
            System.err.println("[REMOTE ERROR] No se pudo obtener cliente remoto: " + url + ". Error: " + e.getMessage());
            return null;
        }
    }

    private void postPolizaRemota(Poliza p) {
        // RUTA: http://nachintoch.mx:8080/poliza/{clave}/{tipo}/...
        String url = API_REMOTA + "/poliza/" + p.getClave() + "/" + p.getTipo() + "/" +
                p.getMonto() + "/" + p.getDescripcion() + "/" +
                (p.getCliente() != null ? p.getCliente().getCurp() : "null");
        restTemplate.postForObject(url, null, Poliza.class);
    }

    private void postBeneficiarioRemoto(UUID clavePoliza, Beneficiario b) {
        // RUTA: http://nachintoch.mx:8080/beneficiario/{fecha_nacimiento}/{clave_poliza}/...
        String url = API_REMOTA + "/beneficiario/" +
                DATE_FORMAT.format(b.getId().getFechaNacimiento()) + "/" +
                clavePoliza + "/" + b.getPorcentaje() + "/" +
                b.getId().getNombres() + "/" + b.getId().getPrimerApellido() + "/" +
                (b.getId().getSegundoApellido() != null ? b.getId().getSegundoApellido() : "");
        restTemplate.postForObject(url, null, Beneficiario.class);
    }

    private void putPolizaRemota(Poliza p) {
        // RUTA: http://nachintoch.mx:8080/poliza/{clave}/{tipo}/...
        String url = API_REMOTA + "/poliza/" + p.getClave() + "/" + p.getTipo() + "/" +
                p.getMonto() + "/" + p.getDescripcion() + "/" +
                (p.getCliente() != null ? p.getCliente().getCurp() : "null");
        restTemplate.put(url, null);
    }

    private boolean filtraPoliza(Poliza p, String clave, String curp, String nombre, String tipo) {
        boolean ok = true;
        if (clave != null && !clave.isBlank())
            ok = p.getClave() != null && p.getClave().toString().toLowerCase().contains(clave.toLowerCase());

        if (ok && curp != null && !curp.isBlank())
            ok = p.getCliente() != null && p.getCliente().getCurp() != null &&
                    p.getCliente().getCurp().toLowerCase().contains(curp.toLowerCase());

        if (ok && nombre != null && !nombre.isBlank()) {
            String completo = p.getCliente() != null ? p.getCliente().getNombres() + " " + p.getCliente().getPrimerApellido() +
                    (p.getCliente().getSegundoApellido() != null ? " " + p.getCliente().getSegundoApellido() : "") : "";
            ok = completo.toLowerCase().contains(nombre.toLowerCase());
        }

        if (ok && tipo != null && !tipo.isBlank())
            ok = p.getTipo() != null && p.getTipo().toLowerCase().contains(tipo.toLowerCase());

        return ok;
    }

    private boolean filtraBeneficiarios(Poliza p, String nombreBenef, Date fechaNacimiento) {
        List<Beneficiario> beneficiarios = p.getBeneficiarios();
        if (beneficiarios == null) return false;

        boolean okNombre = true;
        boolean okFecha = true;

        if (nombreBenef != null && !nombreBenef.isBlank()) {
            String lowerNombre = nombreBenef.toLowerCase();
            okNombre = beneficiarios.stream().anyMatch(b -> {
                String full = b.getId().getNombres() + " " + b.getId().getPrimerApellido() +
                        (b.getId().getSegundoApellido() != null ? " " + b.getId().getSegundoApellido() : "");
                return full.toLowerCase().contains(lowerNombre);
            });
        }

        if (fechaNacimiento != null) {
            okFecha = beneficiarios.stream().anyMatch(b -> fechaNacimiento.equals(b.getId().getFechaNacimiento()));
        }

        return okNombre && okFecha;
    }

    private List<Poliza> paginar(List<Poliza> lista, int page, int size) {
        int from = page * size;
        if (from >= lista.size()) return Collections.emptyList();
        int to = Math.min(from + size, lista.size());
        return lista.subList(from, to);
    }

    // Parseo de fecha
    private Date parseFecha(String fecha) {
        if (fecha == null || fecha.isBlank()) return null;
        try {
            return DATE_FORMAT.parse(fecha);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Fecha inválida");
        }
    }
}