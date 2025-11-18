/*package gimnasio;
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


public class ejercicios extends JFrame {

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


    public ejercicios() {

        con = conexion.conectar();

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

    //(CRUD)
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

    // LÓGICA VISUAL Y UTILIDADES
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

    public JPanel getPanel1() {
        return panel1;
    }
}*/
package gimnasio;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.net.URL;
import java.net.MalformedURLException;

public class ejercicios extends JPanel {

    private JPanel panel1;
    private JTextField txtNombre;
    private JTextArea txtDescripcion;
    private JTextField txtUrlGif;
    private JTextField txtLinkVideo;
    private JLabel lblPreviewGif;
    private JButton btnGuardar;
    private JButton btnCargarPreview;
    private JButton btnLimpiar;
    private JTable tableEjercicios;
    private JTextField txtBuscar;
    private JButton btnModificar;
    private JButton btnEliminar;
    private JButton btnBuscar;
    private JTable table1;
    private Connection con = null;

    public ejercicios() {
        setLayout(new BorderLayout());

        initComponents();

        con = conexion.conectar();

        if (con != null) {
            cargarTabla();
        }

        btnGuardar.addActionListener(e -> guardarEjercicio());
        btnLimpiar.addActionListener(e -> limpiarCampos());
        btnCargarPreview.addActionListener(e -> cargarPreviewGif());

        btnModificar.addActionListener(e -> modificarEjercicio());
        btnEliminar.addActionListener(e -> eliminarEjercicio());
        txtBuscar.addActionListener(e -> buscarEjercicio());
        btnBuscar.addActionListener(e -> buscarEjercicio());

        tableEjercicios.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cargarDatosEnCampos();
            }
        });
    }

    private void initComponents() {
        txtNombre = new JTextField(20);
        txtDescripcion = new JTextArea(4, 20);
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        txtUrlGif = new JTextField(20);
        txtLinkVideo = new JTextField(20);
        lblPreviewGif = new JLabel("Esperando URL...", SwingConstants.CENTER);
        lblPreviewGif.setPreferredSize(new Dimension(150, 150));
        lblPreviewGif.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        btnGuardar = new JButton("Guardar");
        btnCargarPreview = new JButton("Cargar Preview");
        btnLimpiar = new JButton("Limpiar Campos");
        btnModificar = new JButton("Modificar");
        btnEliminar = new JButton("Eliminar");
        btnBuscar = new JButton("Buscar");
        txtBuscar = new JTextField(15);
        tableEjercicios = new JTable();

        JPanel panelDatos = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0; panelDatos.add(new JLabel("Nombre:"), gbc);
        gbc.gridy = 1; panelDatos.add(new JLabel("URL GIF/Imagen:"), gbc);
        gbc.gridy = 2; panelDatos.add(new JLabel("Link Video (Opcional):"), gbc);
        gbc.gridy = 3; panelDatos.add(new JLabel("Descripción:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.gridy = 0; panelDatos.add(txtNombre, gbc);
        gbc.gridy = 1; panelDatos.add(txtUrlGif, gbc);
        gbc.gridy = 2; panelDatos.add(txtLinkVideo, gbc);
        gbc.gridy = 3; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.BOTH;
        panelDatos.add(new JScrollPane(txtDescripcion), gbc);

        JPanel panelPreview = new JPanel(new BorderLayout());
        panelPreview.add(lblPreviewGif, BorderLayout.CENTER);
        panelPreview.add(btnCargarPreview, BorderLayout.SOUTH);

        JPanel panelNorte = new JPanel(new BorderLayout(10, 10));
        panelNorte.add(panelDatos, BorderLayout.CENTER);
        panelNorte.add(panelPreview, BorderLayout.EAST);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panelBotones.add(btnGuardar);
        panelBotones.add(btnModificar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnLimpiar);
        panelBotones.add(new JSeparator(SwingConstants.VERTICAL));
        panelBotones.add(txtBuscar);
        panelBotones.add(btnBuscar);

        JPanel panelControles = new JPanel(new BorderLayout());
        panelControles.add(panelNorte, BorderLayout.NORTH);
        panelControles.add(panelBotones, BorderLayout.CENTER);

        this.add(panelControles, BorderLayout.NORTH);
        this.add(new JScrollPane(tableEjercicios), BorderLayout.CENTER);
    }

    private void cargarTabla() {
        DefaultTableModel modelo = new DefaultTableModel();
        modelo.setColumnIdentifiers(new Object[]{
                "ID", "Nombre", "Descripción", "Link Imagen", "Link Video"
        });

        try {
            String sql = "SELECT id_ejercicio, nombre, descripcion, link_imagen, link_video FROM ejercicios";
            if (con == null) {
                JOptionPane.showMessageDialog(this, "Conexión a DB es nula.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
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
            JOptionPane.showMessageDialog(this, "Error al cargar ejercicios: " + e.getMessage());
        }
    }

    private void guardarEjercicio() {
        String nombre = txtNombre.getText().trim();
        String descripcion = txtDescripcion.getText().trim();
        String linkImagen = txtUrlGif.getText().trim();
        String linkVideo = txtLinkVideo.getText().trim();

        if (nombre.isEmpty() || descripcion.isEmpty() || linkImagen.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nombre, Descripción y Link de Imagen/GIF son obligatorios.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (con == null) return;
        String SQL = "INSERT INTO ejercicios (nombre, descripcion, link_imagen, link_video) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(SQL)) {
            ps.setString(1, nombre);
            ps.setString(2, descripcion);
            ps.setString(3, linkImagen);
            ps.setString(4, linkVideo);

            int filasAfectadas = ps.executeUpdate();

            if (filasAfectadas > 0) {
                JOptionPane.showMessageDialog(this, "Ejercicio '" + nombre + "' guardado con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                cargarTabla();
                limpiarCampos();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo guardar el ejercicio.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error de base de datos: " + ex.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void modificarEjercicio() {
        int fila = tableEjercicios.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un ejercicio para modificar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (con == null) return;
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
                JOptionPane.showMessageDialog(this, "Ejercicio modificado con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                cargarTabla();
                limpiarCampos();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo modificar el ejercicio.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error de base de datos: " + ex.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarEjercicio() {
        int fila = tableEjercicios.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un ejercicio para eliminar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (con == null) return;
        int respuesta = JOptionPane.showConfirmDialog(this, "¿Está seguro que desea eliminar este ejercicio?", "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);

        if (respuesta == JOptionPane.YES_OPTION) {
            int id = Integer.parseInt(tableEjercicios.getValueAt(fila, 0).toString());
            String SQL = "DELETE FROM ejercicios WHERE id_ejercicio=?";

            try (PreparedStatement ps = con.prepareStatement(SQL)) {
                ps.setInt(1, id);

                if (ps.executeUpdate() > 0) {
                    JOptionPane.showMessageDialog(this, "Ejercicio eliminado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    cargarTabla();
                    limpiarCampos();
                }

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error al eliminar ejercicio: " + ex.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void buscarEjercicio() {
        String texto = txtBuscar.getText().trim();
        DefaultTableModel modelo = new DefaultTableModel();
        modelo.setColumnIdentifiers(new Object[]{"ID", "Nombre", "Descripción", "Link Imagen", "Link Video"});

        if (con == null) return;
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
                JOptionPane.showMessageDialog(this, "No se encontró ningún ejercicio con ese nombre.", "Búsqueda", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al buscar ejercicio: " + e.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarDatosEnCampos() {
        int fila = tableEjercicios.getSelectedRow();
        if (fila >= 0) {
            txtNombre.setText(tableEjercicios.getValueAt(fila, 1).toString());
            txtDescripcion.setText(tableEjercicios.getValueAt(fila, 2).toString());
            txtUrlGif.setText(tableEjercicios.getValueAt(fila, 3).toString());

            Object linkVideoObj = tableEjercicios.getValueAt(fila, 4);
            txtLinkVideo.setText(linkVideoObj != null ? linkVideoObj.toString() : "");

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

        new Thread(() -> {
            try {
                URL url = new URL(urlString);
                ImageIcon icon = new ImageIcon(url);

                if (icon.getIconWidth() == -1) {
                    SwingUtilities.invokeLater(() -> lblPreviewGif.setText("Error: URL no válida o no es un link directo a imagen."));
                    return;
                }

                SwingUtilities.invokeLater(() -> {
                    Icon finalIcon = icon;

                    int labelWidth = lblPreviewGif.getWidth() > 0 ? lblPreviewGif.getWidth() : 150;
                    int labelHeight = lblPreviewGif.getHeight() > 0 ? lblPreviewGif.getHeight() : 150;

                    Image originalImage = icon.getImage();
                    int originalWidth = originalImage.getWidth(null);
                    int originalHeight = originalImage.getHeight(null);

                    if (originalWidth > 0 && originalHeight > 0) {
                        double ratioX = (double) labelWidth / originalWidth;
                        double ratioY = (double) labelHeight / originalHeight;
                        double ratio = Math.min(ratioX, ratioY);

                        int newWidth = (int) (originalWidth * ratio);
                        int newHeight = (int) (originalHeight * ratio);

                        if (newWidth > 0 && newHeight > 0) {
                            Image img = originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
                            finalIcon = new ImageIcon(img);
                        }
                    }

                    lblPreviewGif.setIcon(finalIcon);
                    lblPreviewGif.setText("");
                });

            } catch (MalformedURLException ex) {
                SwingUtilities.invokeLater(() -> {
                    lblPreviewGif.setText("URL no válida");
                });
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> {
                    lblPreviewGif.setText("Error de carga");
                });
            }
        }).start();
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

    public JPanel getPanel1() {
        return this;
    }
}