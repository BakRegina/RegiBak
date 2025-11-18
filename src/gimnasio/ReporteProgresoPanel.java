package gimnasio;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ReporteProgresoPanel extends JPanel {

    private JPanel panel1;
    private JTable tableProgreso;

    private final String dniCliente;
    private int idCliente = -1;
    private Connection con = null;

    public ReporteProgresoPanel(String dniCliente) {
        this.dniCliente = dniCliente;

        // initComponents();

        if (panel1 != null) {
            setLayout(new BorderLayout());
            add(panel1, BorderLayout.CENTER);
        } else {

            setLayout(new BorderLayout());
            tableProgreso = new JTable();
            add(new JScrollPane(tableProgreso), BorderLayout.CENTER);
            System.err.println("Advertencia: panel1 es NULL. Usando JTable de respaldo.");
        }

        con = conexion.conectar();

        if (con != null) {
            obtenerIdCliente();
            cargarReporte();
        } else {
            // Agregar un mensaje de error si no hay conexión
            JLabel errorLabel = new JLabel("Error: No hay conexión a la base de datos.", SwingConstants.CENTER);
            if(panel1 != null) panel1.add(errorLabel, BorderLayout.NORTH);
            else add(errorLabel, BorderLayout.NORTH);
        }
    }
    /*
    private void initComponents() {
        // Carga los componentes del .form
    }*/

    private void obtenerIdCliente() {
        String sql = "SELECT id_cliente FROM usuario_clientes WHERE dni = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, dniCliente);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    idCliente = rs.getInt("id_cliente");
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al obtener ID de cliente: " + e.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarReporte() {
        if (idCliente == -1) {
            // Manejar caso sin cliente
            return;
        }

        DefaultTableModel modelo = new DefaultTableModel();
        modelo.setColumnIdentifiers(new Object[]{"Fecha", "Ejercicio", "Series (R)", "Reps (R)", "Peso (kg)"});

        try {
            String sql = "SELECT rp.fecha, e.nombre, rp.series_realizadas, rp.repeticiones_realizadas, rp.peso_utilizado " +
                    "FROM registros_progreso rp " +
                    "JOIN rutina_ejercicios re ON rp.id_rutina_ejercicio = re.id_rutina_ejercicio " +
                    "JOIN ejercicios e ON re.id_ejercicio = e.id_ejercicio " +
                    "WHERE rp.id_cliente = ? ORDER BY rp.fecha DESC, e.nombre ASC";

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, idCliente);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        modelo.addRow(new Object[]{
                                rs.getDate("fecha"),
                                rs.getString("nombre"),
                                rs.getInt("series_realizadas"),
                                rs.getInt("repeticiones_realizadas"),
                                rs.getDouble("peso_utilizado")
                        });
                    }
                    // Asigna el modelo a la JTable del formulario
                    tableProgreso.setModel(modelo);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar el reporte de progreso: " + e.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        }
    }

    public JPanel getPanel1() {
        return panel1;
    }
}