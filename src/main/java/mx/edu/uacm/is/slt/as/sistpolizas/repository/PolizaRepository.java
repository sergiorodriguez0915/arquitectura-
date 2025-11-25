package mx.edu.uacm.is.slt.as.sistpolizas.repository;

import mx.edu.uacm.is.slt.as.sistpolizas.model.Poliza;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PolizaRepository extends JpaRepository<Poliza, UUID> {

    List<Poliza> findByTipo(String tipo);

    List<Poliza> findByDescripcion(String descripcion);
}