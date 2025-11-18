package gimnasio;

import gimnasio.conexion;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.awt.*;


public class rutinas extends JPanel {

    // Las siguientes variables DEBEN estar enlazadas por el IDE a tu rutinas.form
    private JPanel panel1; // Panel raíz de la interfaz (se devuelve con getPanel1)
    private JButton volverButton;
    private JTextField textField1;
    private JButton buscarButton;
    private JComboBox comboBox1;
    private JComboBox comboBox2;
    private JTextField textField2;
    private JTextField textField3;
    private JTable table1;
    private JButton agregarButton;
    private JButton modificarButton;
    private JButton eliminarButton;
    private JButton limpiarButton;
    private JButton btnDetalles;

    // El método initComponents() DEBE ser el que genera tu IDE al diseñar el formulario.
    // (Asegúrate de no tener uno escrito a mano que sobreescriba el diseño)
    private void initComponents() {
        // *** CÓDIGO GENERADO AUTOMÁTICAMENTE POR TU IDE ***
        // No lo modifiques, solo asegúrate de que se genere al guardar el .form.
    }

    private String nombre_usuario;
    Connection con = null;

    public rutinas(String nombre_usuario) {
        this.nombre_usuario = nombre_usuario;

        // El IDE llama a este método para construir el panel1 y todos los componentes del .form
        initComponents();

        // CRÍTICO: Configurar el layout del JPanel que extiende esta clase
        setLayout(new BorderLayout());
        add(panel1, BorderLayout.CENTER);

        con = conexion.conectar();

        if (con != null) {
            cargarClientes();
            cargarEntrenadores();
            cargarTabla();
        }

        // Asignación de ActionListeners
        if (buscarButton != null) buscarButton.addActionListener(e -> buscarRutina());
        if (agregarButton != null) agregarButton.addActionListener(e -> agregarRutina());
        if (modificarButton != null) modificarButton.addActionListener(e -> modificarRutina());
        if (eliminarButton != null) eliminarButton.addActionListener(e -> eliminarRutina());
        if (limpiarButton != null) limpiarButton.addActionListener(e -> limpiarCampos());

        if (btnDetalles != null) {
            btnDetalles.addActionListener(e -> verDetallesRutina());
        }

        if (table1 != null) {
            table1.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    int fila = table1.getSelectedRow();
                    if (fila >= 0) {
                        if (textField2 != null) textField2.setText(table1.getValueAt(fila, 1).toString());

                        if (e.getClickCount() == 2) {
                            verDetallesRutina();
                        }
                    }
                }
            });
        }
    }

    private void limpiarCampos() { /* Lógica de limpieza */ }
    private void eliminarRutina() { /* Lógica de eliminación */ }
    private void modificarRutina() { /* Lógica de modificación */ }
    private void agregarRutina() { /* Lógica de adición */ }
    private void buscarRutina() { /* Lógica de búsqueda */ }
    private void cargarTabla() { /* Lógica de carga de tabla */ }
    private void cargarEntrenadores() { /* Lógica de carga de entrenadores */ }
    private void cargarClientes() { /* Lógica de carga de clientes */ }


    private void verDetallesRutina() {
        if (table1 == null) return;
        int fila = table1.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione una rutina para ver los detalles y asignar ejercicios.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idRutina = (int) table1.getValueAt(fila, 0);
        String nombreRutina = table1.getValueAt(fila, 1).toString();

        // Esta línea ya debería funcionar correctamente
        RutinaDetallesFrame detallesFrame = new RutinaDetallesFrame(idRutina, nombreRutina);
        detallesFrame.setVisible(true);
    }

    // 3. REQUISITO CRÍTICO: Método para que admi.java obtenga el panel
    public JPanel getPanel1() {
        return panel1;
    }
}