/*import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class entrenadores extends JFrame  {
    private JPanel panel1;
    private JTextField textField1;
    private JButton buscarButton;
    private JButton volverButton;
    private JTextField NombreTextField;
    private JTextField DniTextField;
    private JTextField ApellidoTextField;
    private JTextField EspecialidadTextField;
    private JButton agregarButton;
    private JButton modificarButton;
    private JButton eliminarButton;
    private JButton limpiarButton;
    private JTable table1;

    Connection con = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    public entrenadores(String nombre_usuario) {
        setTitle("Gestión de Entrenadores - Gimnasio");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        con = conexion.conectar(); // conexión a la base
        cargarTabla();

        //boton volver
        volverButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new admi("entrenadores",nombre_usuario).setVisible(true);
            }
        });

        // Al hacer clic en una fila, carga los datos
        table1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int fila = table1.getSelectedRow();
                if (fila >= 0) {
                    DniTextField.setText(table1.getValueAt(fila, 1).toString());
                    NombreTextField.setText(table1.getValueAt(fila, 2).toString());
                    ApellidoTextField.setText(table1.getValueAt(fila, 3).toString());
                    EspecialidadTextField.setText(table1.getValueAt(fila, 4).toString());
                }
            }
        });
        agregarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                agregarEntrenador();
            }
        });
        modificarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                modificarEntrenador();
            }
        });
        eliminarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                eliminarEntrenador();
            }
        });
        limpiarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                limpiarCampos();
            }
        });
        buscarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buscarEntrenador();
            }
        });
    }
        private void cargarTabla() {
            DefaultTableModel modelo = new DefaultTableModel();
            modelo.setColumnIdentifiers(new Object[]{
                    "ID", "DNI", "Nombre", "Apellido", "Especialidad"
            });

            try {
                String sql = "SELECT * FROM usuario_entrenador";
                ps = con.prepareStatement(sql);
                rs = ps.executeQuery();

                while (rs.next()) {
                    modelo.addRow(new Object[]{
                            rs.getInt("id_profesor"),
                            rs.getString("dni"),
                            rs.getString("nombre"),
                            rs.getString("apellido"),
                            rs.getString("especialidad"),
                    });
                }
                table1.setModel(modelo);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al cargar entrenadores: " + e.getMessage());
            }
        }
        //Agrega al entrenador
        private void agregarEntrenador() {
            String sql = "INSERT INTO usuario_entrenador (dni, nombre, apellido, especialidad) VALUES (?, ?, ?, ?)";

            try {
                ps = con.prepareStatement(sql);
                ps.setString(1, DniTextField.getText());
                ps.setString(2, NombreTextField.getText());
                ps.setString(3, ApellidoTextField.getText());
                ps.setString(4, EspecialidadTextField.getText());
                ps.executeUpdate();

                JOptionPane.showMessageDialog(this, "Entrenador agregado correctamente.");
                cargarTabla();
                limpiarCampos();

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al agregar entrenador: " + e.getMessage());
            }
        }
        // --- MODIFICAR ENTRENADOR ---
        private void modificarEntrenador() {
            int fila = table1.getSelectedRow();
            if (fila < 0) {
                JOptionPane.showMessageDialog(this, "Seleccione un entrenador para modificar.");
                return;
            }

            int id = Integer.parseInt(table1.getValueAt(fila, 0).toString());
            String sql = "UPDATE usuario_entrenador SET dni=?, nombre=?, apellido=?,especialidad=? WHERE id_profesor=?";

            try {
                ps = con.prepareStatement(sql);
                ps.setString(1, DniTextField.getText());
                ps.setString(2, NombreTextField.getText());
                ps.setString(3, ApellidoTextField.getText());
                ps.setString(4, EspecialidadTextField.getText());
                ps.setInt(5, id);
                ps.executeUpdate();

                JOptionPane.showMessageDialog(this, "Entrenador modificado correctamente.");
                cargarTabla();

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al modificar entrenador: " + e.getMessage());
            }
        }
        // --- ELIMINAR ENTRENADOR ---
        private void eliminarEntrenador() {
            int fila = table1.getSelectedRow();
            if (fila < 0) {
                JOptionPane.showMessageDialog(this, "Seleccione un entrenador para eliminar.");
                return;
            }

            int id = Integer.parseInt(table1.getValueAt(fila, 0).toString());
            String sql = "DELETE FROM usuario_entrenador WHERE id_profesor=?";

            try {
                ps = con.prepareStatement(sql);
                ps.setInt(1, id);
                ps.executeUpdate();

                JOptionPane.showMessageDialog(this, "Entrenador eliminado correctamente.");
                cargarTabla();
                limpiarCampos();

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al eliminar entrenador: " + e.getMessage());
            }
        }
        private void buscarEntrenador() {
            String texto = textField1.getText().trim();
            DefaultTableModel modelo = new DefaultTableModel();
            modelo.setColumnIdentifiers(new Object[]{
                    "ID", "DNI", "Nombre", "Apellido", "Especialidad"
            });

            try {
                String sql;
                if (texto.isEmpty()) {
                    sql = "SELECT * FROM usuario_entrenador";
                    ps = con.prepareStatement(sql);
                } else {
                    sql = "SELECT * FROM usuario_entrenador WHERE dni LIKE ? OR nombre LIKE ?";
                    ps = con.prepareStatement(sql);
                    ps.setString(1, "%" + texto + "%");
                    ps.setString(2, "%" + texto + "%");
                }

                rs = ps.executeQuery();

                while (rs.next()) {
                    modelo.addRow(new Object[]{
                            rs.getInt("id_profesor"),
                            rs.getString("dni"),
                            rs.getString("nombre"),
                            rs.getString("apellido"),
                            rs.getString("especialidad"),
                    });
                }

                table1.setModel(modelo);

                if (modelo.getRowCount() == 0 && !texto.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "No se encontró ningún entrenador con ese DNI o nombre.");
                }

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al buscar entrenador: " + e.getMessage());
            }
        }
        // --- LIMPIAR CAMPOS ---
        private void limpiarCampos() {
            DniTextField.setText("");
            NombreTextField.setText("");
            ApellidoTextField.setText("");
            EspecialidadTextField.setText("");
            textField1.setText("");
        }
        public JPanel getPanel1() {
            return panel1;
        }
    }
*/
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
import java.awt.*; // Asegurarse de importar java.awt.


