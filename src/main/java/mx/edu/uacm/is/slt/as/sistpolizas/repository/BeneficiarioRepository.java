package mx.edu.uacm.is.slt.as.sistpolizas.repository;

import mx.edu.uacm.is.slt.as.sistpolizas.model.Beneficiario;
import mx.edu.uacm.is.slt.as.sistpolizas.model.IdBeneficiarioPoliza;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.Optional;

public interface BeneficiarioRepository extends JpaRepository<Beneficiario, IdBeneficiarioPoliza> {

    Optional<Beneficiario> findByIdNombresAndIdPrimerApellidoAndIdSegundoApellido(
            String nombres,
            String primerApellido,
            String segundoApellido
    );

    Optional<Beneficiario> findByIdFechaNacimiento(Date fechaNacimiento);
}
