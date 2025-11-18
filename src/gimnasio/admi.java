package gimnasio;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class admi extends JFrame {
    private JPanel panel1;
    private JButton cerrarSesionButton;
    private JTabbedPane Pestanias;
    private JLabel Jlabel1;

    private final String rolUsuario;
    private final String nombreUsuario;

    private final Map<String, JPanel> paneles = new HashMap<>();

    public admi(String rol, String nombre_usuario, String s) {
        this.rolUsuario = rol.toLowerCase();
        this.nombreUsuario = nombre_usuario;

        setTitle("Panel de Administración - Gimnasio | Usuario: " + nombre_usuario);

        initComponents();

        setSize(1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);

        if (Jlabel1 != null) {
            Jlabel1.setText("Bienvenido, " + nombre_usuario + " (" + rol + ")");
        }

        configurarPanelesModulos();
        configurarVisibilidadPestanas();

        cerrarSesionButton.addActionListener(e -> {
            int respuesta = JOptionPane.showConfirmDialog(
                    admi.this,
                    "¿Desea cerrar sesión?",
                    "Confirmar salida",
                    JOptionPane.YES_NO_OPTION
            );
            if (respuesta == JOptionPane.YES_OPTION) {
                // Aquí deberías abrir la ventana de login
                dispose();
            }
        });

        setContentPane(panel1);
    }

    private void configurarPanelesModulos() {
        try {
            // Se usa ClientesPanel y se asume constructor sin argumentos
            paneles.put("Clientes", new ClientesPanel().getPanel1());
            paneles.put("Entrenadores", new entrenadores(nombreUsuario).getPanel1());
            paneles.put("Ejercicios", new ejercicios().getPanel1());
            paneles.put("Rutinas", new rutinas(nombreUsuario).getPanel1());
            paneles.put("Productos", new productos().getPanel1());
            paneles.put("Ventas", new ventas().getPanel1());
            paneles.put("Reportes de Progreso", new ReportesProgresoPanel().getPanel1());

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al inicializar un módulo. Verifique que la clase exista y tenga el constructor correcto: " + e.getMessage(),
                    "Error Fatal", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void configurarVisibilidadPestanas() {
        Pestanias.removeAll();

        boolean isAdmin = rolUsuario.equals("admin");
        boolean esCliente = rolUsuario.equals("clientes");
        boolean esentrenadores = rolUsuario.equals("profesores");

        if (isAdmin || esCliente) {
            Pestanias.addTab("Clientes", paneles.get("Clientes"));
        }

        if (paneles.containsKey("Ejercicios")) {
            Pestanias.addTab("Ejercicios", paneles.get("Ejercicios"));
        }

        if (isAdmin || esentrenadores) {
            Pestanias.addTab("Entrenadores", paneles.get("Entrenadores"));
        }

        if (isAdmin || esentrenadores || esCliente) {
            Pestanias.addTab("Rutinas", paneles.get("Rutinas"));
        }

        if (isAdmin || esCliente) {
            Pestanias.addTab("Productos", paneles.get("Productos"));
        }

        if (isAdmin) {
            Pestanias.addTab("Ventas", paneles.get("Ventas"));
        }

        if (paneles.containsKey("Reportes de Progreso")) {
            Pestanias.addTab("Reportes de Progreso", paneles.get("Reportes de Progreso"));
        }
    }

    private void initComponents() {
        // CÓDIGO GENERADO AUTOMÁTICAMENTE POR TU IDE
    }

    public JPanel getPanel1() {
        return panel1;
    }

}