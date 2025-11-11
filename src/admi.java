import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class admi extends JFrame {
    private JPanel panel1; // Panel principal que contendrá todos los elementos del formulario
    private JButton cerrarSesionButton; // Botón para cerrar sesión
    private JTabbedPane Pestanias;// Pestañas del panel principal (Clientes, Entrenadores, etc.)
    private JLabel Jlabel1;// Etiqueta superior de bienvenida
    private String tipo;

    public admi(String tipo,String nombre_usuario) {
        this.tipo = tipo;
        setTitle("Panel de Administración - Gimnasio");
        setContentPane(panel1); // Conecta el .form con el JFrame
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        JPanel panel = new JPanel(new BorderLayout());
        JLabel bienvenida = new JLabel("Bienvenido, " + nombre_usuario + " (" + tipo + ")");
        bienvenida.setHorizontalAlignment(SwingConstants.CENTER);
        bienvenida.setFont(new Font("Arial", Font.BOLD, 24));

        panel.add(bienvenida, BorderLayout.CENTER);
        cerrarSesionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int respuesta = JOptionPane.showConfirmDialog(
                        admi.this,
                        "¿Desea cerrar sesión?",
                        "Confirmar salida",
                        JOptionPane.YES_NO_OPTION
                );

                if (respuesta == JOptionPane.YES_OPTION) {
                    dispose();
                }
            }
        });
        Pestanias.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int index = Pestanias.getSelectedIndex();
                if (index < 0) return;

                String tabName = Pestanias.getTitleAt(index);

                switch (tabName) {
                    case "Clientes":
                        if(tipo=="profesores"){
                            JOptionPane.showMessageDialog(panel1, "No visible para profesores");
                            return;
                        }
                        mostrarContenido(new clientes(nombre_usuario).getPanel1(), index);
                        break;
                    case "Entrenadores":
                        if(tipo=="clientes"){
                            JOptionPane.showMessageDialog(panel1, "No visible para clientes");
                            return;
                        }
                        mostrarContenido(new entrenadores(nombre_usuario).getPanel1(), index);
                        break;
                    /*case "Ejercicios":
                        if(tipo=="")
                        mostrarContenido(new ejercicios().getPanel1(), index);
                        break;*/
                    case "Rutinas":
                        if(tipo=="clientes"){
                            JOptionPane.showMessageDialog(panel1, "No visible para clientes");
                            return;
                        }
                        mostrarContenido(new rutinas(nombre_usuario).getPanel1(), index);
                        break;
                    case "Productos":
                        mostrarContenido(new productos(nombre_usuario).getPanel1(), index);
                        break;
                    //case "Ventas":
                    //  mostrarContenido(new ventas().getPanel1(), index);
                    // break;
                }
            }
        });
    }

    //Carga el panel del .form correspondiente dentro del tab actual
    private void mostrarContenido(JPanel panelContenido, int index) {
        Pestanias.setComponentAt(index, panelContenido);
        Pestanias.revalidate();
        Pestanias.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            admi ventana = new admi("admin","admin");
            ventana.setVisible(true);
        });
    }
    //Getter para que se pueda usar el panel desde otras clases
    public JPanel getPanel1() {
        return panel1;
    }
}
