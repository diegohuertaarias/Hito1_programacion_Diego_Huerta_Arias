package com.empresa;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Cuenta implements Serializable {
    private Cliente cliente;
    private List<Movimiento> movimientos;
    private double saldo;

    public Cuenta(Cliente cliente) {
        this.cliente = cliente;
        this.movimientos = new ArrayList<>();
        this.saldo = 0.0;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public List<Movimiento> getMovimientos() {
        return movimientos;
    }

    public double getSaldo() {
        return saldo;
    }

    public void ingresar(double cantidad) {
        Movimiento movimiento = new Movimiento(cantidad, "Ingreso");
        movimientos.add(movimiento);
        saldo += cantidad;
    }

    public void rerirar(double cantidad) {
        if (cantidad <= saldo) {
            Movimiento movimiento = new Movimiento(cantidad, "Retirada");
            movimientos.add(movimiento);
            saldo -= cantidad;

        } else {
            System.out.println("Saldo insuficiente" + cantidad);
        }
    }

    @Override
    public String toString() {
        return "Cuenta{" + "cliente = " + cliente + ", movimientos = " + movimientos + ", saldo = " + saldo + '}';

    }

    public void retirar(double cantidad) {
        if (cantidad <= saldo) {
            Movimiento movimiento = new Movimiento(cantidad, "Retirada");
            movimientos.add(movimiento);
            saldo -= cantidad;
        } else {
            System.out.println("Saldo insuficiente: " + cantidad);
        }
    }
}

