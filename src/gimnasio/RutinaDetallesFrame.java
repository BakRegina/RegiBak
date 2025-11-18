package  gimnasio;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RutinaDetallesFrame extends JFrame {

    // Componentes que DEBEN estar en el .form
    private JPanel panelPrincipal; // Panel raíz
    private JLabel lblTitulo; // Muestra el nombre de la rutina
    private JComboBox<String> cmbEjercicios; // Ejercicios disponibles del banco
    private JTextField txtSeries;
    private JTextField txtRepeticiones;
    private JTextField txtPeso;
    private JButton btnAgregarEjercicio;
    private JTable tableEjerciciosRutina; // Muestra los ejercicios ASIGNADOS
    private JButton btnEliminarEjercicio;
    private JButton btnCerrar;

    private int idRutina;
    private Connection con;
    private JPanel panel2;

    public RutinaDetallesFrame() {

        setTitle("Detalles de Rutina: " );
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // CRÍTICO: Enlazar el diseño del .form
        if (panel2 != null) {
            setContentPane(panel2);
            setSize(800, 600);
            setLocationRelativeTo(null);
        } else {
            // Fallback si no está el .form
            System.err.println("Advertencia: panelPrincipal es NULL en RutinaDetallesFrame.");
            setSize(800, 600);
        }


        con = conexion.conectar();

        if (con != null) {
            cargarBancoEjercicios();
            cargarEjerciciosAsignados();
        }

        btnAgregarEjercicio.addActionListener(e -> agregarEjercicioARutina());
        btnEliminarEjercicio.addActionListener(e -> eliminarEjercicioDeRutina());
        btnCerrar.addActionListener(e -> dispose());
    }

    // --- CARGA DE DATOS ---
    private void cargarBancoEjercicios() {
        if (con == null) return;
        try {
            String sql = "SELECT id_ejercicio, nombre FROM ejercicios";
            try (PreparedStatement ps = con.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                cmbEjercicios.removeAllItems();
                while (rs.next()) {
                    // Formato: ID - Nombre del Ejercicio
                    cmbEjercicios.addItem(rs.getInt("id_ejercicio") + " - " + rs.getString("nombre"));
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar ejercicios del banco: " + e.getMessage());
        }
    }

    private void cargarEjerciciosAsignados() {
        if (con == null) return;
        DefaultTableModel modelo = new DefaultTableModel();
        modelo.setColumnIdentifiers(new Object[]{"ID Registro", "ID Ejercicio", "Ejercicio", "Series", "Repeticiones", "Peso Sugerido (kg)"});

        try {
            // Unir la tabla de registro_ejercicios con la de ejercicios para mostrar el nombre
            String sql = "SELECT re.id_registro, e.id_ejercicio, e.nombre, re.series, re.repeticiones, re.peso " +
                    "FROM rutina_ejercicios re " +
                    "JOIN ejercicios e ON re.id_ejercicio = e.id_ejercicio " +
                    "WHERE re.id_rutina = ?";

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, idRutina);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        modelo.addRow(new Object[]{
                                rs.getInt("id_registro"),
                                rs.getInt("id_ejercicio"),
                                rs.getString("nombre"),
                                rs.getInt("series"),
                                rs.getInt("repeticiones"),
                                rs.getDouble("peso") // Usamos el campo peso como "peso sugerido"
                        });
                    }
                    tableEjerciciosRutina.setModel(modelo);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar ejercicios de la rutina: " + e.getMessage());
        }
    }

    // --- AGREGAR / ELIMINAR ---
    private void agregarEjercicioARutina() {
        if (con == null || cmbEjercicios.getSelectedItem() == null) return;

        try {
            // Extraer el ID del ejercicio del ComboBox
            String selectedItem = (String) cmbEjercicios.getSelectedItem();
            int idEjercicio = Integer.parseInt(selectedItem.split(" - ")[0]);
            System.out.println(idEjercicio);

            int series = Integer.parseInt(txtSeries.getText().trim());
            int repeticiones = Integer.parseInt(txtRepeticiones.getText().trim());
            double peso = Double.parseDouble(txtPeso.getText().trim());

            // Insertar en la tabla registro_ejercicios, usando el campo fecha como NULL ya que es la rutina base.
            String sql = "INSERT INTO registros_progreso (id_ejercicio, series, repeticiones, peso) VALUES (?, ?, ?, ?)";

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, idEjercicio);
                ps.setInt(2, series);
                ps.setInt(3, repeticiones);
                ps.setDouble(4, peso);

                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Ejercicio añadido a la rutina.");
                cargarEjerciciosAsignados();
                limpiarCamposDetalle();
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Series, Repeticiones y Peso deben ser números válidos.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al añadir ejercicio: " + e.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarEjercicioDeRutina() {
        int fila = tableEjerciciosRutina.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un ejercicio de la tabla para eliminar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idRegistro = Integer.parseInt(tableEjerciciosRutina.getValueAt(fila, 0).toString());

        int respuesta = JOptionPane.showConfirmDialog(this, "¿Eliminar este ejercicio de la rutina?", "Confirmar", JOptionPane.YES_NO_OPTION);

        if (respuesta == JOptionPane.YES_OPTION) {
            String sql = "DELETE FROM registros_progreso WHERE id_registro = ?";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, idRegistro);
                if (ps.executeUpdate() > 0) {
                    JOptionPane.showMessageDialog(this, "Ejercicio eliminado de la rutina.");
                    cargarEjerciciosAsignados();
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al eliminar ejercicio: " + e.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void limpiarCamposDetalle() {
        txtSeries.setText("");
        txtRepeticiones.setText("");
        txtPeso.setText("");
        cmbEjercicios.setSelectedIndex(-1);
    }
    public void mostrarVentana() {
        JFrame frame = new JFrame("Agregar forma");
        frame.setContentPane(panel2);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
        frame.setLocation(x, y);
        frame.setVisible(true);
    }
}