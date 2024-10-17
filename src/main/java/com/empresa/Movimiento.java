package com.empresa;

import java.io.Serializable;
import java.util.Date;

public class Movimiento implements Serializable {
    private Date fecha;
    private double cantidad;
    private String tipo;

    public Movimiento(double cantidad, String tipo){
        this.fecha=new Date();
        this.cantidad=cantidad;
        this.tipo=tipo;

    }
    public Date getFecha(){
        return fecha;
    }
    public double getCantidad(){
        return cantidad;
    }
    public String getTipo(){
        return tipo;
    }
    @Override
    public String toString(){
        return "Movimiento{"+ "fecha = "+fecha+", cantidad = "+cantidad+", tipo = "+tipo+'\''+"}";


    }







}
