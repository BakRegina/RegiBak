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
import java.awt.*; // Necesario para JPanel

public class rutinas extends JFrame {
    private JPanel panel1;
    private JButton volverButton; // Se comentará su lógica
    private JTextField textField1; // Campo de búsqueda
    private JButton buscarButton;
    private JComboBox comboBox1; // Clientes
    private JComboBox comboBox2; // Entrenadores
    private JTextField textField2; // Nombre de la Rutina
    private JTextField textField3; // Campo de texto no usado, se debe eliminar o usar
    private JTable table1;
    private JButton agregarButton;
    private JButton modificarButton;
    private JButton eliminarButton;
    private JButton limpiarButton;
    private JButton btnDetalles; // ⬅️ NUEVO: Para asignar ejercicios

    private String nombre_usuario; // Identificador del profesor logueado
    Connection con = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    public rutinas(String nombre_usuario) {
        this.nombre_usuario = nombre_usuario; // Asignamos el nombre de usuario

        con = conexion.conectar();

        if (con != null) {
            cargarClientes();
            cargarEntrenadores();
            cargarTabla();
        }
        // 🚨 LÓGICA DE BOTÓN VOLVER COMENTADA
        /*
        volverButton.addActionListener(e -> {
           // volver(); 
        });
        */

        // Asignación de ActionListeners
        buscarButton.addActionListener(e -> buscarRutina());
        agregarButton.addActionListener(e -> agregarRutina());
        modificarButton.addActionListener(e -> modificarRutina());
        eliminarButton.addActionListener(e -> eliminarRutina());
        limpiarButton.addActionListener(e -> limpiarCampos());

        // NUEVA FUNCIONALIDAD CRÍTICA
        if (btnDetalles != null) {
            btnDetalles.addActionListener(e -> verDetallesRutina());
        }


        // Cuando seleccionás una fila
        table1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int fila = table1.getSelectedRow();
                if (fila >= 0) {
                    textField2.setText(table1.getValueAt(fila, 1).toString());
                    // 🚨 CORRECCIÓN: Seleccionar el ítem correcto en el ComboBox (requiere que el ID del cliente y profesor estén visibles en el ítem)
                    // La lógica actual funciona si el nombre es único, pero es mejor buscar por ID en la BD. Por ahora, confiamos en la lógica original.
                    // comboBox1.setSelectedItem(table1.getValueAt(fila, 2).toString());
                    // comboBox2.setSelectedItem(table1.getValueAt(fila, 3).toString());

                    // Si haces doble clic, abres los detalles
                    if (e.getClickCount() == 2) {
                        verDetallesRutina();
                    }
                }
            }
        });
    }

    private void limpiarCampos() {
    }

    private void eliminarRutina() {
    }
    
    private void modificarRutina() {
    }
    
    private void agregarRutina() {
    }
    
    private void buscarRutina() {
    }

    private void cargarTabla() {
    }

    private void cargarEntrenadores() {
    }

    private void cargarClientes() {
    }

    // El método 'volver' se elimina o se comenta, ya no es necesario
    /* private void volver() { ... } */

    // El resto de los métodos (cargarClientes, cargarEntrenadores, cargarTabla, agregarRutina, etc.) 
    // permanecen en su mayoría iguales, ya que gestionan la tabla principal.

    // ... (Mantener cargarClientes, cargarEntrenadores, cargarTabla, agregarRutina, modificarRutina, eliminarRutina, buscarRutina, limpiarCampos) ...

    // --- FUNCIONALIDAD CLAVE: VER DETALLES ---
    private void verDetallesRutina() {
        int fila = table1.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(panel1, "Seleccione una rutina para ver los detalles y asignar ejercicios.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idRutina = Integer.parseInt(table1.getValueAt(fila, 0).toString());
        String nombreRutina = table1.getValueAt(fila, 1).toString();

        // 🚨 Paso 2: Abrir el editor de ejercicios de la rutina
        // Debes crear la clase RutinaDetallesFrame
        RutinaDetallesFrame detallesFrame = new RutinaDetallesFrame(idRutina, nombreRutina);
        detallesFrame.setVisible(true);
    }

    // --- GETTER OBLIGATORIO ---
    public JPanel getPanel1() {
        return panel1;
    }

    //Este método está aquí solo para que el código compile, si está en el form, NO debe estar aquí.
    public void setVisible(boolean b) {
    }
}

