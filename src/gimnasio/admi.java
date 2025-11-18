/*package gimnasio;
import javax.swing.*;
import java.awt.*;

public class admi extends JFrame {

    // Componentes de la Interfaz
    private JTabbedPane Pestanias;
    private JPanel panel1;
    private JButton cerrarSesionButton;

    // CAMBIO 1: Campo para almacenar el nombre de usuario y hacerlo accesible en initComponents()
    private final String nombreUsuario;

    public admi(String nombreUsuario, String identificador) {
        // Asignación del nombre de usuario
        this.nombreUsuario = nombreUsuario;

        // Configuración de ventana
        setTitle("Gimnasio - Panel de Administración | Usuario: " + nombreUsuario);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        initComponents();
        setContentPane(panel1);
    }

    private void initComponents() {
        panel1 = new JPanel(new BorderLayout());
        Pestanias = new JTabbedPane();

        // Inicializar el botón, aunque no tenga ActionListener aquí
        cerrarSesionButton = new JButton("Cerrar Sesión");

        // Creamos un panel para el botón de Cerrar Sesión
        JPanel panelSur = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelSur.add(cerrarSesionButton);

        try {
            Pestanias.addTab("Clientes & Membresía", new ClientesPanel().getPanel1());
            Pestanias.addTab("Ejercicios", new ejercicios().getPanel1());
            Pestanias.addTab("Entrenadores", new entrenadores().getPanel1());

            // CAMBIO 2: LÍNEA AÑADIDA para integrar la pestaña de Rutinas
            // Se utiliza el campo 'nombreUsuario' para inicializar el panel de Rutinas.
            Pestanias.addTab("Rutinas", new rutinas(nombreUsuario).getPanel1());

            // Asumiendo que el resto de clases también tienen getPanel1() y extienden JPanel
            Pestanias.addTab("Productos", new productos().getPanel1());
            Pestanias.addTab("Ventas", new ventas().getPanel1());
            Pestanias.addTab("Reportes de Progreso", new ReportesProgresoPanel().getPanel1());

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar un módulo. Verifique la clase y la conexión a la base de datos: " + e.getMessage(),
                    "Error de Carga", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        panel1.add(Pestanias, BorderLayout.CENTER);
        panel1.add(panelSur, BorderLayout.SOUTH);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            String identificador = "A123";
            new admi("Admin", identificador).setVisible(true);
        });
    }

    public JPanel getPanel1()
    {
        return panel1;
    }
}*/
/*
package gimnasio;
import javax.swing.*;
import java.awt.*;

public class admi extends JFrame {

    private JTabbedPane Pestanias;
    private JPanel panel1;
    private JButton cerrarSesionButton;

    private final String nombreUsuario;
    private final String identificador;

    public admi(String nombreUsuario, String identificador) {
        this.nombreUsuario = nombreUsuario;
        this.identificador = identificador;

        setTitle("Gimnasio - Panel de Administración | Usuario: " + nombreUsuario);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        initComponents();
        setContentPane(panel1);
    }

    private void initComponents() {
        panel1 = new JPanel(new BorderLayout());
        Pestanias = new JTabbedPane();

        cerrarSesionButton = new JButton("Cerrar Sesión");

        JPanel panelSur = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelSur.add(cerrarSesionButton);

        // --- NUEVA LÓGICA DE VERIFICACIÓN DE ROL ---

        String rol = "ADMIN"; // Rol por defecto si no se reconoce

        if (this.identificador != null) {
            String idUpper = this.identificador.toUpperCase();
            if (idUpper.startsWith("C")) {
                rol = "CLIENTE";
            } else if (idUpper.startsWith("E")) {
                rol = "ENTRENADOR";
            }
        }

        // Flags para el control de pestañas
        boolean esCliente = rol.equals("CLIENTE");
        boolean esEntrenador = rol.equals("ENTRENADOR");
        boolean esAdmin = rol.equals("ADMIN"); // El administrador ve todo

        try {
            // 1. Clientes & Membresía
            if (esAdmin || esCliente) {
                Pestanias.addTab("Clientes & Membresía", new ClientesPanel().getPanel1());
            }

            // 2. Ejercicios (VISIBLE para todos)
            Pestanias.addTab("Ejercicios", new ejercicios().getPanel1());

            // 3. Entrenadores
            if (esAdmin || esEntrenador) {
                Pestanias.addTab("Entrenadores", new entrenadores().getPanel1());
            }

            // 4. Rutinas (VISIBLE para todos)
            Pestanias.addTab("Rutinas", new rutinas(nombreUsuario).getPanel1());

            // 5. Productos
            if (esAdmin || esCliente) {
                Pestanias.addTab("Productos", new productos().getPanel1());
            }

            // 6. Ventas
            if (esAdmin) {
                Pestanias.addTab("Ventas", new ventas().getPanel1());
            }

            // 7. Reportes de Progreso (VISIBLE para Entrenador y Cliente/Admin)
            Pestanias.addTab("Reportes de Progreso", new ReportesProgresoPanel().getPanel1());

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar un módulo. Verifique la clase: " + e.getMessage(),
                    "Error de Carga", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        panel1.add(Pestanias, BorderLayout.CENTER);
        panel1.add(panelSur, BorderLayout.SOUTH);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Ejemplo 1: ADMIN (identificador 'A' -> ve todo)
            // new admi("Admin", "A123").setVisible(true);

            // Ejemplo 2: ENTRENADOR (identificador 'E' -> ve Ejercicios, Entrenadores, Rutinas, Reportes)
            new admi("Carlos Entrenador", "E789").setVisible(true);

            // Ejemplo 3: CLIENTE (identificador 'C' -> ve Clientes, Ejercicios, Rutinas, Productos, Reportes)
            // new admi("Juan Cliente", "C456").setVisible(true);
        });
    }

    public JPanel getPanel1()
    {
        return panel1;
    }
}*/

