package mx.edu.uacm.is.slt.as.sistpolizas.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.util.Date;
import java.util.Objects;


@Entity
public class Cliente {

    private String nombres;
    private String primerApellido;
    private String segundoApellido;
    private String direccion;

    @Id
    private String curp;
    private Date fechaNacimiento;

    // constructor vacio
    public Cliente() {
    }

    // constructor con parametros
    public Cliente(String nombres, String primerApellido, String segundoApellido, String direccion, String curp, Date fechaNacimiento) {
        this.nombres = nombres;
        this.primerApellido = primerApellido;
        this.segundoApellido = segundoApellido;
        this.direccion = direccion;
        this.curp = curp;
        this.fechaNacimiento = fechaNacimiento;
    }

    // getters y setters

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombre) {
        this.nombres = nombre;
    }

    public String getPrimerApellido() {
        return primerApellido;
    }

    public void setPrimerApellido(String pApellido) {
        this.primerApellido = pApellido;
    }

    public String getSegundoApellido() {
        return segundoApellido;
    }

    public void setSegundoApellido(String sApellido) {
        this.segundoApellido = sApellido;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getCurp() {
        return curp;
    }

    public void setCurp(String curp) {
        this.curp = curp;
    }

    public Date getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(Date fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }
    @Override
    public boolean equals(Object o){
        if(super.equals(o)){
            return true;
        } else if (o instanceof Cliente) {
            Cliente otroCliente = (Cliente) o;
                return Objects.equals(curp, otroCliente.curp);
            } else {
                return false;
            }//comprueba equivalencia
        }//equals

    @Override
    public  int hashCode(){ //hace hashcode con el codigo de la curp internamente
        return Objects.hashCode(curp);
    }

    @Override
    public String toString() {
        return String.format("Cliente(%s, %s, %s, %s, %s, %s)", nombres, primerApellido, segundoApellido,direccion,curp, fechaNacimiento);
    }
}

