package mx.edu.uacm.is.slt.as.sistpolizas.model;

import jakarta.persistence.*;
import java.util.Objects;
import java.util.UUID;

@Entity
public class Poliza {

    @Id
    private UUID clave;

    private String tipo;
    private String descripcion;
    private double monto;
    private String curp_cliente;

    @ManyToOne(optional = false)
    @JoinColumn(name = "curp_cliente", nullable = false)
    private Cliente cliente;

    public Poliza() {
    }

    public Poliza(UUID clave, String tipo, String descripcion, double monto, Cliente cliente) {
        this.clave = clave;
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.monto = monto;
        this.cliente = cliente;
    }

    public UUID getClave() { return clave; }
    public void setClave(UUID clave) { this.clave = clave; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public double getMonto() { return monto; }
    public void setMonto(double monto) { this.monto = monto; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    @Override
    public boolean equals(Object o) {
        if (super.equals(o)) return true;
        if (o instanceof Poliza) {
            return Objects.equals(clave, ((Poliza) o).clave);
        }
        return false;
    }

    @Override
    public int hashCode() { return Objects.hashCode(clave); }

    @Override
    public String toString() {
        return String.format("Poliza(%s, %s, %s, %.2f, %s)", clave, tipo, descripcion, monto, cliente);
    }
}
