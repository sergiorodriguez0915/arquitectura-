package mx.edu.uacm.is.slt.as.sistpolizas.model;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class IdBeneficiarioPoliza implements Serializable {
    private String nombre;
    private String primerApellido;
    private String segundoApellido;
    private Date fechaNacimiento;
    private UUID clavePoliza;

    private static final long serialVersionid = 52711849321L;

    public IdBeneficiarioPoliza(){

    }

    public IdBeneficiarioPoliza(String nombre, String primerApellido, String sApellido, Date fechaNacimiento, UUID idpoliza) {
        this.nombre = nombre;
        this.primerApellido = primerApellido;
        this.segundoApellido = sApellido;
        this.fechaNacimiento = fechaNacimiento;
        this.clavePoliza = idpoliza;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPApellido() {
        return primerApellido;
    }

    public void setPApellido(String pApellido) {
        this.primerApellido = pApellido;
    }

    public String getSApellido() {
        return segundoApellido;
    }

    public void setSApellido(String sApellido) {
        this.segundoApellido = sApellido;
    }

    public Date getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(Date fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public UUID getClavePoliza() {
        return clavePoliza;
    }

    public void setClavePoliza(UUID idpoliza) {
        this.clavePoliza = idpoliza;
    }

    @Override
    public boolean equals(Object o) {
        if(super.equals(o)){
            return true;
        } else if (o instanceof IdBeneficiarioPoliza) {
            IdBeneficiarioPoliza otroIdBeneficiario = (IdBeneficiarioPoliza) o;
            return Objects.equals(nombre, otroIdBeneficiario.nombre) &&
                    Objects.equals(primerApellido,
                            otroIdBeneficiario.primerApellido) &&
                    Objects.equals(segundoApellido,
                            otroIdBeneficiario.segundoApellido) &&
                    Objects.equals(fechaNacimiento,
                            otroIdBeneficiario.fechaNacimiento) &&
                    Objects.equals(clavePoliza, otroIdBeneficiario.clavePoliza);
        } else {
                return false;
            }
    }

    @Override
    public int hashCode() {
        return Objects.hash(nombre, primerApellido, segundoApellido, fechaNacimiento, clavePoliza);
    }

    @Override
    public String toString() {
        return String.format("idBeneficiarioPoliza: (%s, %s, %s, %s, %s)", nombre, primerApellido, segundoApellido, fechaNacimiento, clavePoliza);
    }
}
