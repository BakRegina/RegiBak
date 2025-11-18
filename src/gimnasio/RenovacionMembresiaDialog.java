
package gimnasio;
import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;

public class RenovacionMembresiaDialog extends JDialog {

    private JPanel panelDialog;
    private JLabel lblCliente;
    private JComboBox<String> cmbDuracion;
    private JButton btnConfirmar;

    private final int idCliente;
    private final ClientesPanel panelPadre;
    private JPanel panel1;

    public RenovacionMembresiaDialog(JFrame parent, int idCliente, String nombreCliente, ClientesPanel panelPadre) {
        super(parent, "Renovar Membresía", true);
        this.idCliente = idCliente;
        this.panelPadre = panelPadre;

        initComponents(nombreCliente);

        btnConfirmar.addActionListener(e -> renovar());

        setContentPane(panelDialog);
        setSize(400, 200);
        setResizable(false);
        setLocationRelativeTo(parent);
    }

    private void initComponents(String nombreCliente) {
        panelDialog = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));

        lblCliente = new JLabel("Renovando para: " + nombreCliente);
        lblCliente.setFont(new Font("Arial", Font.BOLD, 14));

        cmbDuracion = new JComboBox<>(new String[]{"1 Mes", "3 Meses", "6 Meses", "12 Meses"});
        btnConfirmar = new JButton("Confirmar Pago y Renovar");

        panelDialog.add(lblCliente);
        panelDialog.add(new JLabel("Seleccionar Duración:"));
        panelDialog.add(cmbDuracion);
        panelDialog.add(btnConfirmar);
    }

    private void renovar() {
        String seleccion = (String) cmbDuracion.getSelectedItem();
        int meses = 0;

        if (seleccion.contains("1 Mes")) meses = 1;
        else if (seleccion.contains("3 Meses")) meses = 3;
        else if (seleccion.contains("6 Meses")) meses = 6;
        else if (seleccion.contains("12 Meses")) meses = 12;

        if (meses == 0) return;

        Connection con = null;
        try {
            con = conexion.conectar();

            LocalDate fechaInicio = LocalDate.now();
            String sqlFecha = "SELECT fecha_vencimiento FROM membresias WHERE id_cliente = ? ORDER BY fecha_vencimiento DESC LIMIT 1";

            try (PreparedStatement psFecha = con.prepareStatement(sqlFecha)) {
                psFecha.setInt(1, idCliente);
                try (ResultSet rs = psFecha.executeQuery()) {
                    if (rs.next()) {
                        LocalDate ultimaFecha = rs.getTimestamp("fecha_vencimiento").toLocalDateTime().toLocalDate();
                        if (ultimaFecha.isAfter(fechaInicio)) {
                            fechaInicio = ultimaFecha.plusDays(1);
                        }
                    }
                }
            }

            LocalDate nuevaFechaVencimiento = fechaInicio.plusMonths(meses);

            String sqlInsert = "INSERT INTO membresias (id_cliente, fecha_inicio, fecha_vencimiento) VALUES (?, ?, ?)";

            try (PreparedStatement psInsert = con.prepareStatement(sqlInsert)) {
                psInsert.setInt(1, idCliente);

                psInsert.setTimestamp(2, Timestamp.valueOf(fechaInicio.atStartOfDay()));
                psInsert.setTimestamp(3, Timestamp.valueOf(nuevaFechaVencimiento.atStartOfDay()));

                psInsert.executeUpdate();

                JOptionPane.showMessageDialog(this,
                        "Membresía renovada con éxito.\nNueva fecha de vencimiento: " + nuevaFechaVencimiento.toString(),
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);

                panelPadre.recargarDatosCliente(idCliente);
                dispose();
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error de DB al renovar membresía: " + e.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (con != null) con.close();
            } catch (SQLException ex) { }
        }
    }
}