package mx.edu.uacm.is.slt.as.sistpolizas.model;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.util.Date;
import java.util.Objects;

@Entity
public class Beneficiario {

    @EmbeddedId
    private IdBeneficiarioPoliza id;

    private int porcentaje;

    public Beneficiario() {}

    public Beneficiario(IdBeneficiarioPoliza id,
                        int porcentaje) {
        this.id = id;
        this.porcentaje = porcentaje;
    }

    public IdBeneficiarioPoliza getId() {
        return id;
    }

    public void setId(IdBeneficiarioPoliza id) {
        this.id = id;
    }

    public int getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(int porcentaje) {
        this.porcentaje = porcentaje;
    }

    @Override
    public boolean equals(Object o) {
        if (super.equals(o)) {
            return true;
        } else if (o instanceof Beneficiario) {
            Beneficiario otroBeneficiario = (Beneficiario) o;
            return Objects.equals(poliza_beneficiario, otroBeneficiario.poliza_beneficiario);
        } else {
            return false;
        } // comprueba equivalencia
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(poliza_beneficiario);
    }

    @Override
    public String toString() {
        return String.format("Beneficiario: (%s, %s )",poliza_beneficiario,porcentaje);
    }

}