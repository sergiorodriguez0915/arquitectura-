package mx.edu.uacm.is.slt.as.sistpolizas.service;

import mx.edu.uacm.is.slt.as.sistpolizas.model.Poliza;
import mx.edu.uacm.is.slt.as.sistpolizas.repository.PolizaRepository;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class PolizaService {

    private final PolizaRepository polizaRepo;         // Repositorio interno
    private final PolizaRepository polizaExternaRepo;  // Repositorio externo simulado

    public PolizaService(PolizaRepository polizaRepo, PolizaRepository polizaExternaRepo) {
        this.polizaRepo = polizaRepo;
        this.polizaExternaRepo = polizaExternaRepo;
    }

    // Función privada para comparar dos pólizas
    private boolean polizasIguales(Poliza p1, Poliza p2) {
        if (p1 == null || p2 == null) return false;
        return Objects.equals(p1.getTipo(), p2.getTipo()) &&
                Objects.equals(p1.getMonto(), p2.getMonto()) &&
                Objects.equals(p1.getDescripcion(), p2.getDescripcion()) &&
                Objects.equals(p1.getCurpCliente(), p2.getCurpCliente());
    }

    // Verificar consistencia por ID (clave)
    public boolean verificarPorId(UUID clave) {
        Optional<Poliza> interna = polizaRepo.findById(clave);
        Optional<Poliza> externa = polizaExternaRepo.findById(clave);
        return interna.isPresent() && externa.isPresent() && polizasIguales(interna.get(), externa.get());
    }

    //Verificar consistencia por objeto Poliza
    public boolean verificarPoliza(Poliza poliza) {
        return verificarPorId(poliza.getClave());
    }

}
