/*import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.net.URL; // Para cargar la imagen/GIF desde URL
import javax.swing.Icon; // Para usar el icono del GIF


public class ejercicios extends JFrame {

    // Componentes que DEBEN ser enlazados desde el .form
    private JPanel panelPrincipal;
    private JTextField txtNombre;
    private JTextArea txtDescripcion;
    private JTextField txtUrlGif;
    private JLabel lblPreviewGif; // Para mostrar la imagen/GIF cargado
    private JButton btnGuardar;
    private JButton btnCargarPreview; // Botón para probar la URL
    private JButton btnCancelar;
    private JPanel panel1;

    // El resto de componentes (JLabels, etc.) deben ser manejados por el .form

    public ejercicios() {
        // Configuraciones de la Ventana
        setTitle("Gestión de Ejercicios");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        //CRÍTICO: Enlazar el diseño del .form
        if (panelPrincipal != null) {
            setContentPane(panelPrincipal);
            pack();
            setLocationRelativeTo(null);
        } else {
            // Esto sucede si el enlace del .form falla.
            System.err.println("Error: panelPrincipal es NULL. Revisar el enlace con GestionEjercicios.form.");
            setSize(600, 450);
        }

        // --- ActionListeners ---
        btnGuardar.addActionListener(e -> guardarEjercicio());
        btnCancelar.addActionListener(e -> dispose());
        btnCargarPreview.addActionListener(e -> cargarPreviewGif());
    }

    // ======================================
    // LÓGICA DE NEGOCIO (Insertar en DB)
    // ======================================

    private void guardarEjercicio() {
        String nombre = txtNombre.getText().trim();
        String descripcion = txtDescripcion.getText().trim();
        String urlGif = txtUrlGif.getText().trim();
        // Nota: Dejamos link_video como NULL o vacío ya que el GIF cumple la función.
        String linkVideo = "";

        if (nombre.isEmpty() || descripcion.isEmpty() || urlGif.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nombre, Descripción y URL son obligatorios.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Consulta SQL para insertar en la tabla ejercicios
        String SQL = "INSERT INTO ejercicios (nombre, descripcion, link_imagen, link_video) VALUES (?, ?, ?, ?)";

        try (Connection conn = conexion.conectar();
             PreparedStatement ps = conn.prepareStatement(SQL)) {

            ps.setString(1, nombre);
            ps.setString(2, descripcion);
            ps.setString(3, urlGif);
            ps.setString(4, linkVideo);

            int filasAfectadas = ps.executeUpdate();

            if (filasAfectadas > 0) {
                JOptionPane.showMessageDialog(this, "Ejercicio '" + nombre + "' guardado con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                // Opcional: limpiar campos después de guardar
                limpiarCampos();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo guardar el ejercicio.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error de base de datos: " + ex.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    // ======================================
    // LÓGICA VISUAL (Cargar GIF)
    // ======================================

    private void cargarPreviewGif() {
        String urlString = txtUrlGif.getText().trim();
        if (urlString.isEmpty()) {
            lblPreviewGif.setIcon(null);
            return;
        }

        try {
            // Carga la imagen/GIF desde la URL
            URL url = new URL(urlString);
            Icon icon = new ImageIcon(url);

            lblPreviewGif.setIcon(icon);
            lblPreviewGif.setText(""); // Ocultar texto si se carga la imagen

        } catch (java.net.MalformedURLException ex) {
            lblPreviewGif.setIcon(null);
            lblPreviewGif.setText("URL no válida");
            JOptionPane.showMessageDialog(this, "La URL proporcionada no es válida.", "Error de URL", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            lblPreviewGif.setIcon(null);
            lblPreviewGif.setText("Error de carga");
            JOptionPane.showMessageDialog(this, "Error al cargar la imagen/GIF.", "Error de Carga", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarCampos() {
        txtNombre.setText("");
        txtDescripcion.setText("");
        txtUrlGif.setText("");
        lblPreviewGif.setIcon(null);
        lblPreviewGif.setText("Esperando URL...");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ejercicios().setVisible(true));
    }
}
*/
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.net.URL; // Para cargar la imagen/GIF desde URL
import java.awt.*;

// Mantener la extensión JFrame es opcional, pero proveemos el panel
public class ejercicios extends JFrame {

    // Componentes que deben ser enlazados desde el .form
    private JPanel panel1; // Panel principal que será devuelto
    private JTextField txtNombre;
    private JTextArea txtDescripcion;
    private JTextField txtUrlGif;
    private JTextField txtLinkVideo; // Nuevo: Para videos de YouTube/otros
    private JLabel lblPreviewGif;
    private JButton btnGuardar;
    private JButton btnCargarPreview;
    private JButton btnLimpiar; // Usaremos el botón Cancelar/Limpiar

