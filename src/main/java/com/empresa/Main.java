package com.empresa;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Cuenta cuenta = iniciar();

        if (cuenta == null) {
            System.out.println("No se pudo iniciar sesión ni registrar una nueva cuenta.");
            return;
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
        ingresarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                double cantidad = Double.parseDouble(cantidadField.getText());
                cuenta.ingresar(cantidad);
                saldoLabel.setText("Saldo: " + cuenta.getSaldo());
                cantidadField.setText("");
            }
        });

        JButton retirarButton = new JButton("Retirar");
        retirarButton.setBackground(new Color(220, 53, 69));
        retirarButton.setForeground(Color.WHITE);
        retirarButton.setFont(new Font("Arial", Font.BOLD, 14));
        retirarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                double cantidad = Double.parseDouble(cantidadField.getText());
                cuenta.retirar(cantidad);
                saldoLabel.setText("Saldo: " + cuenta.getSaldo());
                cantidadField.setText("");
            }
        });

        JButton movimientosButton = new JButton("Ver Movimientos");
        movimientosButton.setBackground(new Color(40, 167, 69));
        movimientosButton.setForeground(Color.WHITE);
        movimientosButton.setFont(new Font("Arial", Font.BOLD, 14));
        movimientosButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mostrarMovimientos(cuenta.getMovimientos());
            }
        });

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.add(saldoLabel);
        panel.add(cantidadField);
        panel.add(ingresarButton);
        panel.add(retirarButton);
        panel.add(movimientosButton);

        frame.getContentPane().add(panel, BorderLayout.CENTER);
        frame.setVisible(true);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> guardarCuenta(cuenta)));
    }

    private static void mostrarMovimientos(List<Movimiento> movimientos) {
        String[] columnNames = {"Fecha", "Cantidad", "Tipo"};
        Object[][] data = new Object[movimientos.size()][3];

        for (int i = 0; i < movimientos.size(); i++) {
            Movimiento movimiento = movimientos.get(i);
            data[i][0] = movimiento.getFecha();
            data[i][1] = movimiento.getCantidad();
            data[i][2] = movimiento.getTipo();
        }

        JTable table = new JTable(data, columnNames);
        table.setFillsViewportHeight(true);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setRowHeight(30);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(500, 300));

        JOptionPane.showMessageDialog(null, scrollPane, "Movimientos de la Cuenta", JOptionPane.INFORMATION_MESSAGE);
    }

    private static String getFileName(String dni) {
        return dni + "_cuenta.dat";
    }

    private static Cuenta iniciar() {
        String[] options = {"Iniciar Sesión", "Registrarse"};
        int choice = JOptionPane.showOptionDialog(null, "Seleccione una opción", "Inicio",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

        if (choice == 1) {
            return crearNuevaCuenta();
        } else {
            return iniciarSesion();
        }
    }

    private static Cuenta iniciarSesion() {
        String dni = JOptionPane.showInputDialog("Ingrese su DNI para cargar la cuenta:");
        String fileName = getFileName(dni);
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
            return (Cuenta) ois.readObject();
        } catch (FileNotFoundException e) {
            System.out.println("Archivo no encontrado.");
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
            System.out.println("Archivo no encontrado.");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}