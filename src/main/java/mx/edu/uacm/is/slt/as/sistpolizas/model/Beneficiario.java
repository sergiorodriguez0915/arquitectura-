package mx.edu.uacm.is.slt.as.sistpolizas.model;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.springframework.core.SpringVersion;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Entity
public class Beneficiario {

    @EmbeddedId 
    private IdBeneficiarioPoliza id;
    private double porcentaje;

    // Constructor vacío
    public Beneficiario() {
    }

    // Constructor con parámetros

    public Beneficiario(IdBeneficiarioPoliza id, double porcentaje) {
        this.id = id;
        this.porcentaje = porcentaje;
    }


    // Getters y Setters


    public IdBeneficiarioPoliza getId() {
        return id;
    }

    public void setId(IdBeneficiarioPoliza id) {
        this.id = id;
    }

    public double getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(double porcentaje) {
        this.porcentaje = porcentaje;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return String.format("Beneficiario: (%s, %s )",id ,porcentaje);
    }

}