    private JTable tableEjercicios; // Asumimos este nombre para la tabla
    private JTextField txtBuscar; // Campo de búsqueda
    private JButton btnModificar;
    private JButton btnEliminar;
    private JTable table1;

    private Connection con = null;

    // 🚨 CONSTRUCTOR ADAPTADO: Recibe el identificador (aunque no lo use)
    public ejercicios(String identificador) {

        // Establecer conexión
        con = conexion.conectar();

        // Cargar datos al iniciar, solo si la conexión fue exitosa
        if (con != null) {
            cargarTabla();
        }

        // --- ActionListeners ---
        btnGuardar.addActionListener(e -> guardarEjercicio());
        btnLimpiar.addActionListener(e -> limpiarCampos());
        btnCargarPreview.addActionListener(e -> cargarPreviewGif());

        btnModificar.addActionListener(e -> modificarEjercicio());
        btnEliminar.addActionListener(e -> eliminarEjercicio());
        txtBuscar.addActionListener(e -> buscarEjercicio());

        // Listener para seleccionar una fila en la tabla
        tableEjercicios.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cargarDatosEnCampos();
            }
        });
    }

    // ======================================
    // LÓGICA DE GESTIÓN (CRUD)
    // ======================================

    private void cargarTabla() {
        DefaultTableModel modelo = new DefaultTableModel();
        modelo.setColumnIdentifiers(new Object[]{
                "ID", "Nombre", "Descripción", "Link Imagen", "Link Video"
        });

        try {
            String sql = "SELECT id_ejercicio, nombre, descripcion, link_imagen, link_video FROM ejercicios";
            try (PreparedStatement ps = con.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    modelo.addRow(new Object[]{
                            rs.getInt("id_ejercicio"),
                            rs.getString("nombre"),
                            rs.getString("descripcion"),
                            rs.getString("link_imagen"),
                            rs.getString("link_video")
                    });
                }
                tableEjercicios.setModel(modelo);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(panel1, "Error al cargar ejercicios: " + e.getMessage());
        }
    }

    private void guardarEjercicio() {
        String nombre = txtNombre.getText().trim();
        String descripcion = txtDescripcion.getText().trim();
        String linkImagen = txtUrlGif.getText().trim();
        String linkVideo = txtLinkVideo.getText().trim();

        if (nombre.isEmpty() || descripcion.isEmpty() || linkImagen.isEmpty()) {
            JOptionPane.showMessageDialog(panel1, "Nombre, Descripción y Link de Imagen/GIF son obligatorios.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String SQL = "INSERT INTO ejercicios (nombre, descripcion, link_imagen, link_video) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(SQL)) {
            ps.setString(1, nombre);
            ps.setString(2, descripcion);
            ps.setString(3, linkImagen);
            ps.setString(4, linkVideo);

            int filasAfectadas = ps.executeUpdate();

            if (filasAfectadas > 0) {
                JOptionPane.showMessageDialog(panel1, "Ejercicio '" + nombre + "' guardado con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                cargarTabla();
                limpiarCampos();
            } else {
                JOptionPane.showMessageDialog(panel1, "No se pudo guardar el ejercicio.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(panel1, "Error de base de datos: " + ex.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void modificarEjercicio() {
        int fila = tableEjercicios.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(panel1, "Seleccione un ejercicio para modificar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Obtener el ID del ejercicio seleccionado
        int id = Integer.parseInt(tableEjercicios.getValueAt(fila, 0).toString());

        String nombre = txtNombre.getText().trim();
        String descripcion = txtDescripcion.getText().trim();
        String linkImagen = txtUrlGif.getText().trim();
        String linkVideo = txtLinkVideo.getText().trim();

        String SQL = "UPDATE ejercicios SET nombre=?, descripcion=?, link_imagen=?, link_video=? WHERE id_ejercicio=?";

        try (PreparedStatement ps = con.prepareStatement(SQL)) {
            ps.setString(1, nombre);
            ps.setString(2, descripcion);
            ps.setString(3, linkImagen);
            ps.setString(4, linkVideo);
            ps.setInt(5, id);

            int filasAfectadas = ps.executeUpdate();

            if (filasAfectadas > 0) {
                JOptionPane.showMessageDialog(panel1, "Ejercicio modificado con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                cargarTabla();
                limpiarCampos();
            } else {
                JOptionPane.showMessageDialog(panel1, "No se pudo modificar el ejercicio.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(panel1, "Error de base de datos: " + ex.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarEjercicio() {
        int fila = tableEjercicios.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(panel1, "Seleccione un ejercicio para eliminar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int respuesta = JOptionPane.showConfirmDialog(panel1, "¿Está seguro que desea eliminar este ejercicio?", "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);

        if (respuesta == JOptionPane.YES_OPTION) {
            int id = Integer.parseInt(tableEjercicios.getValueAt(fila, 0).toString());
            String SQL = "DELETE FROM ejercicios WHERE id_ejercicio=?";

            try (PreparedStatement ps = con.prepareStatement(SQL)) {
                ps.setInt(1, id);

                if (ps.executeUpdate() > 0) {
                    JOptionPane.showMessageDialog(panel1, "Ejercicio eliminado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    cargarTabla();
                    limpiarCampos();
                }

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(panel1, "Error al eliminar ejercicio: " + ex.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void buscarEjercicio() {
        String texto = txtBuscar.getText().trim();
        DefaultTableModel modelo = new DefaultTableModel();
        modelo.setColumnIdentifiers(new Object[]{"ID", "Nombre", "Descripción", "Link Imagen", "Link Video"});

        try {
            String sql = texto.isEmpty()
                    ? "SELECT id_ejercicio, nombre, descripcion, link_imagen, link_video FROM ejercicios"
                    : "SELECT id_ejercicio, nombre, descripcion, link_imagen, link_video FROM ejercicios WHERE nombre LIKE ?";

            try (PreparedStatement ps = con.prepareStatement(sql)) {

                if (!texto.isEmpty()) {
                    ps.setString(1, "%" + texto + "%");
                }

                try(ResultSet rs = ps.executeQuery()){
                    while (rs.next()) {
                        modelo.addRow(new Object[]{
                                rs.getInt("id_ejercicio"),
                                rs.getString("nombre"),
                                rs.getString("descripcion"),
                                rs.getString("link_imagen"),
                                rs.getString("link_video")
                        });
                    }
                }
            }

            tableEjercicios.setModel(modelo);

            if (modelo.getRowCount() == 0 && !texto.isEmpty()) {
                JOptionPane.showMessageDialog(panel1, "No se encontró ningún ejercicio con ese nombre.", "Búsqueda", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(panel1, "Error al buscar ejercicio: " + e.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        }
    }


    // ======================================
    // LÓGICA VISUAL Y UTILIDADES
    // ======================================

    private void cargarDatosEnCampos() {
        int fila = tableEjercicios.getSelectedRow();
        if (fila >= 0) {
            // Asumiendo que las columnas son 0:ID, 1:Nombre, 2:Desc, 3:LinkImg, 4:LinkVid
            txtNombre.setText(tableEjercicios.getValueAt(fila, 1).toString());
            txtDescripcion.setText(tableEjercicios.getValueAt(fila, 2).toString());
            txtUrlGif.setText(tableEjercicios.getValueAt(fila, 3).toString());

            // Verifica que la columna 4 no sea null antes de obtener el texto
            Object linkVideoObj = tableEjercicios.getValueAt(fila, 4);
            txtLinkVideo.setText(linkVideoObj != null ? linkVideoObj.toString() : "");

            // Cargar la previsualización del GIF inmediatamente
            cargarPreviewGif();
        }
    }

    private void cargarPreviewGif() {
        String urlString = txtUrlGif.getText().trim();
        lblPreviewGif.setText("Cargando...");
        lblPreviewGif.setIcon(null);

        if (urlString.isEmpty()) {
            lblPreviewGif.setText("Esperando URL...");
            return;
        }

        try {
            // Usamos SwingUtilities.invokeLater para no bloquear la interfaz
            SwingUtilities.invokeLater(() -> {
                try {
                    URL url = new URL(urlString);
                    Icon icon = new ImageIcon(url);

                    // Ajustar la imagen si es demasiado grande
                    if (icon.getIconWidth() > lblPreviewGif.getWidth() || icon.getIconHeight() > lblPreviewGif.getHeight()) {
                        Image img = ((ImageIcon) icon).getImage().getScaledInstance(
                                lblPreviewGif.getWidth(), lblPreviewGif.getHeight(), Image.SCALE_SMOOTH);
                        icon = new ImageIcon(img);
                    }

                    lblPreviewGif.setIcon(icon);
                    lblPreviewGif.setText("");
                } catch (java.net.MalformedURLException ex) {
                    lblPreviewGif.setText("URL no válida");
                    JOptionPane.showMessageDialog(panel1, "La URL proporcionada no es válida.", "Error de URL", JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    lblPreviewGif.setText("Error de carga");
                    // Opcional: mostrar error en consola si la carga falla
                }
            });

        } catch (Exception ex) {
            lblPreviewGif.setText("Error general");
        }
    }

    private void limpiarCampos() {
        txtNombre.setText("");
        txtDescripcion.setText("");
        txtUrlGif.setText("");
        txtLinkVideo.setText("");
        lblPreviewGif.setIcon(null);
        lblPreviewGif.setText("Esperando URL...");
        tableEjercicios.clearSelection();
    }

    //MÉTODO CRÍTICO para que admi.java pueda usar este panel
    public JPanel getPanel1() {
        return panel1;
    }
}