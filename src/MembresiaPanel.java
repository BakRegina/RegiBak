import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class MembresiaPanel extends JPanel {

    private final String dniCliente;
    private JLabel lblEstado;

    public MembresiaPanel(String dniCliente) {
        this.dniCliente = dniCliente;
        setLayout(new BorderLayout(10, 10));

        // Estilo del panel
        setBackground(new Color(240, 240, 240));
        setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        // Título
        JLabel lblTitulo = new JLabel("Estado de Membresía");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 28));
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        add(lblTitulo, BorderLayout.NORTH);

        // Contenido
        JPanel panelContenido = new JPanel(new GridLayout(2, 1, 10, 10));
        panelContenido.setOpaque(false);

        lblEstado = new JLabel("Cargando estado...");
        lblEstado.setFont(new Font("Arial", Font.PLAIN, 20));
        lblEstado.setHorizontalAlignment(SwingConstants.CENTER);

        panelContenido.add(new JLabel("DNI: " + dniCliente, SwingConstants.CENTER));
        panelContenido.add(lblEstado);

        add(panelContenido, BorderLayout.CENTER);

        // Llamar a la función para cargar datos de la DB
        cargarEstadoMembresia();
    }

    private void cargarEstadoMembresia() {
        try (Connection con = conexion.conectar()) {

            // Usamos la lógica de validación de membresía adaptada
            String sql = "SELECT fecha_vencimiento FROM usuario_clientes WHERE dni = ?";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, dniCliente);
                try (ResultSet rs = ps.executeQuery()) {

                    if (rs.next()) {
                        LocalDate fechaVenc = LocalDate.parse(rs.getString("fecha_vencimiento"));
                        long diasRestantes = ChronoUnit.DAYS.between(LocalDate.now(), fechaVenc);

                        if (diasRestantes > 0) {
                            lblEstado.setText("¡Membresía Activa! Vence en " + diasRestantes + " días.");
                            lblEstado.setForeground(new Color(34, 139, 34)); // Verde
                        } else if (diasRestantes == 0) {
                            lblEstado.setText("¡Membresía Vence HOY! Por favor renueve.");
                            lblEstado.setForeground(new Color(255, 140, 0)); // Naranja
                        } else {
                            lblEstado.setText("Membresía Vencida hace " + Math.abs(diasRestantes) + " días.");
                            lblEstado.setForeground(Color.RED);
                        }
                    } else {
                        lblEstado.setText("Error: DNI no encontrado en la base de datos.");
                        lblEstado.setForeground(Color.BLACK);
                    }
                }
            }
        } catch (SQLException e) {
            lblEstado.setText("Error de conexión o consulta DB.");
            lblEstado.setForeground(Color.RED);
            e.printStackTrace();
        }
    }
}
