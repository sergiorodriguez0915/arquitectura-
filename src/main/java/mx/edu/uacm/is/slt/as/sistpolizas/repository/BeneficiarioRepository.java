package mx.edu.uacm.is.slt.as.sistpolizas.repository;

import mx.edu.uacm.is.slt.as.sistpolizas.model.Beneficiario;
import mx.edu.uacm.is.slt.as.sistpolizas.model.IdBeneficiarioPoliza;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface BeneficiarioRepository extends JpaRepository<Beneficiario, IdBeneficiarioPoliza> {

    List<Beneficiario> findByIdClavePoliza(UUID clavePoliza);

    List<Beneficiario> findByIdFechaNacimiento(Date fechaNacimiento);

    List<Beneficiario> findByIdNombresAndIdPrimerApellidoAndIdSegundoApellido(
            String nombres,
            String primerApellido,
            String segundoApellido
    );

    Optional<Beneficiario> findByPolizaBeneficiario_FechaNacimiento(Date fechaNacimiento);
}


