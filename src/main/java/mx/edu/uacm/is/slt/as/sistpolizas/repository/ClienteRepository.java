package mx.edu.uacm.is.slt.as.sistpolizas.repository;

import mx.edu.uacm.is.slt.as.sistpolizas.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, String>{
    Optional<Cliente> findByNombresAndPrimerApellidoAndSegundoApellido(
            String nombres,
            String primerApellido,
            String segundoApellido
    );
}
