package gimnasio;
import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class RegistroUsuarios extends JFrame {

    private JTabbedPane pestañas;
    private JPanel panelClientes;
    private JPanel panelEntrenadores;

    // Campos para clientes
    private JTextField dniClienteField, nombreClienteField, apellidoClienteField, usuarioClienteField, telefonoClienteField;
    private JPasswordField contrasenaClienteField;
    private JButton btnRegistrarCliente;
    private JButton btnVolverC;

    // Campos para entrenadores
    private JTextField dniEntrenadorField, nombreEntrenadorField, apellidoEntrenadorField, especialidadField, usuarioEntrenadorField;
    private JPasswordField contrasenaEntrenadorField;
    private JButton btnRegistrarEntrenador;
    private JButton btnVolverE;

    public RegistroUsuarios() {
        setTitle("Registro de Usuarios");
        setSize(500, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        pestañas = new JTabbedPane();

        crearPanelClientes();
        crearPanelEntrenadores();

        pestañas.addTab("Clientes", panelClientes);
        pestañas.addTab("Entrenadores", panelEntrenadores);

        add(pestañas);
    }

    private void crearPanelClientes() {
        panelClientes = new JPanel(new BorderLayout(10, 10));
        panelClientes.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        JPanel form = new JPanel(new GridLayout(6, 2, 10, 10));

        dniClienteField = new JTextField();
        nombreClienteField = new JTextField();
        apellidoClienteField = new JTextField();
        telefonoClienteField = new JTextField();
        usuarioClienteField = new JTextField();
        contrasenaClienteField = new JPasswordField();

        form.add(new JLabel("DNI:"));
        form.add(dniClienteField);
        form.add(new JLabel("Nombre:"));
        form.add(nombreClienteField);
        form.add(new JLabel("Apellido:"));
        form.add(apellidoClienteField);
        form.add(new JLabel("Teléfono:"));
        form.add(telefonoClienteField);
        form.add(new JLabel("Usuario:"));
        form.add(usuarioClienteField);
        form.add(new JLabel("Contraseña:"));
        form.add(contrasenaClienteField);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        btnRegistrarCliente = new JButton("Registrar Cliente");
        btnVolverC = new JButton("Volver al login");
        botones.add(btnRegistrarCliente);
        botones.add(btnVolverC);

        // Acciones
        btnRegistrarCliente.addActionListener(e -> registrarCliente());
        btnVolverC.addActionListener(e -> {
            login r = new login();
            r.setVisible(true);
            dispose();
        });

        panelClientes.add(form, BorderLayout.CENTER);
        panelClientes.add(botones, BorderLayout.SOUTH);
    }

    private void crearPanelEntrenadores() {
        panelEntrenadores = new JPanel(new BorderLayout(10, 10));
        panelEntrenadores.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        JPanel form = new JPanel(new GridLayout(6, 2, 10, 10));

        dniEntrenadorField = new JTextField();
        nombreEntrenadorField = new JTextField();
        apellidoEntrenadorField = new JTextField();
        especialidadField = new JTextField();
        usuarioEntrenadorField = new JTextField();
        contrasenaEntrenadorField = new JPasswordField();

        form.add(new JLabel("DNI:"));
        form.add(dniEntrenadorField);
        form.add(new JLabel("Nombre:"));
        form.add(nombreEntrenadorField);
        form.add(new JLabel("Apellido:"));
        form.add(apellidoEntrenadorField);
        form.add(new JLabel("Especialidad:"));
        form.add(especialidadField);
        form.add(new JLabel("Usuario:"));
        form.add(usuarioEntrenadorField);
        form.add(new JLabel("Contraseña:"));
        form.add(contrasenaEntrenadorField);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        btnRegistrarEntrenador = new JButton("Registrar Entrenador");
        btnVolverE = new JButton("Volver al login");
        botones.add(btnRegistrarEntrenador);
        botones.add(btnVolverE);

        // Acciones
        btnRegistrarEntrenador.addActionListener(e -> registrarEntrenador());
        btnVolverE.addActionListener(e -> {
            login r = new login();
            r.setVisible(true);
            dispose();
        });

        panelEntrenadores.add(form, BorderLayout.CENTER);
        panelEntrenadores.add(botones, BorderLayout.SOUTH);
    }

        // MÉTODOS DE REGISTRO
    private void registrarCliente() {
        String dni = dniClienteField.getText().trim();
        String nombre = nombreClienteField.getText().trim();
        String apellido = apellidoClienteField.getText().trim();
        String telefono = telefonoClienteField.getText().trim();
        String usuario = usuarioClienteField.getText().trim();
        String contrasena = new String(contrasenaClienteField.getPassword()).trim();

        if (dni.isEmpty() || nombre.isEmpty() || apellido.isEmpty() ||
                telefono.isEmpty() || usuario.isEmpty() || contrasena.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios");
            return;
        }

        if (!dni.matches("\\d+")) {
            JOptionPane.showMessageDialog(this, "El DNI solo debe contener números");
            return;
        }

        if (!nombre.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$")) {
            JOptionPane.showMessageDialog(this, "El nombre solo debe contener letras");
            return;
        }

        if (!apellido.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$")) {
            JOptionPane.showMessageDialog(this, "El apellido solo debe contener letras");
            return;
        }

        if (!telefono.matches("\\d{6,15}")) {
            JOptionPane.showMessageDialog(this, "El teléfono debe contener entre 6 y 15 números");
            return;
        }

        try (Connection conn = conexion.conectar()) {
            PreparedStatement check = conn.prepareStatement(
                    "SELECT * FROM usuario_clientes WHERE dni = ? OR nombre_usuario = ?");
            check.setString(1, dni);
            check.setString(2, usuario);
            ResultSet rs = check.executeQuery();

            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "El DNI o usuario ya existen");
                return;
            }

            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO usuario_clientes (dni, nombre, apellido, telefono, nombre_usuario, contrasena) VALUES (?, ?, ?, ?, ?, ?)");
            ps.setString(1, dni);
            ps.setString(2, nombre);
            ps.setString(3, apellido);
            ps.setString(4, telefono);
            ps.setString(5, usuario);
            ps.setString(6, contrasena);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Cliente registrado correctamente");
            limpiarCamposCliente();
            login r = new login();
            r.setVisible(true);
            dispose();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al registrar cliente: " + ex.getMessage());
        }
    }

    private void registrarEntrenador() {
        String dni = dniEntrenadorField.getText().trim();
        String nombre = nombreEntrenadorField.getText().trim();
        String apellido = apellidoEntrenadorField.getText().trim();
        String especialidad = especialidadField.getText().trim();
        String usuario = usuarioEntrenadorField.getText().trim();
        String contrasena = new String(contrasenaEntrenadorField.getPassword()).trim();

        if (dni.isEmpty() || nombre.isEmpty() || apellido.isEmpty() ||
                especialidad.isEmpty() || usuario.isEmpty() || contrasena.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios");
            return;
        }

        if (!dni.matches("\\d+")) {
            JOptionPane.showMessageDialog(this, "El DNI solo debe contener números");
            return;
        }

        if (!nombre.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$")) {
            JOptionPane.showMessageDialog(this, "El nombre solo debe contener letras");
            return;
        }

        if (!apellido.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$")) {
            JOptionPane.showMessageDialog(this, "El apellido solo debe contener letras");
            return;
        }

        if (!especialidad.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$")) {
            JOptionPane.showMessageDialog(this, "La especialidad solo debe contener letras");
            return;
        }

        try (Connection conn = conexion.conectar()) {
            PreparedStatement check = conn.prepareStatement(
                    "SELECT * FROM usuario_entrenador WHERE dni = ? OR nombre_usuario = ?");
            check.setString(1, dni);
            check.setString(2, usuario);
            ResultSet rs = check.executeQuery();

            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "El DNI o usuario ya existen");
                return;
            }

            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO usuario_entrenador (dni, nombre, apellido, especialidad, nombre_usuario, contrasena) VALUES (?, ?, ?, ?, ?, ?)");
            ps.setString(1, dni);
            ps.setString(2, nombre);
            ps.setString(3, apellido);
            ps.setString(4, especialidad);
            ps.setString(5, usuario);
            ps.setString(6, contrasena);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Entrenador registrado correctamente");
            limpiarCamposEntrenador();
            login r = new login();
            r.setVisible(true);
            dispose();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al registrar entrenador: " + ex.getMessage());
        }
    }

    private void limpiarCamposCliente() {
        dniClienteField.setText("");
        nombreClienteField.setText("");
        apellidoClienteField.setText("");
        telefonoClienteField.setText("");
        usuarioClienteField.setText("");
        contrasenaClienteField.setText("");
    }

    private void limpiarCamposEntrenador() {
        dniEntrenadorField.setText("");
        nombreEntrenadorField.setText("");
        apellidoEntrenadorField.setText("");
        especialidadField.setText("");
        usuarioEntrenadorField.setText("");
        contrasenaEntrenadorField.setText("");
    }

}
