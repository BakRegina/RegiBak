package gimnasio;
import javax.accessibility.AccessibleContext;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class productos extends JFrame { // Podría extender JPanel si se diseñó solo un panel
    private JPanel panel1;
    private JButton volverButton; // Se mantiene en el form, pero se anula su función
    private JTextField textField1; // Búsqueda
    private JButton buscarButton;
    private JTextField textField2; // Nombre
    private JTextField textField3; // Categoría
    private JTextField textField4; // Precio
    private JTextField textField5; // Stock
    private JButton agregarButton;
    private JButton modificarButton;
    private JButton eliminarButton;
    private JButton limpiarButton;
    private JTable table1;

    Connection con = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    public productos() {


        con = conexion.conectar(); // conexión a la base

        if (con != null) {
            cargarTabla();
        }

        // ActionListeners originales
        buscarButton.addActionListener(e -> buscarProducto());
        agregarButton.addActionListener(e -> agregarProducto());
        modificarButton.addActionListener(e -> modificarProducto());
        eliminarButton.addActionListener(e -> eliminarProducto());
        limpiarButton.addActionListener(e -> limpiarCampos());

        // Al hacer clic en una fila de la tabla, cargamos los datos en los campos
        table1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int fila = table1.getSelectedRow();
                if (fila >= 0) {
                    textField2.setText(table1.getValueAt(fila, 1).toString()); // Nombre
                    textField3.setText(table1.getValueAt(fila, 2).toString()); // Categoría
                    textField4.setText(table1.getValueAt(fila, 3).toString()); // Precio
                    textField5.setText(table1.getValueAt(fila, 4).toString()); // Stock
                }
            }
        });
    }

    private void limpiarCampos() {
    }

    private void buscarProducto() {
    }

    // El método 'volver' se elimina o se comenta.
    /* private void volver() { ... } */

    // --- CARGAR TABLA ---
    private void cargarTabla() {
        DefaultTableModel modelo = new DefaultTableModel();
        modelo.setColumnIdentifiers(new Object[]{"ID", "Nombre", "Categoría", "Precio", "Stock"});

        try {
            String sql = "SELECT id_producto, nombre, categoria, precio, stock FROM productos";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                modelo.addRow(new Object[]{
                        rs.getInt("id_producto"),
                        rs.getString("nombre"),
                        rs.getString("categoria"),
                        rs.getDouble("precio"),
                        rs.getInt("stock")
                });
            }
            table1.setModel(modelo);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(panel1, "Error al cargar productos: " + e.getMessage());
        }
    }

    // --- AGREGAR PRODUCTO ---
    private void agregarProducto() {
        String sql = "INSERT INTO productos (nombre, categoria, precio, stock) VALUES (?, ?, ?, ?)";

        try {
            ps = con.prepareStatement(sql);
            ps.setString(1, textField2.getText()); // Nombre
            ps.setString(2, textField3.getText()); // Categoría
            ps.setDouble(3, Double.parseDouble(textField4.getText())); // Precio
            ps.setInt(4, Integer.parseInt(textField5.getText())); // Stock
            ps.executeUpdate();

            JOptionPane.showMessageDialog(panel1, "Producto agregado correctamente.");
            cargarTabla();
            limpiarCampos();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(panel1, "Error al agregar producto: " + e.getMessage());
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(panel1, "Precio o stock inválido. Asegúrese de ingresar números.");
        }
    }

    // --- MODIFICAR PRODUCTO ---
    private void modificarProducto() {
        int fila = table1.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(panel1, "Seleccione un producto para modificar.");
            return;
        }

        int id = Integer.parseInt(table1.getValueAt(fila, 0).toString());
        String sql = "UPDATE productos SET nombre=?, categoria=?, precio=?, stock=? WHERE id_producto=?";

        try {
            ps = con.prepareStatement(sql);
            ps.setString(1, textField2.getText());
            ps.setString(2, textField3.getText());
            ps.setDouble(3, Double.parseDouble(textField4.getText()));
            ps.setInt(4, Integer.parseInt(textField5.getText()));
            ps.setInt(5, id);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(panel1, "Producto modificado correctamente.");
            cargarTabla();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(panel1, "Error al modificar producto: " + e.getMessage());
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(panel1, "Precio o stock inválido. Asegúrese de ingresar números.");
        }
    }
    // --- ELIMINAR PRODUCTO ---
    private void eliminarProducto() {
        AccessibleContext fila = table1.getAccessibleContext();
    }
    public JPanel getPanel1() {
        JPanel panelPrincipal = null;
        return panelPrincipal;
    }
}