/*import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class clientes extends JFrame {
    private JPanel panel1;
    private JTextField textField1;
    private JButton buscarButton;
    private JTable table1;
    private JButton agregarButton;
    private JButton modificarButton;
    private JButton eliminarButton;
    private JTextField dniTextField;
    private JTextField nombreTextField;
    private JTextField telefonoTextField;
    private JTextField apellidoTextField;
    private JTextField fechaDeInicioTextField;
    private JTextField fechaDeVencimientoTextField;
    private JButton limpiarButton;
    private JButton volverButton;
    private JButton validarButton;


    private Connection con = null;
    private PreparedStatement ps = null;
    private ResultSet rs = null;

    public clientes(String nombre_usuario) {

        setTitle("Gestión de Clientes");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Establecer conexión
        con = conexion.conectar();

        // Cargar datos al iniciar
        cargarTabla();

        // (solo implementás el código dentro de cada método generado)
        agregarButton.addActionListener(e -> agregarCliente());
        modificarButton.addActionListener(e -> modificarCliente());
        eliminarButton.addActionListener(e -> eliminarCliente());
        limpiarButton.addActionListener(e -> limpiarCampos());
        buscarButton.addActionListener(e -> buscarCliente());
        validarButton.addActionListener(e -> validarMembresia());
        volverButton.addActionListener(e -> {
            dispose();
            new admi("clientes",nombre_usuario).setVisible(true);
        });
        // Listener para seleccionar una fila en la tabla
        table1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int fila = table1.getSelectedRow();
                if (fila >= 0) {
                    dniTextField.setText(table1.getValueAt(fila, 1).toString());
                    nombreTextField.setText(table1.getValueAt(fila, 2).toString());
                    apellidoTextField.setText(table1.getValueAt(fila, 3).toString());
                    telefonoTextField.setText(table1.getValueAt(fila, 4).toString());
                    fechaDeInicioTextField.setText(table1.getValueAt(fila, 5).toString());
                    fechaDeVencimientoTextField.setText(table1.getValueAt(fila, 6).toString());
                }
            }
        });
    }
    private void cargarTabla() {
        DefaultTableModel modelo = new DefaultTableModel();
        modelo.setColumnIdentifiers(new Object[]{"ID", "DNI", "Nombre", "Apellido", "Teléfono", "Fecha Inicio", "Fecha Vencimiento"
        });

        try {
            String sql = "SELECT * FROM usuario_clientes";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                modelo.addRow(new Object[]{
                        rs.getInt("id_cliente"),
                        rs.getString("dni"),
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getString("telefono"),
                        rs.getString("fecha_inicio"),
                        rs.getString("fecha_vencimiento")
                });
            }
            table1.setModel(modelo);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar clientes: " + e.getMessage());
        }
    }
    private void agregarCliente() {
        String fechaInicio = fechaDeInicioTextField.getText().trim();
        String fechaVenc = fechaDeVencimientoTextField.getText().trim();

        if (!fechaInicio.matches("\\d{4}[-/]\\d{2}[-/]\\d{2}") || !fechaVenc.matches("\\d{4}[-/]\\d{2}[-/]\\d{2}")) {
            JOptionPane.showMessageDialog(this, "Las fechas deben tener formato AAAA-MM-DD o AAAA/MM/DD");
            return;
        }

        fechaInicio = fechaInicio.replace('/', '-');
        fechaVenc = fechaVenc.replace('/', '-');

        String sql = "INSERT INTO usuario_clientes (dni, nombre, apellido, telefono, fecha_inicio, fecha_vencimiento) VALUES (?, ?, ?, ?, ?, ?)";
        try {
            ps = con.prepareStatement(sql);
            ps.setString(1, dniTextField.getText());
            ps.setString(2, nombreTextField.getText());
            ps.setString(3, apellidoTextField.getText());
            ps.setString(4, telefonoTextField.getText());
            ps.setString(5, fechaInicio);
            ps.setString(6, fechaVenc);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Cliente agregado correctamente");
            cargarTabla();
            limpiarCampos();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al agregar cliente: " + e.getMessage());
        }
    }
    private void modificarCliente() {
        int fila = table1.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un cliente para modificar");
            return;
        }

        int id = Integer.parseInt(table1.getValueAt(fila, 0).toString());
        String sql = "UPDATE usuario_clientes SET dni=?, nombre=?, apellido=?, telefono=?, fecha_inicio=?, fecha_vencimiento=? WHERE id_cliente=?";
        try {
            ps = con.prepareStatement(sql);
            ps.setString(1, dniTextField.getText());
            ps.setString(2, nombreTextField.getText());
            ps.setString(3, apellidoTextField.getText());
            ps.setString(4, telefonoTextField.getText());
            ps.setString(5, fechaDeInicioTextField.getText());
            ps.setString(6, fechaDeVencimientoTextField.getText());
            ps.setInt(7, id);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Cliente modificado correctamente");
            cargarTabla();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al modificar cliente: " + e.getMessage());
        }

    }
    private void eliminarCliente() {
        int fila = table1.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un cliente para eliminar");
            return;
        }

        int id = Integer.parseInt(table1.getValueAt(fila, 0).toString());
        String sql = "DELETE FROM usuario_clientes WHERE id_cliente=?";

        try {
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Cliente eliminado correctamente");
            cargarTabla();
            limpiarCampos();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al eliminar cliente: " + e.getMessage());
        }
    }
    private void buscarCliente() {
        String texto = textField1.getText().trim();
        DefaultTableModel modelo = new DefaultTableModel();
        modelo.setColumnIdentifiers(new Object[]{
                "ID", "DNI", "Nombre", "Apellido", "Teléfono", "Fecha Inicio", "Fecha Vencimiento"
        });

        try {
            String sql = texto.isEmpty()
                    ? "SELECT * FROM usuario_clientes"
                    : "SELECT * FROM usuario_clientes WHERE dni LIKE ? OR nombre LIKE ?";
            ps = con.prepareStatement(sql);

            if (!texto.isEmpty()) {
                ps.setString(1, "%" + texto + "%");
                ps.setString(2, "%" + texto + "%");
            }

            rs = ps.executeQuery();
            while (rs.next()) {
                modelo.addRow(new Object[]{
                        rs.getInt("id_cliente"),
                        rs.getString("dni"),
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getString("telefono"),
                        rs.getString("fecha_inicio"),
                        rs.getString("fecha_vencimiento")
                });
            }

            table1.setModel(modelo);

            if (modelo.getRowCount() == 0 && !texto.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No se encontró ningún cliente con ese DNI o nombre.");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al buscar cliente: " + e.getMessage());
        }
    } private void limpiarCampos() {
        dniTextField.setText("");
        nombreTextField.setText("");
        apellidoTextField.setText("");
        telefonoTextField.setText("");
        fechaDeInicioTextField.setText("");
        fechaDeVencimientoTextField.setText("");
        textField1.setText("");
    }

    private void validarMembresia() {
        String dni = dniTextField.getText().trim();
        if (dni.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese el DNI para validar la membresía.");
            return;
        }

        try {
            String sql = "SELECT fecha_vencimiento FROM usuario_clientes WHERE dni = ?";
            ps = con.prepareStatement(sql);
            ps.setString(1, dni);
            rs = ps.executeQuery();

            if (rs.next()) {
                LocalDate fechaVenc = LocalDate.parse(rs.getString("fecha_vencimiento"));
                long diasRestantes = ChronoUnit.DAYS.between(LocalDate.now(), fechaVenc);

                if (diasRestantes > 0)
                    JOptionPane.showMessageDialog(this, "La membresía vence en " + diasRestantes + " días.");
                else if (diasRestantes == 0)
                    JOptionPane.showMessageDialog(this, "La membresía vence hoy. ¡Último día!");
                else
                    JOptionPane.showMessageDialog(this, "La membresía está vencida hace " + Math.abs(diasRestantes) + " días.");
            } else {
                JOptionPane.showMessageDialog(this, "No se encontró un cliente con ese DNI.");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al validar membresía: " + e.getMessage());
        }
    }
    public JPanel getPanel1() {
        return panel1;
    }
}*/

