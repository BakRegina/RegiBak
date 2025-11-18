
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
package gimnasio;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class login extends JFrame {
    
    // Asumo que estos componentes están en tu archivo .form con estos nombres:
    private JPanel mainPanel; 
    private JTextField txtUsuario;
    private JPasswordField txtContrasena;
    private JButton btnLogin;
    
    // Colores de Estilo (pueden ser aplicados manualmente en el .form o aquí)
    private final Color COLOR_FONDO = new Color(30, 30, 30); // Gris muy oscuro/negro
    private final Color COLOR_TEXTO = Color.WHITE;
    private final Color COLOR_ACCENTO = new Color(50, 200, 50); // Verde Lima vibrante

    public login() {
        setTitle("Gimnasio - Acceso Único");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        
        // El IDE debe generar initComponents() y enlazar los componentes
        initComponents();
        
        // --- ESTILOS MANUALES APLICADOS AL DISEÑO ---
        
        if (mainPanel != null) {
            mainPanel.setBackground(COLOR_FONDO);
        }

        // Estilizar campos de texto
        if (txtUsuario != null) {
            applyStyle(txtUsuario);
        }
        if (txtContrasena != null) {
            applyStyle(txtContrasena);
        }
        
        // Estilizar botón
        if (btnLogin != null) {
            btnLogin.setBackground(COLOR_ACCENTO);
            btnLogin.setForeground(COLOR_FONDO); // Texto oscuro en botón claro
            btnLogin.setFont(new Font("Arial", Font.BOLD, 16));
            btnLogin.setFocusPainted(false);
            btnLogin.addActionListener(this::iniciarSesion);
        }
        
        // Si tienes etiquetas (JLabel) en el mainPanel, estilízalas:
        // Por ejemplo, si tienes un JLabel llamado lblTitulo, haz:
        // if (lblTitulo != null) lblTitulo.setForeground(COLOR_ACCENTO);
        
        setContentPane(mainPanel);
    }
    
    // Método auxiliar para aplicar estilos a JTextField/JPasswordField
    private void applyStyle(JComponent component) {
        component.setBackground(new Color(60, 60, 60)); // Fondo oscuro
        component.setForeground(COLOR_TEXTO);
        if (component instanceof JTextField) {
            ((JTextField) component).setCaretColor(COLOR_ACCENTO);
        }
        component.setBorder(BorderFactory.createLineBorder(COLOR_ACCENTO, 1));
    }
    
    // El IDE generará esto al crear el .form
    private void initComponents() {
        // CÓDIGO GENERADO AUTOMÁTICAMENTE POR TU IDE
    }


    private void iniciarSesion(ActionEvent e) {
        String usuario = txtUsuario.getText().trim();
        String contrasena = new String(txtContrasena.getPassword());
        
        // La clave es obtener el ROL
        String rol = verificarCredenciales(usuario, contrasena);

        if (rol.equals("FALLO")) {
            JOptionPane.showMessageDialog(this, "Usuario o contraseña incorrectos.", "Error de Login", JOptionPane.ERROR_MESSAGE);
        } else {
            // Creamos la instancia de admi con el rol y el identificador
            admi adminWindow = new admi(usuario, "ID_UNICO_BD", rol); 
            adminWindow.setVisible(true);
            this.dispose(); 
        }
    }

    private String verificarCredenciales(String user, String pass) {
        Connection con = conexion.conectar();
        if (con == null) return "FALLO";
        
        // 1. Verificar ADMIN (Ejemplo simple, asumiendo un usuario 'admin' o una tabla aparte)
        if (user.equals("admin") && pass.equals("123")) {
             return "ADMIN"; 
        }
        
        // 2. Verificar ENTRENADOR
        try {
            String sqlE = "SELECT nombre FROM usuario_entrenador WHERE nombre_usuario = ? AND contrasena = ?";
            try (PreparedStatement ps = con.prepareStatement(sqlE)) {
                ps.setString(1, user);
                ps.setString(2, pass); 
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return "ENTRENADOR";
                }
            }
        } catch (SQLException ex) { /* Manejo de error de BD */ }

        // 3. Verificar CLIENTE
        try {
            String sqlC = "SELECT nombre FROM usuario_clientes WHERE nombre_usuario = ? AND contrasena = ?";
            try (PreparedStatement ps = con.prepareStatement(sqlC)) {
                ps.setString(1, user);
                ps.setString(2, pass);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return "CLIENTE";
                }
            }
        } catch (SQLException ex) { /* Manejo de error de BD */ }

        return "FALLO";
    }

    public static void main(String[] args) {
        try {
            // Aplicar el Look and Feel oscuro de FlatLaf para mejorar la estética global
            UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatDarkLaf()); 
        } catch (Exception ex) {
            System.err.println("Failed to initialize FlatLaf");
        }
        
        SwingUtilities.invokeLater(() -> {
            // Usamos el nombre de tu clase: login
            new login().setVisible(true);
        });
    }
}
