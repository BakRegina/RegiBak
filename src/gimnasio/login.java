package gimnasio;
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

            //1. Verificar si es CLIENTE (VALIDACIÓN POR DNI)
            String sqlCliente = "SELECT nombre, apellido FROM usuario_clientes WHERE dni = ? AND contrasena = ?";
            try (PreparedStatement ps = con.prepareStatement(sqlCliente)) {
                ps.setString(1, usuario); // DNI
                ps.setString(2, password);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    String nombre = rs.getString("nombre");
                    String apellido = rs.getString("apellido");
                    String nombreCompleto = nombre + " " + apellido;

                    JOptionPane.showMessageDialog(panel1, "Bienvenido cliente: " + nombreCompleto);
                    abrirVentanaAdmi("clientes", usuario); // Mandamos el DNI como identificador
                    return;
                }
            }

            // 2. Verificar si es ENTRENADOR (Sigue siendo por nombre de usuario)
            String sqlEntrenador = "SELECT nombre_usuario FROM usuario_entrenador WHERE nombre_usuario = ? AND contrasena = ?";
            try (PreparedStatement ps = con.prepareStatement(sqlEntrenador)) {
                ps.setString(1, usuario);
                ps.setString(2, password);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    String nombreUsuario = rs.getString("nombre_usuario");
                    JOptionPane.showMessageDialog(panel1, "Bienvenido profesor: " + nombreUsuario);
                    abrirVentanaAdmi("profesores", nombreUsuario);
                    return;
                }
            }

            // 3. Verificar si es ADMIN
            if (usuario.equals("admin") && password.equals("admin123")) {
                JOptionPane.showMessageDialog(panel1, "Bienvenido administrador.");
                abrirVentanaAdmi("admin", "Administrador");
                return;
            }

            //4. Si no es ninguno
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
        admi ventanaAdmi = new admi(tipo, identificador,"pepito");
        ventanaAdmi.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new login().setVisible(true);
        });
    }
}
/*
package gimnasio;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class login extends JFrame {

    private JPanel mainPanel;
    private JTextField txtUsuario;
    private JPasswordField txtContrasena;
    private JButton btnLogin;

    private final Color COLOR_FONDO = new Color(30, 30, 30); // Gris muy oscuro/negro
    private final Color COLOR_TEXTO = Color.WHITE;
    private final Color COLOR_ACCENTO = new Color(50, 200, 50); // Verde Lima vibrante

    public login() {
        setTitle("Gimnasio - Acceso Único");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);

        initComponents();

        // --- ESTILOS MANUALES APLICADOS AL DISEÑO ---

        if (mainPanel != null) {
            mainPanel.setBackground(COLOR_FONDO);
        }

        if (txtUsuario != null) {
            applyStyle(txtUsuario);
        }
        if (txtContrasena != null) {
            applyStyle(txtContrasena);
        }

        if (btnLogin != null) {
            btnLogin.setBackground(COLOR_ACCENTO);
            btnLogin.setForeground(COLOR_FONDO); // Texto oscuro en botón claro
            btnLogin.setFont(new Font("Arial", Font.BOLD, 16));
            btnLogin.setFocusPainted(false);
            btnLogin.addActionListener(this::iniciarSesion);
        }

        setContentPane(mainPanel);
    }

    private void applyStyle(JComponent component) {
        component.setBackground(new Color(60, 60, 60));
        component.setForeground(COLOR_TEXTO);
        if (component instanceof JTextField) {
            ((JTextField) component).setCaretColor(COLOR_ACCENTO);
        }
        component.setBorder(BorderFactory.createLineBorder(COLOR_ACCENTO, 1));
    }

    private void initComponents() {
        // CÓDIGO GENERADO AUTOMÁTICAMENTE POR TU IDE
    }

    private void iniciarSesion(ActionEvent e) {
        String usuario = txtUsuario.getText().trim();
        String contrasena = new String(txtContrasena.getPassword());

        String rol = verificarCredenciales(usuario, contrasena);

        if (rol.equals("FALLO")) {
            JOptionPane.showMessageDialog(this, "Usuario o contraseña incorrectos.", "Error de Login", JOptionPane.ERROR_MESSAGE);
        } else {
            admi adminWindow = new admi(usuario, "ID_UNICO_BD", rol);
            adminWindow.setVisible(true);
            this.dispose();
        }
    }

    private String verificarCredenciales(String user, String pass) {
        try (Connection con = conexion.conectar()) {
            if (con == null) return "FALLO";

            // 1. Verificar ADMIN
            if (user.equals("admin") && pass.equals("123")) {
                return "ADMIN";
            }

            // 2. Verificar ENTRENADOR
            String sqlE = "SELECT nombre FROM usuario_entrenador WHERE nombre_usuario = ? AND contrasena = ?";
            try (PreparedStatement ps = con.prepareStatement(sqlE)) {
                ps.setString(1, user);
                ps.setString(2, pass);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return "ENTRENADOR";
                }
            }

            // 3. Verificar CLIENTE
            String sqlC = "SELECT nombre FROM usuario_clientes WHERE nombre_usuario = ? AND contrasena = ?";
            try (PreparedStatement ps = con.prepareStatement(sqlC)) {
                ps.setString(1, user);
                ps.setString(2, pass);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return "CLIENTE";
                }
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error de base de datos: " + ex.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        }

        return "FALLO";
    }

    public static void main(String[] args) {
        try {
            // Utiliza el Look and Feel 'Nimbus', que está incorporado en Java
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) {
            System.err.println("Error al aplicar Nimbus LnF.");
        }

        SwingUtilities.invokeLater(() -> {
            new login().setVisible(true);
        });
    }
}
/*
package gimnasio;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
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
    private JButton volverButton;
    private JButton registrarseButton;

    private final Color COLOR_FONDO = new Color(30, 30, 30); // Gris muy oscuro/negro
    private final Color COLOR_TEXTO = Color.WHITE;
    private final Color COLOR_ACCENTO = new Color(50, 200, 50); // Verde Lima vibrante

    public login() {
        setTitle("Gimnasio - Acceso Único");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);

        initComponents();

        // --- ESTILOS MANUALES APLICADOS AL DISEÑO ---

        if (panel1 != null) {
            panel1.setBackground(COLOR_FONDO);
        }

        if (textoUsuario != null) {
            applyStyle(textoUsuario);
        }
        if (textoPassword != null) {
            applyStyle(textoPassword);
        }

        if (ingresarButton != null) {
            ingresarButton.setBackground(COLOR_ACCENTO);
            ingresarButton.setForeground(COLOR_FONDO); // Texto oscuro en botón claro
            ingresarButton.setFont(new Font("Arial", Font.BOLD, 16));
            ingresarButton.setFocusPainted(false);
            ingresarButton.addActionListener(this::iniciarSesion);
        }

        setContentPane(panel1);
    }

    private void applyStyle(JComponent component) {
        component.setBackground(new Color(60, 60, 60));
        component.setForeground(COLOR_TEXTO);
        if (component instanceof JTextField) {
            ((JTextField) component).setCaretColor(COLOR_ACCENTO);
        }
        component.setBorder(BorderFactory.createLineBorder(COLOR_ACCENTO, 1));
    }

    private void initComponents() {
        // CÓDIGO GENERADO AUTOMÁTICAMENTE POR TU IDE
    }

    private void iniciarSesion(ActionEvent e) {
        String usuario = textoUsuario.getText().trim();
        String contrasena = new String(textoPassword.getPassword());

        String rol = verificarCredenciales(usuario, contrasena);

        if (rol.equals("FALLO")) {
            JOptionPane.showMessageDialog(this, "Usuario o contraseña incorrectos.", "Error de Login", JOptionPane.ERROR_MESSAGE);
        } else {
            admi adminWindow = new admi(usuario, "ID_UNICO_BD", rol);
            adminWindow.setVisible(true);
            this.dispose();
        }
    }

    private String verificarCredenciales(String user, String pass) {
        try (Connection con = conexion.conectar()) {
            if (con == null) return "FALLO";

            // 1. Verificar ADMIN
            if (user.equals("admin") && pass.equals("123")) {
                return "ADMIN";
            }

            // 2. Verificar ENTRENADOR
            String sqlE = "SELECT nombre FROM usuario_entrenador WHERE nombre_usuario = ? AND contrasena = ?";
            try (PreparedStatement ps = con.prepareStatement(sqlE)) {
                ps.setString(1, user);
                ps.setString(2, pass);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return "ENTRENADOR";
                }
            }

            // 3. Verificar CLIENTE
            String sqlC = "SELECT nombre FROM usuario_clientes WHERE nombre_usuario = ? AND contrasena = ?";
            try (PreparedStatement ps = con.prepareStatement(sqlC)) {
                ps.setString(1, user);
                ps.setString(2, pass);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return "CLIENTE";
                }
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error de base de datos: " + ex.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        }

        return "FALLO";
    }

    public static void main(String[] args) {
        try {
            // Utiliza el Look and Feel 'Nimbus', que está incorporado en Java
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) {
            System.err.println("Error al aplicar Nimbus LnF.");
        }

        SwingUtilities.invokeLater(() -> {
            new login().setVisible(true);
        });
    }
}*/