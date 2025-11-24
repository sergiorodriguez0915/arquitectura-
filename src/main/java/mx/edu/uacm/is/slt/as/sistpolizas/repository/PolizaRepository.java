package mx.edu.uacm.is.slt.as.sistpolizas.repository;


import mx.edu.uacm.is.slt.as.sistpolizas.model.Poliza;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PolizaRepository extends JpaRepository<Poliza, UUID> {

    /*Optional<Poliza> findByPolizaId(UUID polizaId);

    List<Poliza> findPolizaByTipo(String tipo);

    List<Poliza> findPolizaByDescripcion(String descripcion);
*/
}



//dstem.currentTimeMillis();


