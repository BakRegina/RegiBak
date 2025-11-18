
package gimnasio;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;


public class ClientesPanel extends JPanel {
    private JPanel panel1;
    private JTable tableClientes;
    private JTextField txtDni;
    private JTextField txtNombre;
    private JTextField txtApellido;
    private JTextField txtTelefono;
    private JTextField txtUsuario;
    private JTextField txtContrasena;
    private JButton btnAgregar;
    private JButton btnModificar;
    private JButton btnEliminar;
    private JButton btnLimpiar;
    private JButton btnRenovarMembresia;
    private JLabel lblMembresiaActual;
    private JButton btnBuscarCliente;

    private Connection con = null;

    public ClientesPanel() {
        initComponents();

        try {
            con = new conexion().conectar();
            if (con != null) {
                cargarClientes();
            } else {
                JOptionPane.showMessageDialog(this, "La conexión a la base de datos es nula.", "Error de Conexión", JOptionPane.ERROR_MESSAGE);
            }
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, "Error al conectar con la base de datos: " + e.getMessage(), "Error de Conexión", JOptionPane.ERROR_MESSAGE);
        }

        btnAgregar.addActionListener(e -> agregarCliente());
        btnModificar.addActionListener(e -> modificarCliente());
        btnEliminar.addActionListener(e -> eliminarCliente());
        btnLimpiar.addActionListener(e -> limpiarCampos());
        btnRenovarMembresia.addActionListener(e -> abrirDialogoRenovacion());
        btnBuscarCliente.addActionListener(e -> buscarCliente());

