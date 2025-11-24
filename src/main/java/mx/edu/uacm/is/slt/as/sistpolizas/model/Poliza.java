package mx.edu.uacm.is.slt.as.sistpolizas.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.util.Objects;
import java.util.UUID;

@Entity
public class Poliza {

    @Id
    private UUID clave; //todo es el identificador
    private String tipo;
    private String descripcion;
    private double monto;
    private String curpCliente;


    public Poliza() {
    }

    public Poliza(UUID polizaid, String tipo, String descripcion, double monto, String cliente_asegurado) {
        this.clave = polizaid;
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.monto = monto;
        this.curpCliente = cliente_asegurado;
    }

    public UUID getClave() {
        return clave;
    }

    public void setClave(UUID polizaid) {
        this.clave = polizaid;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public String getCurpCliente() {
        return curpCliente;
    }

    public void setCurpCliente(String curpCliente) {
        this.curpCliente = curpCliente;
    }

    @Override
    public boolean equals(Object o) {
        if (super.equals(o)) {
            return true;
        } else if (o instanceof Poliza) {
            Poliza otraPoliza = (Poliza) o;
            return Objects.equals(clave, otraPoliza.clave);
        } else {
            return false;
        } // comprueba equivalencia
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(clave);
    }

    @Override
    public String toString() {
        return String.format("Poliza(%s, %s, %s, %.2f, %s)", clave, tipo, descripcion, monto, curpCliente);
    }
}