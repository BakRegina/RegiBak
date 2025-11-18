package gimnasio;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class productos extends JPanel {
    private JPanel panel1;
    private JTextField txtNombreProducto;
    private JTextField txtCategoria;
    private JTextField txtPrecio;
    private JTextField txtStock;
    private JButton btnAgregar;
    private JTable tableProductos;
    
    private Connection con = null;

    public productos() {
        initComponents();
        setLayout(new BorderLayout());
        add(panel1, BorderLayout.CENTER);
        
        con = conexion.conectar();
        if (con != null) {
            cargarTablaProductos();
        }

        if (btnAgregar != null) {
            btnAgregar.addActionListener(this::agregarProducto);
        }
    }

    private void initComponents() {
        panel1 = new JPanel(new BorderLayout());
        txtNombreProducto = new JTextField(20);
        txtCategoria = new JTextField(20);
        txtPrecio = new JTextField(10);
        txtStock = new JTextField(10);
        btnAgregar = new JButton("Agregar Producto");
        tableProductos = new JTable();
        
        JPanel inputPanel = new JPanel(new FlowLayout());
        inputPanel.add(new JLabel("Nombre:"));
        inputPanel.add(txtNombreProducto);
        inputPanel.add(new JLabel("Categoría:"));
        inputPanel.add(txtCategoria);
        inputPanel.add(new JLabel("Precio:"));
        inputPanel.add(txtPrecio);
        inputPanel.add(new JLabel("Stock:"));
        inputPanel.add(txtStock);
        inputPanel.add(btnAgregar);

        panel1.add(inputPanel, BorderLayout.NORTH);
        panel1.add(new JScrollPane(tableProductos), BorderLayout.CENTER);
    }

    private void agregarProducto(ActionEvent e) {
        if (con == null) return;

        String nombre = txtNombreProducto.getText().trim();
        String categoria = txtCategoria.getText().trim();
        String precioStr = txtPrecio.getText().trim();
        String stockStr = txtStock.getText().trim();

        if (nombre.isEmpty() || precioStr.isEmpty() || stockStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            double precio = Double.parseDouble(precioStr);
            int stock = Integer.parseInt(stockStr);
            
            String sql = "INSERT INTO productos (nombre, categoria, precio, stock) VALUES (?, ?, ?, ?)";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, nombre);
                ps.setString(2, categoria);
                ps.setDouble(3, precio);
                ps.setInt(4, stock);
                
                ps.executeUpdate();
                
                JOptionPane.showMessageDialog(this, "Producto '" + nombre + "' agregado con éxito.");
                cargarTablaProductos(); 
                limpiarCamposProductos();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "El Precio y el Stock deben ser números válidos.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al insertar producto: " + ex.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void cargarTablaProductos() {
        DefaultTableModel modelo = new DefaultTableModel();
        modelo.setColumnIdentifiers(new Object[]{"ID", "Nombre", "Categoría", "Precio", "Stock"});
        
        try {
            String sql = "SELECT id_producto, nombre, categoria, precio, stock FROM productos";
            try (PreparedStatement ps = con.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                
                while (rs.next()) {
                    modelo.addRow(new Object[]{
                        rs.getInt("id_producto"),
                        rs.getString("nombre"),
                        rs.getString("categoria"),
                        rs.getDouble("precio"),
                        rs.getInt("stock")
                    });
                }
            }
            tableProductos.setModel(modelo);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar productos: " + e.getMessage());
        }
    }

    private void limpiarCamposProductos() {
        txtNombreProducto.setText("");
        txtCategoria.setText("");
        txtPrecio.setText("");
        txtStock.setText("");
    }

    public JPanel getPanel1() {
        return panel1;
    }
}