        tableClientes.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                seleccionarCliente();
            }
        });

        setLayout(new BorderLayout());
        add(panel1, BorderLayout.CENTER);
    }

    private void initComponents() {
        panel1 = new JPanel(new BorderLayout(10, 10));

        JPanel panelCampos = new JPanel(new GridLayout(3, 4, 10, 5));

        txtDni = new JTextField(15);
        txtNombre = new JTextField(15);
        txtApellido = new JTextField(15);
        txtTelefono = new JTextField(15);
        txtUsuario = new JTextField(15);
        txtContrasena = new JTextField(15);

        btnBuscarCliente = new JButton("Buscar");

        panelCampos.add(new JLabel("DNI:"));
        panelCampos.add(txtDni);
        panelCampos.add(new JLabel("Nombre:"));
        panelCampos.add(txtNombre);

        panelCampos.add(new JLabel("Apellido:"));
        panelCampos.add(txtApellido);
        panelCampos.add(new JLabel("Teléfono:"));
        panelCampos.add(txtTelefono);

        panelCampos.add(new JLabel("Usuario:"));
        panelCampos.add(txtUsuario);
        panelCampos.add(new JLabel("Contraseña:"));
        panelCampos.add(txtContrasena);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        btnAgregar = new JButton("Agregar");
        btnModificar = new JButton("Modificar");
        btnEliminar = new JButton("Eliminar");
        btnLimpiar = new JButton("Limpiar");
        btnRenovarMembresia = new JButton("Renovar Membresía");
        lblMembresiaActual = new JLabel("Seleccione un cliente.");

        panelBotones.add(btnAgregar);
        panelBotones.add(btnModificar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnLimpiar);
        panelBotones.add(btnRenovarMembresia);
        panelBotones.add(btnBuscarCliente);

        JPanel panelInfo = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelInfo.add(lblMembresiaActual);

        JPanel panelNorteCompleto = new JPanel(new BorderLayout());
        panelNorteCompleto.add(panelCampos, BorderLayout.NORTH);
        panelNorteCompleto.add(panelBotones, BorderLayout.CENTER);
        panelNorteCompleto.add(panelInfo, BorderLayout.SOUTH);

        tableClientes = new JTable();

        panel1.add(panelNorteCompleto, BorderLayout.NORTH);
        panel1.add(new JScrollPane(tableClientes), BorderLayout.CENTER);
    }

    private void cargarClientes() {
        if (con == null) return;

        String[] nombresColumnas = {"ID", "DNI", "Nombre", "Apellido", "Teléfono", "Usuario", "Membresía Vence"};
        DefaultTableModel modelo = new DefaultTableModel(null, nombresColumnas);
        tableClientes.setModel(modelo);

        String sql = "SELECT uc.id_cliente, uc.dni, uc.nombre, uc.apellido, uc.telefono, uc.nombre_usuario, MAX(m.fecha_vencimiento) " +
                "FROM usuario_clientes uc " +
                "LEFT JOIN membresias m ON uc.id_cliente = m.id_cliente " +
                "GROUP BY uc.id_cliente";

        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Object[] fila = new Object[nombresColumnas.length];
                fila[0] = rs.getInt("id_cliente");
                fila[1] = rs.getString("dni");
                fila[2] = rs.getString("nombre");
                fila[3] = rs.getString("apellido");
                fila[4] = rs.getString("telefono");
                fila[5] = rs.getString("nombre_usuario");

                Timestamp fechaVencimiento = rs.getTimestamp(7);
                fila[6] = (fechaVencimiento != null) ? fechaVencimiento.toLocalDateTime().toLocalDate().toString() : "Inactiva";

                modelo.addRow(fila);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar clientes: " + e.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void buscarCliente() {
        String dniBusqueda = txtDni.getText().trim();
        if (dniBusqueda.isEmpty()) {
            cargarClientes();
            return;
        }

        for (int i = 0; i < tableClientes.getRowCount(); i++) {
            if (tableClientes.getValueAt(i, 1).toString().equals(dniBusqueda)) {
                tableClientes.setRowSelectionInterval(i, i);
                seleccionarCliente();
                return;
            }
        }
        JOptionPane.showMessageDialog(this, "Cliente con DNI " + dniBusqueda + " no encontrado.", "Búsqueda Fallida", JOptionPane.INFORMATION_MESSAGE);
    }

    private void seleccionarCliente() {
        int fila = tableClientes.getSelectedRow();
        if (fila == -1) return;

        txtDni.setText(tableClientes.getValueAt(fila, 1).toString());
        txtNombre.setText(tableClientes.getValueAt(fila, 2).toString());
        txtApellido.setText(tableClientes.getValueAt(fila, 3).toString());
        txtTelefono.setText(tableClientes.getValueAt(fila, 4).toString());
        txtUsuario.setText(tableClientes.getValueAt(fila, 5).toString());

        txtContrasena.setText("");

        lblMembresiaActual.setText("Membresía Vence: " + tableClientes.getValueAt(fila, 6).toString());
    }

    private void limpiarCampos() {
        txtDni.setText("");
        txtNombre.setText("");
        txtApellido.setText("");
        txtTelefono.setText("");
        txtUsuario.setText("");
        txtContrasena.setText("");
        lblMembresiaActual.setText("Seleccione un cliente.");
        tableClientes.clearSelection();
    }

    private void agregarCliente() {

    }

    private void modificarCliente() {
        if (con == null) return;

        int filaSeleccionada = tableClientes.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un cliente de la tabla para modificar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idCliente = (int) tableClientes.getValueAt(filaSeleccionada, 0);

        String dni = txtDni.getText().trim();
        String nombre = txtNombre.getText().trim();
        String apellido = txtApellido.getText().trim();
        String telefono = txtTelefono.getText().trim();
        String usuario = txtUsuario.getText().trim();
        String contrasena = txtContrasena.getText();

        if (dni.isEmpty() || nombre.isEmpty() || apellido.isEmpty() || usuario.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Los campos DNI, Nombre, Apellido y Usuario son obligatorios.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            if (!dni.matches("\\d+")) {
                JOptionPane.showMessageDialog(this, "El DNI debe contener solo números.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (Exception e) {}

        String sql = "UPDATE usuario_clientes SET dni=?, nombre=?, apellido=?, telefono=?, nombre_usuario=? " +
                (contrasena.isEmpty() ? "" : ", contrasena=? ") +
                "WHERE id_cliente=?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            int index = 1;
            ps.setString(index++, dni);
            ps.setString(index++, nombre);
            ps.setString(index++, apellido);
            ps.setString(index++, telefono);
            ps.setString(index++, usuario);

            if (!contrasena.isEmpty()) {
                ps.setString(index++, contrasena);
            }

            ps.setInt(index, idCliente);

            int filasAfectadas = ps.executeUpdate();

            if (filasAfectadas > 0) {
                JOptionPane.showMessageDialog(this, "Cliente modificado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                cargarClientes();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo modificar el cliente.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al modificar cliente: " + e.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarCliente() {
        if (con == null) return;

        int filaSeleccionada = tableClientes.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un cliente de la tabla para eliminar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de que desea eliminar este cliente?",
                "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);

        if (confirmacion == JOptionPane.YES_OPTION) {
            int idCliente = (int) tableClientes.getValueAt(filaSeleccionada, 0);

            String sql = "DELETE FROM usuario_clientes WHERE id_cliente=?";

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, idCliente);

                int filasAfectadas = ps.executeUpdate();

                if (filasAfectadas > 0) {
                    JOptionPane.showMessageDialog(this, "Cliente eliminado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    limpiarCampos();
                    cargarClientes();
                } else {
                    JOptionPane.showMessageDialog(this, "No se pudo eliminar el cliente.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al eliminar cliente. Debe asegurar ON DELETE CASCADE en la BD. Detalle: " + e.getMessage(), "Error de Integridad", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void abrirDialogoRenovacion() {
        int filaSeleccionada = tableClientes.getSelectedRow();

        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un cliente de la tabla para renovar la membresía.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idCliente = (int) tableClientes.getValueAt(filaSeleccionada, 0);
        String nombreCliente = tableClientes.getValueAt(filaSeleccionada, 2).toString() + " " + tableClientes.getValueAt(filaSeleccionada, 3).toString();


    }
    public JPanel getPanel1()
    {
        return panel1;
    }

    public void recargarDatosCliente(int idCliente) {
    }
}