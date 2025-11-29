package mx.edu.uacm.is.slt.as.sistpolizas.model;

import jakarta.persistence.*;

@Entity
@Table(name = "beneficiario")
public class Beneficiario {

    @EmbeddedId
    private IdBeneficiarioPoliza id;

    @Column(name = "porcentaje")
    private int porcentaje;

    // Relación con Poliza usando la clave de la PK
    @ManyToOne
    @JoinColumn(name = "clave_poliza", insertable = false, updatable = false)
    private Poliza poliza;

    public Beneficiario() {}

    public Beneficiario(IdBeneficiarioPoliza id, int porcentaje) {
        this.id = id;
        this.porcentaje = porcentaje;
    }

    // Getters y setters
    public IdBeneficiarioPoliza getId() { return id; }
    public void setId(IdBeneficiarioPoliza id) { this.id = id; }

    public int getPorcentaje() { return porcentaje; }
    public void setPorcentaje(int porcentaje) { this.porcentaje = porcentaje; }

    public Poliza getPoliza() { return poliza; }
    public void setPoliza(Poliza poliza) { this.poliza = poliza; }
}
