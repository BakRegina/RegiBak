package gimnasio;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ejercicios extends JPanel {
    private JPanel panel1;
    private JTextField txtNombre;
    private JTextField txtDescripcion;
    private JTextField txtLinkVideo; // Nuevo campo basado en tu BD
    private JButton btnAgregar;
    private JTable tableEjercicios;
    
    private Connection con = null;

    public ejercicios() {
        initComponents();
        setLayout(new BorderLayout());
        add(panel1, BorderLayout.CENTER);
        
        con = conexion.conectar();
        if (con != null) {
            cargarTablaEjercicios();
        }

        if (btnAgregar != null) {
            btnAgregar.addActionListener(this::agregarEjercicio);
        }
    }

    private void initComponents() {
        panel1 = new JPanel(new BorderLayout());
        txtNombre = new JTextField(20);
        txtDescripcion = new JTextField(30);
        txtLinkVideo = new JTextField(25);
        btnAgregar = new JButton("Agregar Ejercicio");
        tableEjercicios = new JTable();
        
        JPanel inputPanel = new JPanel(new FlowLayout());
        inputPanel.add(new JLabel("Nombre:"));
        inputPanel.add(txtNombre);
        inputPanel.add(new JLabel("Descripción:"));
        inputPanel.add(txtDescripcion);
        inputPanel.add(new JLabel("Link Video:"));
        inputPanel.add(txtLinkVideo);
        inputPanel.add(btnAgregar);

        panel1.add(inputPanel, BorderLayout.NORTH);
        panel1.add(new JScrollPane(tableEjercicios), BorderLayout.CENTER);
    }

    private void agregarEjercicio(ActionEvent e) {
        if (con == null) return;

        String nombre = txtNombre.getText().trim();
        String descripcion = txtDescripcion.getText().trim();
        String linkVideo = txtLinkVideo.getText().trim(); 

        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre del ejercicio es obligatorio.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Se asume que 'link_imagen' puede ser nulo, por lo que no se incluye aquí
            String sql = "INSERT INTO ejercicios (nombre, descripcion, link_video) VALUES (?, ?, ?)";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, nombre);
                ps.setString(2, descripcion);
                ps.setString(3, linkVideo.isEmpty() ? null : linkVideo); // Manejo de nulo
                
                ps.executeUpdate();
                
                JOptionPane.showMessageDialog(this, "Ejercicio '" + nombre + "' agregado con éxito.");
                cargarTablaEjercicios(); 
                limpiarCampos(); 
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al insertar ejercicio: " + ex.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void cargarTablaEjercicios() {
        DefaultTableModel modelo = new DefaultTableModel();
        modelo.setColumnIdentifiers(new Object[]{"ID", "Nombre", "Descripción", "Video"});
        
        try {
            String sql = "SELECT id_ejercicio, nombre, descripcion, link_video FROM ejercicios";
            try (PreparedStatement ps = con.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                
                while (rs.next()) {
                    modelo.addRow(new Object[]{
                        rs.getInt("id_ejercicio"),
                        rs.getString("nombre"),
                        rs.getString("descripcion"),
                        rs.getString("link_video")
                    });
                }
            }
            tableEjercicios.setModel(modelo);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar ejercicios: " + e.getMessage());
        }
    }

    private void limpiarCampos() {
        txtNombre.setText("");
        txtDescripcion.setText("");
        txtLinkVideo.setText("");
    }

    public JPanel getPanel1() {
        return panel1;
    }
}