////////////////////////////////////////////////////////////7
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.awt.*; // Asegurarse de importar java.awt.

public class clientes extends JFrame {
    private JPanel panel1;
    private JTextField textField1;
    private JButton buscarButton;
    private JTable table1;
    private JButton agregarButton;
    private JButton modificarButton;
    private JButton eliminarButton;
    private JTextField dniTextField;
    private JTextField nombreTextField;
    private JTextField telefonoTextField;
    private JTextField apellidoTextField;
    private JTextField fechaDeInicioTextField;
    private JTextField fechaDeVencimientoTextField;
    private JButton limpiarButton;
    private JButton volverButton;
    private JButton validarButton;

    private Connection con = null;
    private PreparedStatement ps = null;
    private ResultSet rs = null;

    public clientes(String nombre_usuario) {

        // Nota: Al usar esta clase dentro de admi.java, la configuración de JFrame (setTitle, setSize)
        // no tiene efecto, solo usamos getPanel1().

        // Establecer conexión
        con = conexion.conectar();

        // Cargar datos al iniciar, solo si la conexión fue exitosa
        if (con != null) {
            cargarTabla();
        }


        // (solo implementás el código dentro de cada método generado)
        agregarButton.addActionListener(e -> agregarCliente());
        modificarButton.addActionListener(e -> modificarCliente());
        eliminarButton.addActionListener(e -> eliminarCliente());
        limpiarButton.addActionListener(e -> limpiarCampos());
        buscarButton.addActionListener(e -> buscarCliente());
        validarButton.addActionListener(e -> validarMembresia());

        // 🚨 Lógica comentada: El botón "Volver" no debe reiniciar el JFrame de admi.
        /*
        volverButton.addActionListener(e -> {
            dispose();
            new admi("clientes",nombre_usuario).setVisible(true);
        });
        */

        // Listener para seleccionar una fila en la tabla
        table1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int fila = table1.getSelectedRow();
                if (fila >= 0) {
                    dniTextField.setText(table1.getValueAt(fila, 1).toString());
                    nombreTextField.setText(table1.getValueAt(fila, 2).toString());
                    apellidoTextField.setText(table1.getValueAt(fila, 3).toString());
                    telefonoTextField.setText(table1.getValueAt(fila, 4).toString());
                    fechaDeInicioTextField.setText(table1.getValueAt(fila, 5).toString());
                    fechaDeVencimientoTextField.setText(table1.getValueAt(fila, 6).toString());
                }
            }
        });
    }
    // ... (cargarTabla, agregarCliente, modificarCliente, eliminarCliente, buscarCliente, limpiarCampos son idénticos y funcionales) ...

    private void cargarTabla() {
        DefaultTableModel modelo = new DefaultTableModel();
        modelo.setColumnIdentifiers(new Object[]{"ID", "DNI", "Nombre", "Apellido", "Teléfono", "Fecha Inicio", "Fecha Vencimiento"
        });

        try {
            String sql = "SELECT id_cliente, dni, nombre, apellido, telefono, fecha_inicio, fecha_vencimiento FROM usuario_clientes";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                modelo.addRow(new Object[]{
                        rs.getInt("id_cliente"),
                        rs.getString("dni"),
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getString("telefono"),
                        rs.getString("fecha_inicio"),
                        rs.getString("fecha_vencimiento")
                });
            }
            table1.setModel(modelo);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(panel1, "Error al cargar clientes: " + e.getMessage());
        }
    }
    private void agregarCliente() {
        String fechaInicio = fechaDeInicioTextField.getText().trim();
        String fechaVenc = fechaDeVencimientoTextField.getText().trim();

        if (!fechaInicio.matches("\\d{4}[-/]\\d{2}[-/]\\d{2}") || !fechaVenc.matches("\\d{4}[-/]\\d{2}[-/]\\d{2}")) {
            JOptionPane.showMessageDialog(panel1, "Las fechas deben tener formato AAAA-MM-DD o AAAA/MM/DD");
            return;
        }

        fechaInicio = fechaInicio.replace('/', '-');
        fechaVenc = fechaVenc.replace('/', '-');

        // Nota: Asegúrate que tu tabla usuario_clientes tiene los campos para las fechas.
        String sql = "INSERT INTO usuario_clientes (dni, nombre, apellido, telefono, fecha_inicio, fecha_vencimiento) VALUES (?, ?, ?, ?, ?, ?)";
        try {
            ps = con.prepareStatement(sql);
            ps.setString(1, dniTextField.getText());
            ps.setString(2, nombreTextField.getText());
            ps.setString(3, apellidoTextField.getText());
            ps.setString(4, telefonoTextField.getText());
            ps.setString(5, fechaInicio);
            ps.setString(6, fechaVenc);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(panel1, "Cliente agregado correctamente");
            cargarTabla();
            limpiarCampos();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(panel1, "Error al agregar cliente: " + e.getMessage());
        }
    }
    private void modificarCliente() {
        int fila = table1.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(panel1, "Seleccione un cliente para modificar");
            return;
        }

        int id = Integer.parseInt(table1.getValueAt(fila, 0).toString());
        String sql = "UPDATE usuario_clientes SET dni=?, nombre=?, apellido=?, telefono=?, fecha_inicio=?, fecha_vencimiento=? WHERE id_cliente=?";
        try {
            ps = con.prepareStatement(sql);
            ps.setString(1, dniTextField.getText());
            ps.setString(2, nombreTextField.getText());
            ps.setString(3, apellidoTextField.getText());
            ps.setString(4, telefonoTextField.getText());
            ps.setString(5, fechaDeInicioTextField.getText());
            ps.setString(6, fechaDeVencimientoTextField.getText());
            ps.setInt(7, id);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(panel1, "Cliente modificado correctamente");
            cargarTabla();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(panel1, "Error al modificar cliente: " + e.getMessage());
        }

    }
    private void eliminarCliente() {
        int fila = table1.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(panel1, "Seleccione un cliente para eliminar");
            return;
        }

        int id = Integer.parseInt(table1.getValueAt(fila, 0).toString());
        String sql = "DELETE FROM usuario_clientes WHERE id_cliente=?";

        try {
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(panel1, "Cliente eliminado correctamente");
            cargarTabla();
            limpiarCampos();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(panel1, "Error al eliminar cliente: " + e.getMessage());
        }
    }
    private void buscarCliente() {
        String texto = textField1.getText().trim();
        DefaultTableModel modelo = new DefaultTableModel();
        modelo.setColumnIdentifiers(new Object[]{
                "ID", "DNI", "Nombre", "Apellido", "Teléfono", "Fecha Inicio", "Fecha Vencimiento"
        });

        try {
            String sql = texto.isEmpty()
                    ? "SELECT id_cliente, dni, nombre, apellido, telefono, fecha_inicio, fecha_vencimiento FROM usuario_clientes"
                    : "SELECT id_cliente, dni, nombre, apellido, telefono, fecha_inicio, fecha_vencimiento FROM usuario_clientes WHERE dni LIKE ? OR nombre LIKE ?";
            ps = con.prepareStatement(sql);

            if (!texto.isEmpty()) {
                ps.setString(1, "%" + texto + "%");
                ps.setString(2, "%" + texto + "%");
            }

            rs = ps.executeQuery();
            while (rs.next()) {
                modelo.addRow(new Object[]{
                        rs.getInt("id_cliente"),
                        rs.getString("dni"),
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getString("telefono"),
                        rs.getString("fecha_inicio"),
                        rs.getString("fecha_vencimiento")
                });
            }

            table1.setModel(modelo);

            if (modelo.getRowCount() == 0 && !texto.isEmpty()) {
                JOptionPane.showMessageDialog(panel1, "No se encontró ningún cliente con ese DNI o nombre.");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(panel1, "Error al buscar cliente: " + e.getMessage());
        }
    }
    private void limpiarCampos() {
        dniTextField.setText("");
        nombreTextField.setText("");
        apellidoTextField.setText("");
        telefonoTextField.setText("");
        fechaDeInicioTextField.setText("");
        fechaDeVencimientoTextField.setText("");
        textField1.setText("");
    }

    private void validarMembresia() {
        // Usa los campos de texto del formulario para el DNI, pero solo si no está vacío.
        String dni = dniTextField.getText().trim();
        if (dni.isEmpty()) {
            JOptionPane.showMessageDialog(panel1, "Ingrese el DNI para validar la membresía.");
            return;
        }

        try {
            String sql = "SELECT fecha_vencimiento FROM usuario_clientes WHERE dni = ?";
            ps = con.prepareStatement(sql);
            ps.setString(1, dni);
            rs = ps.executeQuery();

            if (rs.next()) {
                // Se asume que la columna fecha_vencimiento es compatible con LocalDate.parse
                LocalDate fechaVenc = LocalDate.parse(rs.getString("fecha_vencimiento"));
                long diasRestantes = ChronoUnit.DAYS.between(LocalDate.now(), fechaVenc);

                if (diasRestantes > 0)
                    JOptionPane.showMessageDialog(panel1, "La membresía vence en " + diasRestantes + " días.");
                else if (diasRestantes == 0)
                    JOptionPane.showMessageDialog(panel1, "La membresía vence hoy. ¡Último día!");
                else
                    JOptionPane.showMessageDialog(panel1, "La membresía está vencida hace " + Math.abs(diasRestantes) + " días.");
            } else {
                JOptionPane.showMessageDialog(panel1, "No se encontró un cliente con ese DNI.");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(panel1, "Error al validar membresía: " + e.getMessage());
        }
    }

    // Método que admi.java necesita para cargar este panel
    public JPanel getPanel1() {
        return panel1;
    }
}























