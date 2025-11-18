package  gimnasio;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ReportesProgresoPanel extends JPanel {

    // Componentes que deben estar en el .form (o creados con initComponents)
    private JPanel panelPrincipal;
    private JComboBox<String> cmbClientes;
    private JComboBox<String> cmbEjercicios;
    private JTextField txtFechaInicio;
    private JTextField txtFechaFin;
    private JButton btnGenerarReporte;
    private JTable tableReporte;
    private JScrollPane scrollTable;

    private Connection con = null;

    // Almacena el ID del cliente seleccionado para futuras consultas
    private int idClienteSeleccionado = -1;
    private JPanel panel1;
    private JTable tableProgreso;

    public ReportesProgresoPanel() {

        // 2. Conexión a la base de datos
        con = conexion.conectar();

        // 3. Cargar datos iniciales
        if (con != null) {
            cargarClientes();
        }

        // 4. Listeners
        btnGenerarReporte.addActionListener(e -> generarReporte());

        // Listener para cargar ejercicios cuando se selecciona un cliente
        cmbClientes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (cmbClientes.getSelectedIndex() > 0) {
                    seleccionarCliente();
                    cargarEjercicios();
                } else {
                    cmbEjercicios.removeAllItems();
                    cmbEjercicios.addItem("Todos los ejercicios");
                    idClienteSeleccionado = -1;
                }
            }
        });
    }

    // --- LÓGICA DE CARGA DE DATOS ---
    private void cargarClientes() {
        if (con == null) return;
        cmbClientes.removeAllItems();
        cmbClientes.addItem("Seleccione un cliente...");

        String sql = "SELECT id_cliente, nombre, apellido FROM usuario_clientes ORDER BY apellido";
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                // Formato: ID - Apellido, Nombre
                String item = rs.getInt("id_cliente") + " - " + rs.getString("apellido") + ", " + rs.getString("nombre");
                cmbClientes.addItem(item);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar clientes: " + e.getMessage());
        }
    }

    private void seleccionarCliente() {
        String selectedItem = (String) cmbClientes.getSelectedItem();
        if (selectedItem != null && selectedItem.contains(" - ")) {
            try {
                // Extraer el ID del cliente
                idClienteSeleccionado = Integer.parseInt(selectedItem.split(" - ")[0]);
            } catch (NumberFormatException ex) {
                idClienteSeleccionado = -1;
            }
        }
    }

    private void cargarEjercicios() {
        if (con == null || idClienteSeleccionado == -1) return;
        cmbEjercicios.removeAllItems();
        cmbEjercicios.addItem("Todos los ejercicios");

        try {
            // Buscamos todos los ejercicios que este cliente haya realizado (registros_progreso)
            String sql = "SELECT DISTINCT e.id_ejercicio, e.nombre " +
                    "FROM registros_progreso rp " +
                    "JOIN rutina_ejercicios re ON rp.id_rutina_ejercicio = re.id_rutina_ejercicio " +
                    "JOIN ejercicios e ON re.id_ejercicio = e.id_ejercicio " +
                    "WHERE rp.id_cliente = ? ORDER BY e.nombre";

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, idClienteSeleccionado);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String item = rs.getInt("id_ejercicio") + " - " + rs.getString("nombre");
                        cmbEjercicios.addItem(item);
                    }
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar ejercicios: " + e.getMessage());
        }
    }

    // --- LÓGICA DEL REPORTE ---

    private void generarReporte() {
        if (con == null || idClienteSeleccionado == -1) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un cliente para generar el reporte.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        DefaultTableModel modelo = (DefaultTableModel) tableReporte.getModel();
        modelo.setRowCount(0); // Limpiar resultados anteriores

        // Obtener filtros
        String fechaInicio = txtFechaInicio.getText().trim();
        String fechaFin = txtFechaFin.getText().trim();
        String ejercicioFiltro = cmbEjercicios.getSelectedIndex() > 0 ? (String) cmbEjercicios.getSelectedItem() : null;

        Integer idEjercicioFiltro = null;
        if (ejercicioFiltro != null && ejercicioFiltro.contains(" - ")) {
            try {
                idEjercicioFiltro = Integer.parseInt(ejercicioFiltro.split(" - ")[0]);
            } catch (NumberFormatException ex) {
                // No se pudo parsear el ID, se ignora el filtro de ejercicio
            }
        }

        // Construcción de la consulta SQL dinámica
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT rp.fecha, e.nombre, rp.series_realizadas, rp.repeticiones_realizadas, rp.peso_utilizado ");
        sql.append("FROM registros_progreso rp ");
        sql.append("JOIN rutina_ejercicios re ON rp.id_rutina_ejercicio = re.id_rutina_ejercicio ");
        sql.append("JOIN ejercicios e ON re.id_ejercicio = e.id_ejercicio ");
        sql.append("WHERE rp.id_cliente = ? ");

        // Parámetros dinámicos para el PreparedStatement
        int paramIndex = 1;

        if (idEjercicioFiltro != null) {
            sql.append("AND e.id_ejercicio = ? ");
        }

        // Validación de formato de fecha simple (asume que el usuario ingresa YYYY-MM-DD o lo deja vacío)
        if (!fechaInicio.isEmpty() && !fechaInicio.equals("YYYY-MM-DD")) {
            sql.append("AND rp.fecha >= ? ");
        }

        if (!fechaFin.isEmpty() && !fechaFin.equals("YYYY-MM-DD")) {
            sql.append("AND rp.fecha <= ? ");
        }

        sql.append("ORDER BY rp.fecha DESC, e.nombre ASC");

        try (PreparedStatement ps = con.prepareStatement(sql.toString())) {

            // 1. Establecer id_cliente
            ps.setInt(paramIndex++, idClienteSeleccionado);

            // 2. Establecer id_ejercicio (si existe)
            if (idEjercicioFiltro != null) {
                ps.setInt(paramIndex++, idEjercicioFiltro);
            }

            // 3. Establecer fecha_inicio (si existe)
            if (!fechaInicio.isEmpty() && !fechaInicio.equals("YYYY-MM-DD")) {
                ps.setString(paramIndex++, fechaInicio);
            }

            // 4. Establecer fecha_fin (si existe)
            if (!fechaFin.isEmpty() && !fechaFin.equals("YYYY-MM-DD")) {
                ps.setString(paramIndex++, fechaFin);
            }

            try (ResultSet rs = ps.executeQuery()) {
                boolean resultados = false;
                while (rs.next()) {
                    resultados = true;
                    modelo.addRow(new Object[]{
                            rs.getDate("fecha"),
                            rs.getString("nombre"), // Nombre del ejercicio
                            rs.getInt("series_realizadas"),
                            rs.getInt("repeticiones_realizadas"),
                            rs.getDouble("peso_utilizado")
                    });
                }

                if (!resultados) {
                    JOptionPane.showMessageDialog(this, "No se encontraron registros de progreso para los filtros seleccionados.", "Sin Resultados", JOptionPane.INFORMATION_MESSAGE);
                }
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al generar el reporte: " + e.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        }
    }

    // --- MÉTODO REQUERIDO PARA ADMI.JAVA (Corregido) ---
    public JPanel getPanel1() {
        return panel1;
    }
}