package gimnasio;
import javax.swing.*;
import java.awt.*;

public class admi extends JFrame {

    private JTabbedPane Pestanias;
    private JPanel panel1;
    private JButton cerrarSesionButton;

    private final String nombreUsuario;
    private final String identificador;

    public admi(String nombreUsuario, String identificador) {
        this.nombreUsuario = nombreUsuario;
        this.identificador = identificador;

        setTitle("Gimnasio - Panel de Administración | Usuario: " + nombreUsuario);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        initComponents();
        setContentPane(panel1);
        setVisible(true);
    }

    private void initComponents() {
        panel1 = new JPanel(new BorderLayout());
        Pestanias = new JTabbedPane();

        cerrarSesionButton = new JButton("Cerrar Sesión");

        JPanel panelSur = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelSur.add(cerrarSesionButton);

        String rol = "ADMIN";

        if (this.identificador != null) {
            String idUpper = this.identificador.toUpperCase();
            if (idUpper.startsWith("C")) {
                rol = "CLIENTE";
            } else if (idUpper.startsWith("E")) {
                rol = "ENTRENADOR";
            }
        }

        boolean esCliente = rol.equals("CLIENTE");
        boolean esEntrenador = rol.equals("ENTRENADOR");
        boolean esAdmin = rol.equals("ADMIN");

        try {
            // 1. Clientes & Membresía
            // VISIBLE: Cliente, Admin
            if (esAdmin || esCliente) {
                Pestanias.addTab("Clientes & Membresía", new ClientesPanel().getPanel1());
            }

            // 2. Ejercicios
            // VISIBLE: Todos
            if (esAdmin || esCliente || esEntrenador) {
                Pestanias.addTab("Ejercicios", new ejercicios().getPanel1());
            }

            // 3. Entrenadores
            // VISIBLE: Entrenador, Admin
            if (esAdmin || esEntrenador) {
                Pestanias.addTab("Entrenadores", new entrenadores().getPanel1());
            }

            // 4. Rutinas
            // VISIBLE: Cliente, Entrenador, Admin
            if (esAdmin || esCliente || esEntrenador) {
                Pestanias.addTab("Rutinas", new rutinas(nombreUsuario).getPanel1());
            }

            // 5. Productos
            // VISIBLE: Cliente, Admin
            if (esAdmin || esCliente) {
                Pestanias.addTab("Productos", new productos().getPanel1());
            }

            // 6. Ventas
            // VISIBLE: Solo Admin
            if (esAdmin) {
                Pestanias.addTab("Ventas", new ventas().getPanel1());
            }

            // 7. Reportes de Progreso
            // VISIBLE: Cliente, Admin
            if (esAdmin || esCliente) {
                Pestanias.addTab("Reportes de Progreso", new ReporteProgresoPanel(this.identificador).getPanel1());
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar un módulo. Verifique la clase: " + e.getMessage(),
                    "Error de Carga", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        panel1.add(Pestanias, BorderLayout.CENTER);
        panel1.add(panelSur, BorderLayout.SOUTH);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Ejemplo de prueba:
            // Cliente:
            // new admi("Juan Cliente", "C456");

            // Entrenador:
            // new admi("Carlos Entrenador", "E789");

            // Admin:
            new admi("Admin", "A123");
        });
    }

    public JPanel getPanel1()
    {
        return panel1;
    }
}