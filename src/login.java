
/*
import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class login extends JFrame { //CORRECCIÓN: Extiende JFrame
    private JPanel panel1; // Este panel se enlaza automáticamente desde login.form
    private JTextField textoUsuario;
    private JPasswordField textoPassword;
    private JButton ingresarButton;
    private JButton cancelarButton;
    private JButton registrarseButton;

    public login() {
        // CONFIGURACIÓN BÁSICA DE LA VENTANA (SOLO UNA VEZ)
        setTitle("Acceso de Usuarios (Login)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Enlace del diseño del .form y ajuste de tamaño
        if (panel1 != null) {
            setContentPane(panel1);
            pack(); // Ajusta el tamaño según el contenido del formulario
            setLocationRelativeTo(null);
        } else {
            // Alternativa si el .form falla, para que la ventana aparezca.
            System.err.println("Advertencia: El panel1 es NULL. Usando setSize() de emergencia.");
            setSize(400, 300);
            setLocationRelativeTo(null);
        }

        // ActionListeners
        ingresarButton.addActionListener(e -> intentarLogin());

        registrarseButton.addActionListener(e -> {
            RegistroUsuarios registroUsuarios = new RegistroUsuarios();
            registroUsuarios.setVisible(true);
            this.dispose();
        });

        cancelarButton.addActionListener(e -> {
            this.dispose(); // ⬅️ Código limpio
        });
    }

    private void intentarLogin() {
        String usuario = textoUsuario.getText().trim();
        String password = new String(textoPassword.getPassword()).trim();

        if (usuario.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(panel1, "Usuario y contraseña son obligatorios.");
            return;
        }

        try (Connection con = conexion.conectar()) {

            // 🔹 1. Verificar si es CLIENTE
            String sqlCliente = "SELECT nombre_usuario FROM usuario_clientes WHERE nombre_usuario = ? AND contrasena = ?";
            try (PreparedStatement ps = con.prepareStatement(sqlCliente)) {
                ps.setString(1, usuario);
                ps.setString(2, password);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    String nombreUsuario = rs.getString("nombre_usuario");
                    JOptionPane.showMessageDialog(panel1, "Bienvenido cliente: " + nombreUsuario);
                    abrirVentanaAdmi("clientes", nombreUsuario);
                    return;
                }
            }

            // 🔹 2. Verificar si es ENTRENADOR
            String sqlEntrenador = "SELECT nombre_usuario FROM usuario_entrenador WHERE nombre_usuario = ? AND contrasena = ?";
            try (PreparedStatement ps = con.prepareStatement(sqlEntrenador)) {
                ps.setString(1, usuario);
                ps.setString(2, password);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    String nombreUsuario = rs.getString("nombre_usuario");
                    JOptionPane.showMessageDialog(panel1, "Bienvenido profesor: " + nombreUsuario);
                    abrirVentanaAdmi("profesor", nombreUsuario);
                    return;
                }
            }

            // 🔹 3. Verificar si es ADMIN
            if (usuario.equals("admin") && password.equals("admin123")) {
                JOptionPane.showMessageDialog(panel1, "Bienvenido administrador.");
                abrirVentanaAdmi("admin", "Administrador");
                return;
            }

            // 🔹 4. Si no es ninguno
            JOptionPane.showMessageDialog(panel1, "Credenciales inválidas. Verifique usuario y contraseña.");

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(panel1, "Error al conectar con la base de datos: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void abrirVentanaAdmi(String tipo, String nombreUsuario) {
        this.dispose();
        admi ventanaAdmi = new admi(tipo, nombreUsuario);
        ventanaAdmi.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new login().setVisible(true); // ⬅️ Punto de inicio
        });
    }
}*/
import  javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class login extends JFrame {
    private JPanel panel1;
    private JTextField textoUsuario;
    private JPasswordField textoPassword;
    private JButton ingresarButton;
    private JButton cancelarButton;
    private JButton registrarseButton;

    public login() {
        // CONFIGURACIÓN BÁSICA DE LA VENTANA (SOLO UNA VEZ)
        setTitle("Acceso de Usuarios (Login)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Enlace del diseño del .form y ajuste de tamaño
        if (panel1 != null) {
            setContentPane(panel1);
            pack(); // Ajusta el tamaño según el contenido del formulario
            setLocationRelativeTo(null);
        } else {
            // Alternativa si el .form falla, para que la ventana aparezca.
            System.err.println("Advertencia: El panel1 es NULL. Usando setSize() de emergencia.");
            setSize(400, 300);
            setLocationRelativeTo(null);
        }

        // ActionListeners
        ingresarButton.addActionListener(e -> intentarLogin());

        registrarseButton.addActionListener(e -> {
            RegistroUsuarios registroUsuarios = new RegistroUsuarios();
            registroUsuarios.setVisible(true);
            this.dispose();
        });

        cancelarButton.addActionListener(e -> {
            this.dispose();
        });
    }

    private void intentarLogin() {
        String usuario = textoUsuario.getText().trim(); // En Clientes, este es el DNI.
        String password = new String(textoPassword.getPassword()).trim();

        if (usuario.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(panel1, "Usuario/DNI y contraseña son obligatorios.");
            return;
        }

        try (Connection con = conexion.conectar()) {

            // 🔹 1. Verificar si es CLIENTE (VALIDACIÓN POR DNI)
            String sqlCliente = "SELECT nombre, apellido FROM usuario_clientes WHERE dni = ? AND contrasena = ?";
            try (PreparedStatement ps = con.prepareStatement(sqlCliente)) {
                ps.setString(1, usuario); // ⬅️ DNI
                ps.setString(2, password);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    String nombre = rs.getString("nombre");
                    String apellido = rs.getString("apellido");
                    String nombreCompleto = nombre + " " + apellido;

                    JOptionPane.showMessageDialog(panel1, "Bienvenido cliente: " + nombreCompleto);
                    abrirVentanaAdmi("clientes", usuario); // ⬅️ Mandamos el DNI como identificador
                    return;
                }
            }

            // 🔹 2. Verificar si es ENTRENADOR (Sigue siendo por nombre de usuario)
            String sqlEntrenador = "SELECT nombre_usuario FROM usuario_entrenador WHERE nombre_usuario = ? AND contrasena = ?";
            try (PreparedStatement ps = con.prepareStatement(sqlEntrenador)) {
                ps.setString(1, usuario);
                ps.setString(2, password);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    String nombreUsuario = rs.getString("nombre_usuario");
                    JOptionPane.showMessageDialog(panel1, "Bienvenido profesor: " + nombreUsuario);
                    abrirVentanaAdmi("profesor", nombreUsuario);
                    return;
                }
            }

            // 🔹 3. Verificar si es ADMIN
            if (usuario.equals("admin") && password.equals("admin123")) {
                JOptionPane.showMessageDialog(panel1, "Bienvenido administrador.");
                abrirVentanaAdmi("admin", "Administrador");
                return;
            }

            // 🔹 4. Si no es ninguno
            JOptionPane.showMessageDialog(panel1, "Credenciales inválidas. Verifique usuario y contraseña.");

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(panel1, "Error al conectar con la base de datos: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void abrirVentanaAdmi(String tipo, String identificador) {
        // Cierra el login y abre la ventana correspondiente
        this.dispose();
        //ABRO la ventana admi
        admi ventanaAdmi = new admi(tipo, identificador);
        ventanaAdmi.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new login().setVisible(true);
        });
    }
}