package com.empresa;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class Main {
    public static void main(String[] args) {
        Cuenta cuenta = cargarCuenta();

        if (cuenta == null) {
            cuenta = crearNuevaCuenta();
        } else {
            Cliente cliente = iniciarSesion(cuenta);
            if (cliente == null) {
                System.out.println("Inicio de sesión fallido.");
                return;
            }
        }

        JFrame frame = new JFrame("Gestión de Cuenta Bancaria");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLayout(new BorderLayout());

        JLabel saldoLabel = new JLabel("Saldo: " + cuenta.getSaldo());
        saldoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        saldoLabel.setFont(new Font("Arial", Font.BOLD, 16));

        JTextField cantidadField = new JTextField("Ingrese cantidad a ingresar o retirar");
        cantidadField.setHorizontalAlignment(SwingConstants.CENTER);
        cantidadField.setForeground(Color.GRAY);
        cantidadField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (cantidadField.getText().equals("Ingrese cantidad a ingresar o retirar")) {
                    cantidadField.setText("");
                    cantidadField.setForeground(Color.BLACK);
                }
            }

            public void focusLost(java.awt.event.FocusEvent evt) {
                if (cantidadField.getText().isEmpty()) {
                    cantidadField.setForeground(Color.GRAY);
                    cantidadField.setText("Ingrese cantidad a ingresar o retirar");
                }
            }
        });

        JButton ingresarButton = new JButton("Ingresar");
        ingresarButton.setBackground(new Color(0, 123, 255));
        ingresarButton.setForeground(Color.WHITE);
        ingresarButton.setFont(new Font("Arial", Font.BOLD, 14));
        Cuenta finalCuenta = cuenta;
        ingresarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                double cantidad = Double.parseDouble(cantidadField.getText());
                finalCuenta.ingresar(cantidad);
                saldoLabel.setText("Saldo: " + finalCuenta.getSaldo());
                cantidadField.setText("");
            }
        });

        JButton retirarButton = new JButton("Retirar");
        retirarButton.setBackground(new Color(220, 53, 69));
        retirarButton.setForeground(Color.WHITE);
        retirarButton.setFont(new Font("Arial", Font.BOLD, 14));
        Cuenta finalCuenta1 = cuenta;
        retirarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                double cantidad = Double.parseDouble(cantidadField.getText());
                finalCuenta1.retirar(cantidad);
                saldoLabel.setText("Saldo: " + finalCuenta1.getSaldo());
                cantidadField.setText("");
            }
        });

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.add(saldoLabel);
        panel.add(cantidadField);
        panel.add(ingresarButton);
        panel.add(retirarButton);

        frame.getContentPane().add(panel, BorderLayout.CENTER);
        frame.setVisible(true);

        Cuenta finalCuenta2 = cuenta;
        Runtime.getRuntime().addShutdownHook(new Thread(() -> guardarCuenta(finalCuenta2)));
    }

    private static String getFileName(String dni) {
        return dni + "_cuenta.dat";
    }

    private static Cuenta cargarCuenta() {
        String dni = JOptionPane.showInputDialog("Ingrese su DNI para cargar la cuenta:");
        String fileName = getFileName(dni);
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
            return (Cuenta) ois.readObject();
        } catch (FileNotFoundException e) {
            System.out.println("Archivo no encontrado, creando nueva cuenta.");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void guardarCuenta(Cuenta cuenta) {
        String fileName = getFileName(cuenta.getCliente().getDni());
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            oos.writeObject(cuenta);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Cuenta crearNuevaCuenta() {
        String nombre;
        String dni;
        Cuenta cuentaExistente;

        do {
            nombre = JOptionPane.showInputDialog("Ingrese su nombre:");
            dni = JOptionPane.showInputDialog("Ingrese su DNI:");
            cuentaExistente = cargarCuenta(dni);
            if (cuentaExistente != null && cuentaExistente.getCliente().getNombre().equals(nombre) && cuentaExistente.getCliente().getDni().equals(dni)) {
                JOptionPane.showMessageDialog(null, "El nombre y DNI ya existen. Por favor, ingrese datos diferentes.");
            } else {
                break;
            }
        } while (true);

        Cliente cliente = new Cliente(nombre, dni);
        return new Cuenta(cliente);
    }

    private static Cuenta cargarCuenta(String dni) {
        String fileName = getFileName(dni);
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
            return (Cuenta) ois.readObject();
        } catch (FileNotFoundException e) {
            System.out.println("Archivo no encontrado, creando nueva cuenta.");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Cliente iniciarSesion(Cuenta cuenta) {
        int intentos = 3;
        while (intentos > 0) {
            String nombre = JOptionPane.showInputDialog("Ingrese su nombre:");
            String dni = JOptionPane.showInputDialog("Ingrese su DNI:");
            Cliente cliente = cuenta.getCliente();
            if (cliente.getNombre().equals(nombre) && cliente.getDni().equals(dni)) {
                return cliente;
            }
            intentos--;
            JOptionPane.showMessageDialog(null, "Credenciales incorrectas. Intentos restantes: " + intentos);
        }

        int respuesta = JOptionPane.showConfirmDialog(null, "¿No tienes cuenta? ¿Deseas registrarte?", "Registro", JOptionPane.YES_NO_OPTION);
        if (respuesta == JOptionPane.YES_OPTION) {
            return crearNuevaCuenta().getCliente();
        }

        return null;
    }
}