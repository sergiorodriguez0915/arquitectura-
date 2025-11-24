package mx.edu.uacm.is.slt.as.sistpolizas.model;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;

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
}
