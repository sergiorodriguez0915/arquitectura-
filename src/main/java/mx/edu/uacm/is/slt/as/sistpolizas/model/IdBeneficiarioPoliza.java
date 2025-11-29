package mx.edu.uacm.is.slt.as.sistpolizas.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class IdBeneficiarioPoliza implements Serializable {

    private static final long serialVersionUID = 1L;

    // FK que pertenece a la clave compuesta
    @Column(name = "clave_poliza")
    private UUID clavePoliza;

    // Datos que identifican al beneficiario dentro de la póliza
    @Column(name = "nombres")
    private String nombres;

    @Column(name = "primer_apellido")
    private String primerApellido;

    @Column(name = "segundo_apellido")
    private String segundoApellido;

    @Column(name = "fecha_nacimiento")
    private Date fechaNacimiento;

    public IdBeneficiarioPoliza() {}

    public IdBeneficiarioPoliza(UUID clavePoliza,
                                String nombres,
                                String primerApellido,
                                String segundoApellido,
                                Date fechaNacimiento) {
        this.clavePoliza = clavePoliza;
        this.nombres = nombres;
        this.primerApellido = primerApellido;
        this.segundoApellido = segundoApellido;
        this.fechaNacimiento = fechaNacimiento;
    }

    // Getters y setters
    public UUID getClavePoliza() { return clavePoliza; }
    public void setClavePoliza(UUID clavePoliza) { this.clavePoliza = clavePoliza; }

    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }

    public String getPrimerApellido() { return primerApellido; }
    public void setPrimerApellido(String primerApellido) { this.primerApellido = primerApellido; }

    public String getSegundoApellido() { return segundoApellido; }
    public void setSegundoApellido(String segundoApellido) { this.segundoApellido = segundoApellido; }

    public Date getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(Date fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    // equals y hashCode basados en todos los campos
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IdBeneficiarioPoliza)) return false;
        IdBeneficiarioPoliza that = (IdBeneficiarioPoliza) o;
        return Objects.equals(clavePoliza, that.clavePoliza) &&
                Objects.equals(nombres, that.nombres) &&
                Objects.equals(primerApellido, that.primerApellido) &&
                Objects.equals(segundoApellido, that.segundoApellido) &&
                Objects.equals(fechaNacimiento, that.fechaNacimiento);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clavePoliza, nombres, primerApellido, segundoApellido, fechaNacimiento);
    }

    @Override
    public String toString() {
        return "IdBeneficiarioPoliza{" +
                "clavePoliza=" + clavePoliza +
                ", nombres='" + nombres + '\'' +
                ", primerApellido='" + primerApellido + '\'' +
                ", segundoApellido='" + segundoApellido + '\'' +
                ", fechaNacimiento=" + fechaNacimiento +
                '}';
    }
}
