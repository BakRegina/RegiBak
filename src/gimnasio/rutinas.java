package gimnasio;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class rutinas extends JPanel {

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
    private JButton agregarRutinaButton;
    private DefaultComboBoxModel clientedefault;
    DefaultTableModel javaTable = new DefaultTableModel(){
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        };

    };

    private String nombre_usuario;
    Connection con = null;

    public rutinas(String nombre_usuario) {
        this.nombre_usuario = nombre_usuario;


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
        agregarRutinaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RutinaDetallesFrame det= new RutinaDetallesFrame();
                det.mostrarVentana();
            }
        });
    }

    private void cargarClientes() {
        if (con == null) return;
        comboBox1.removeAllItems();
        comboBox1.addItem("Seleccione un cliente...");

        String sql = "SELECT id_cliente, nombre, apellido FROM usuario_clientes ORDER BY apellido";
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                // Formato: ID - Apellido, Nombre
                String item = rs.getInt("id_cliente") + " - " + rs.getString("apellido") + ", " + rs.getString("nombre");
                comboBox1.addItem(item);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar clientes: " + e.getMessage());
        }
    }


    private void limpiarCampos() { /* Lógica de limpieza */ }
    private void eliminarRutina() { /* Lógica de eliminación */ }
    private void modificarRutina() { /* Lógica de modificación */ }
    private void agregarRutina() { /* Lógica de adición */ }
    private void buscarRutina() { /* Lógica de búsqueda */ }
    private void cargarTabla() { /* Lógica de carga de tabla */ }
    private void cargarEntrenadores() { /* Lógica de carga de entrenadores */ }



    private void verDetallesRutina() {
        javaTable.addColumn("Usuario");
        javaTable.addColumn("Nombre");
        javaTable.addColumn("Apellido");
        javaTable.addColumn("Documento");
        table1.setModel(javaTable);
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        TableColumn columna;
        for(int i = 0; i<table1.getColumnCount(); i++){
            table1.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }


        if (table1 == null) return;

        int fila = table1.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione una rutina para ver los detalles y asignar ejercicios.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idRutina = (int) table1.getValueAt(fila, 0);
        String nombreRutina = table1.getValueAt(fila, 1).toString();

        // Esta línea ya debería funcionar correctamente
    }


    public JPanel getPanel1() {
        return panel1;
    }
}