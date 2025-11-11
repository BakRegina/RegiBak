import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class RutinasClientePanel extends JPanel {

    // ... (Componentes y variables de clase) ...
    private JTable tableRutina;
    private JLabel lblNombreRutina;
    private JLabel lblInstruccion;
    private JTextField txtSeriesReales;
    private JTextField txtRepeticionesReales;
    private JTextField txtPesoReal;
    private JButton btnRegistrar;
    private JButton btnVerProgreso;

    private final String dniCliente;
    private int idCliente = -1;
    private int idRutina = -1;
    private Connection con = null;

    // Campo CRÍTICO: ID del elemento de la plantilla seleccionado
    private int idRutinaEjercicioSeleccionado = -1;
    private String nombreEjercicioSeleccionado = "";
    private JPanel panel1;

    public RutinasClientePanel(String dniCliente) {
        this.dniCliente = dniCliente;
        setLayout(new BorderLayout());

        // ... (Inicialización de Componentes y organización de Layout) ...

        // --- Conexión y Carga Inicial ---
        try {
            con = conexion.conectar();
            obtenerIdClienteYRutina();
            cargarRutinaBase();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error de conexión o de DB: " + e.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        }

        // --- Listeners ---
        tableRutina.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                seleccionarEjercicio();
            }
        });
        if(btnRegistrar != null) btnRegistrar.addActionListener(e -> registrarProgreso());
    }

    // ... (Método crearPanelRegistro) ...
    private JPanel crearPanelRegistro() {
        JPanel panel = new JPanel(new GridLayout(2, 4, 10, 5));

        panel.add(new JLabel("Series Reales:"));
        txtSeriesReales = new JTextField(5);
        panel.add(txtSeriesReales);

        panel.add(new JLabel("Repeticiones Reales:"));
        txtRepeticionesReales = new JTextField(5);
        panel.add(txtRepeticionesReales);

        panel.add(new JLabel("Peso Real (kg):"));
        txtPesoReal = new JTextField(5);
        panel.add(txtPesoReal);

        btnRegistrar = new JButton("Registrar Progreso");
        panel.add(btnRegistrar);

        return panel;
    }

    private void obtenerIdClienteYRutina() throws SQLException {
        // 1. Obtener ID del cliente a partir del DNI
        // ... (Lógica para obtener idCliente) ...
        String sqlCliente = "SELECT id_cliente FROM usuario_clientes WHERE dni = ?";
        try (PreparedStatement ps = con.prepareStatement(sqlCliente)) {
            ps.setString(1, dniCliente);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    idCliente = rs.getInt("id_cliente");
                } else {
                    throw new SQLException("Cliente no encontrado.");
                }
            }
        }

        // 2. Obtener la rutina principal del cliente
        String sqlRutina = "SELECT id_rutina, nombre FROM rutinas WHERE id_cliente = ? ORDER BY fecha_creacion DESC LIMIT 1";
        try (PreparedStatement ps = con.prepareStatement(sqlRutina)) {
            ps.setInt(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    idRutina = rs.getInt("id_rutina");
                    lblNombreRutina.setText("Mi Rutina Activa: " + rs.getString("nombre"));
                } else {
                    lblNombreRutina.setText("No tienes una rutina asignada.");
                }
            }
        }
    }

    // --- CARGA DE LA RUTINA BASE ---
    private void cargarRutinaBase() {
        if (idRutina == -1 || con == null) return;

        DefaultTableModel modelo = new DefaultTableModel();
        // Incluimos el ID de la plantilla para el registro posterior
        modelo.setColumnIdentifiers(new Object[]{"ID Template", "ID Ejercicio", "Ejercicio", "Series Sugeridas", "Reps Sugeridas", "Peso Sugerido (kg)"});

        try {
            // Buscamos los ejercicios de la plantilla usando 'rutina_ejercicios'
            String sql = "SELECT re.id_rutina_ejercicio, e.id_ejercicio, e.nombre, re.series, re.repeticiones, re.peso_sugerido " +
                    "FROM rutina_ejercicios re " + //Tabla corregida
                    "JOIN ejercicios e ON re.id_ejercicio = e.id_ejercicio " +
                    "WHERE re.id_rutina = ?";

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, idRutina);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        modelo.addRow(new Object[]{
                                rs.getInt("id_rutina_ejercicio"), // ID de la plantilla
                                rs.getInt("id_ejercicio"),
                                rs.getString("nombre"),
                                rs.getInt("series"),
                                rs.getInt("repeticiones"),
                                rs.getDouble("peso_sugerido")
                        });
                    }
                    tableRutina.setModel(modelo);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar la rutina base: " + e.getMessage());
        }
    }

    // --- MANEJO DE REGISTRO ---
    private void seleccionarEjercicio() {
        int fila = tableRutina.getSelectedRow();
        if (fila >= 0) {
            // Columna 0 es ID Template, Columna 2 es Nombre
            idRutinaEjercicioSeleccionado = (int) tableRutina.getValueAt(fila, 0);
            nombreEjercicioSeleccionado = (String) tableRutina.getValueAt(fila, 2);

            // Cargar sugerencias en los campos de registro
            txtSeriesReales.setText(tableRutina.getValueAt(fila, 3).toString());
            txtRepeticionesReales.setText(tableRutina.getValueAt(fila, 4).toString());
            txtPesoReal.setText(tableRutina.getValueAt(fila, 5).toString());

            lblInstruccion.setText("Registrando: " + nombreEjercicioSeleccionado);
        }
    }

    private void registrarProgreso() {
        if (idRutinaEjercicioSeleccionado == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un ejercicio de la tabla para registrar su progreso.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Obtener valores reales
            int seriesReales = Integer.parseInt(txtSeriesReales.getText().trim());
            int repeticionesReales = Integer.parseInt(txtRepeticionesReales.getText().trim());
            double pesoReal = Double.parseDouble(txtPesoReal.getText().trim());

            // 🚨 Insertamos en la tabla de PROGRESO
            String sql = "INSERT INTO registros_progreso (id_cliente, id_rutina_ejercicio, fecha, series_realizadas, repeticiones_realizadas, peso_utilizado) VALUES (?, ?, ?, ?, ?, ?)";

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, idCliente); // ID del cliente logueado
                ps.setInt(2, idRutinaEjercicioSeleccionado); // ID del elemento de la plantilla
                ps.setDate(3, java.sql.Date.valueOf(LocalDate.now())); // Fecha actual
                ps.setInt(4, seriesReales);
                ps.setInt(5, repeticionesReales);
                ps.setDouble(6, pesoReal);

                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Progreso registrado para " + nombreEjercicioSeleccionado + " en la fecha de hoy.", "Éxito", JOptionPane.INFORMATION_MESSAGE);

                // Limpiar campos y selección
                // ... (limpiar lógica) ...

            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Series, Repeticiones y Peso deben ser números válidos.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al registrar progreso: " + e.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        }
    }
}
