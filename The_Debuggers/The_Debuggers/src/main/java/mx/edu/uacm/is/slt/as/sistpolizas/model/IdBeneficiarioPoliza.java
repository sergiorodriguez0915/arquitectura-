package mx.edu.uacm.is.slt.as.sistpolizas.model;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class IdBeneficiarioPoliza implements Serializable {

    private String curp;           // NECESARIO para identificar beneficiario
    private UUID clavePoliza;      // Clave de la póliza (asociación)

    private String nombres;
    private String primerApellido;
    private String segundoApellido;
    private Date fechaNacimiento;

    public IdBeneficiarioPoliza() {}

    /** Constructor mínimo requerido por los Services */
    public IdBeneficiarioPoliza(String curp, UUID clavePoliza) {
        this.curp = curp;
        this.clavePoliza = clavePoliza;
    }

    /** Constructor completo opcional (si se requiere) */
    public IdBeneficiarioPoliza(String curp,
                                UUID clavePoliza,
                                String nombres,
                                String primerApellido,
                                String segundoApellido,
                                Date fechaNacimiento) {
        this.curp = curp;
        this.clavePoliza = clavePoliza;
        this.nombres = nombres;
        this.primerApellido = primerApellido;
        this.segundoApellido = segundoApellido;
        this.fechaNacimiento = fechaNacimiento;
    }

    /* ==========================
            GETTERS / SETTERS
       ========================== */

    public String getCurp() {
        return curp;
    }

    public void setCurp(String curp) {
        this.curp = curp;
    }

    public UUID getClavePoliza() {
        return clavePoliza;
    }

    public void setClavePoliza(UUID clavePoliza) {
        this.clavePoliza = clavePoliza;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getPrimerApellido() {
        return primerApellido;
    }

    public void setPrimerApellido(String primerApellido) {
        this.primerApellido = primerApellido;
    }

    public String getSegundoApellido() {
        return segundoApellido;
    }

    public void setSegundoApellido(String segundoApellido) {
        this.segundoApellido = segundoApellido;
    }

    public Date getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(Date fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    /* ==========================
           equals / hashCode
       ========================== */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IdBeneficiarioPoliza)) return false;
        IdBeneficiarioPoliza that = (IdBeneficiarioPoliza) o;
        return Objects.equals(curp, that.curp) &&
                Objects.equals(clavePoliza, that.clavePoliza);
    }

    @Override
    public int hashCode() {
        return Objects.hash(curp, clavePoliza);
    }

    @Override
    public String toString() {
        return "IdBeneficiarioPoliza(" +
                "curp='" + curp + '\'' +
                ", clavePoliza=" + clavePoliza +
                ", nombres='" + nombres + '\'' +
                ", primerApellido='" + primerApellido + '\'' +
                ", segundoApellido='" + segundoApellido + '\'' +
                ", fechaNacimiento=" + fechaNacimiento +
                ')';
    }
}
