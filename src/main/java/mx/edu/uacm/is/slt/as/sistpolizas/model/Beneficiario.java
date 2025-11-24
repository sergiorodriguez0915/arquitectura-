package mx.edu.uacm.is.slt.as.sistpolizas.model;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;

import java.util.Objects;

@Entity
public class Beneficiario {

    @EmbeddedId
    /*private String nombre;
    private String p_apellido;
    private String s_apellido;
    private Date fecha_nacimiento;
    */
    private IdBeneficiarioPoliza polizaBeneficiario;
    private double porcentaje;

    // Constructor vacío
    public Beneficiario() {
    }

    // Constructor con parámetros


    public Beneficiario(IdBeneficiarioPoliza polizaBeneficiario, double porcentaje) {
        this.polizaBeneficiario = polizaBeneficiario;
        this.porcentaje = porcentaje;
    }

    // Getters y Setters


    public IdBeneficiarioPoliza getPolizaBeneficiario() {
        return polizaBeneficiario;
    }

    public void setPolizaBeneficiario(IdBeneficiarioPoliza polizaBeneficiario) {
        this.polizaBeneficiario = polizaBeneficiario;
    }

    public double getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(double porcentaje) {
        this.porcentaje = porcentaje;
    }

    @Override
    public boolean equals(Object o) {
        if (super.equals(o)) {
            return true;
        } else if (o instanceof Beneficiario) {
            Beneficiario otroBeneficiario = (Beneficiario) o;
            return Objects.equals(polizaBeneficiario, otroBeneficiario.polizaBeneficiario);
        } else {
            return false;
        } // comprueba equivalencia
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(polizaBeneficiario);
    }

    @Override
    public String toString() {
        return String.format("Beneficiario: (%s, %s )", polizaBeneficiario,porcentaje);
    }

}