public class entrenadores extends JFrame  {
    private JPanel panel1;
    private JTextField textField1;
    private JButton buscarButton;
    private JButton volverButton;
    private JTextField NombreTextField;
    private JTextField DniTextField;
    private JTextField ApellidoTextField;
    private JTextField EspecialidadTextField;
    private JButton agregarButton;
    private JButton modificarButton;
    private JButton eliminarButton;
    private JButton limpiarButton;
    private JTable table1;

    Connection con = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    public entrenadores(String nombre_usuario) {

        // La configuración de JFrame (setTitle, setSize, setDefaultCloseOperation)
        // no tiene efecto cuando se usa dentro de JTabbedPane, solo usamos getPanel1().

        // Establecer conexión
        con = conexion.conectar();

        // Cargar datos al iniciar, solo si la conexión fue exitosa
        if (con != null) {
            cargarTabla();
        }

        // 🚨 Lógica comentada: El botón "Volver" no debe reiniciar el JFrame de admi.
        /*
        volverButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new admi("entrenadores",nombre_usuario).setVisible(true);
            }
        });
        */

        // Al hacer clic en una fila, carga los datos
        table1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int fila = table1.getSelectedRow();
                if (fila >= 0) {
                    DniTextField.setText(table1.getValueAt(fila, 1).toString());
                    NombreTextField.setText(table1.getValueAt(fila, 2).toString());
                    ApellidoTextField.setText(table1.getValueAt(fila, 3).toString());
                    EspecialidadTextField.setText(table1.getValueAt(fila, 4).toString());
                }
            }
        });

        // Implementación de ActionListeners
        agregarButton.addActionListener(e -> agregarEntrenador());
        modificarButton.addActionListener(e -> modificarEntrenador());
        eliminarButton.addActionListener(e -> eliminarEntrenador());
        limpiarButton.addActionListener(e -> limpiarCampos());
        buscarButton.addActionListener(e -> buscarEntrenador());
    }

    private void cargarTabla() {
        DefaultTableModel modelo = new DefaultTableModel();
        modelo.setColumnIdentifiers(new Object[]{
                "ID", "DNI", "Nombre", "Apellido", "Especialidad"
        });

        try {
            String sql = "SELECT id_profesor, dni, nombre, apellido, especialidad FROM usuario_entrenador";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                modelo.addRow(new Object[]{
                        rs.getInt("id_profesor"),
                        rs.getString("dni"),
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getString("especialidad"),
                });
            }
            table1.setModel(modelo);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(panel1, "Error al cargar entrenadores: " + e.getMessage());
        }
    }

    //Agrega al entrenador
    private void agregarEntrenador() {
        String sql = "INSERT INTO usuario_entrenador (dni, nombre, apellido, especialidad) VALUES (?, ?, ?, ?)";

        try {
            ps = con.prepareStatement(sql);
            ps.setString(1, DniTextField.getText());
            ps.setString(2, NombreTextField.getText());
            ps.setString(3, ApellidoTextField.getText());
            ps.setString(4, EspecialidadTextField.getText());
            ps.executeUpdate();

            JOptionPane.showMessageDialog(panel1, "Entrenador agregado correctamente.");
            cargarTabla();
            limpiarCampos();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(panel1, "Error al agregar entrenador: " + e.getMessage());
        }
    }
    // --- MODIFICAR ENTRENADOR ---
    private void modificarEntrenador() {
        int fila = table1.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(panel1, "Seleccione un entrenador para modificar.");
            return;
        }

        int id = Integer.parseInt(table1.getValueAt(fila, 0).toString());
        String sql = "UPDATE usuario_entrenador SET dni=?, nombre=?, apellido=?,especialidad=? WHERE id_profesor=?";

        try {
            ps = con.prepareStatement(sql);
            ps.setString(1, DniTextField.getText());
            ps.setString(2, NombreTextField.getText());
            ps.setString(3, ApellidoTextField.getText());
            ps.setString(4, EspecialidadTextField.getText());
            ps.setInt(5, id);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(panel1, "Entrenador modificado correctamente.");
            cargarTabla();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(panel1, "Error al modificar entrenador: " + e.getMessage());
        }
    }
    // --- ELIMINAR ENTRENADOR ---
    private void eliminarEntrenador() {
        int fila = table1.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(panel1, "Seleccione un entrenador para eliminar.");
            return;
        }

        int id = Integer.parseInt(table1.getValueAt(fila, 0).toString());
        String sql = "DELETE FROM usuario_entrenador WHERE id_profesor=?";

        try {
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(panel1, "Entrenador eliminado correctamente.");
            cargarTabla();
            limpiarCampos();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(panel1, "Error al eliminar entrenador: " + e.getMessage());
        }
    }
    private void buscarEntrenador() {
        String texto = textField1.getText().trim();
        DefaultTableModel modelo = new DefaultTableModel();
        modelo.setColumnIdentifiers(new Object[]{
                "ID", "DNI", "Nombre", "Apellido", "Especialidad"
        });

        try {
            String sql;
            if (texto.isEmpty()) {
                sql = "SELECT id_profesor, dni, nombre, apellido, especialidad FROM usuario_entrenador";
                ps = con.prepareStatement(sql);
            } else {
                sql = "SELECT id_profesor, dni, nombre, apellido, especialidad FROM usuario_entrenador WHERE dni LIKE ? OR nombre LIKE ?";
                ps = con.prepareStatement(sql);
                ps.setString(1, "%" + texto + "%");
                ps.setString(2, "%" + texto + "%");
            }

            rs = ps.executeQuery();

            while (rs.next()) {
                modelo.addRow(new Object[]{
                        rs.getInt("id_profesor"),
                        rs.getString("dni"),
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getString("especialidad"),
                });
            }

            table1.setModel(modelo);

            if (modelo.getRowCount() == 0 && !texto.isEmpty()) {
                JOptionPane.showMessageDialog(panel1, "No se encontró ningún entrenador con ese DNI o nombre.");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(panel1, "Error al buscar entrenador: " + e.getMessage());
        }
    }
    // --- LIMPIAR CAMPOS ---
    private void limpiarCampos() {
        DniTextField.setText("");
        NombreTextField.setText("");
        ApellidoTextField.setText("");
        EspecialidadTextField.setText("");
        textField1.setText("");
    }

    // Método que admi.java necesita para cargar este panel
    public JPanel getPanel1() {
        return panel1;
    }
}
