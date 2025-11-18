/*package gimnasio;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class ventas extends JFrame {

    // Componentes del Form (deben estar enlazados)
    private JPanel panel1;
    private JTextField txtDniCliente;
    // private JButton buscarClientesButton; // Componente no usado, se elimina
    // private JComboBox comboBox1; // Componente no usado, se elimina
    private JButton btnBuscarCliente;

    // Si en el form es un JTextField, cámbialo a JTextField aquí.
    private JLabel lblNombreCliente;

    private JComboBox<String> cmbProductos;
    private JTextField txtCantidad;
    private JButton button1;
    private JButton btnAgregarProducto;
    private JTable tableDetalle;
    private JLabel lblTotal;
    private JButton btnConfirmarVenta;

    // Variables de estado
    private Connection con = null;
    private int idClienteSeleccionado = -1;
    private double totalVenta = 0.0;

    // Almacenamiento temporal para el carrito: ID Producto -> [Nombre, Precio, Cantidad]
    private Map<Integer, Object[]> carrito = new HashMap<>();

    public ventas() {

        // Asumiendo que 'conexion.conectar()' maneja la SQLException internamente o usa un try-catch
        con = conexion.conectar();

        // 1. Configurar la tabla de detalle (carrito)
        configurarTablaDetalle();

        if (con != null) {
            cargarProductosEnCombo();
        }

        // 2. Asignación de ActionListeners
        btnBuscarCliente.addActionListener(e -> buscarCliente());
        btnAgregarProducto.addActionListener(e -> agregarProductoAlCarrito());
        btnConfirmarVenta.addActionListener(e -> confirmarVenta());

        //Configuración inicial de lblNombreCliente para evitar NullPointerException si se usa antes
        if (lblNombreCliente != null) {
            lblNombreCliente.setText("---");
        }
    }

    // ----------------------------------------------------------------------
    // --- LÓGICA DE INICIALIZACIÓN ---
    // ----------------------------------------------------------------------

    private void configurarTablaDetalle() {
        DefaultTableModel modelo = new DefaultTableModel();
        modelo.setColumnIdentifiers(new Object[]{"ID", "Producto", "Cantidad", "Precio Unitario", "Subtotal"});

        //Manejo de posible NullPointerException si tableDetalle no se inicializa en el form
        if (tableDetalle != null) {
            tableDetalle.setModel(modelo);
        } else {
            // Si no estás usando un form, deberías inicializar la tabla aquí:
            // tableDetalle = new JTable(modelo);
        }
    }

    private void cargarProductosEnCombo() {
        if (con == null || cmbProductos == null) return;
        String sql = "SELECT id_producto, nombre, precio, stock FROM productos WHERE stock > 0";
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            cmbProductos.removeAllItems();
            cmbProductos.addItem("Seleccionar Producto...");
            while (rs.next()) {
                // Formato: ID - Nombre (Precio: X.XX, Stock: Y)
                String item = rs.getInt("id_producto") + " - " +
                        rs.getString("nombre") +
                        " (Precio: " + String.format("%.2f", rs.getDouble("precio")) +
                        ", Stock: " + rs.getInt("stock") + ")";
                cmbProductos.addItem(item);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(panel1, "Error al cargar productos: " + e.getMessage());
        }
    }

    // ----------------------------------------------------------------------
    // --- LÓGICA DE CLIENTES ---
    // ----------------------------------------------------------------------

    private void buscarCliente() {
        String dni = txtDniCliente.getText().trim();
        if (dni.isEmpty()) {
            JOptionPane.showMessageDialog(panel1, "Ingrese el DNI del cliente.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String sql = "SELECT id_cliente, nombre, apellido FROM usuario_clientes WHERE dni = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, dni);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    idClienteSeleccionado = rs.getInt("id_cliente");
                    //Uso de lblNombreCliente
                    if (lblNombreCliente != null) {
                        lblNombreCliente.setText(rs.getString("nombre") + " " + rs.getString("apellido"));
                    }
                    JOptionPane.showMessageDialog(panel1, "Cliente encontrado y seleccionado.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    //Uso de lblNombreCliente
                    if (lblNombreCliente != null) {
                        lblNombreCliente.setText("Cliente no encontrado.");
                    }
                    idClienteSeleccionado = -1;
                    JOptionPane.showMessageDialog(panel1, "Cliente no encontrado.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(panel1, "Error al buscar cliente: " + e.getMessage());
        }
    }

    // ----------------------------------------------------------------------
    // --- LÓGICA DEL CARRITO (DETALLE) ---
    // ----------------------------------------------------------------------

    private void agregarProductoAlCarrito() {
        if (cmbProductos == null || cmbProductos.getSelectedIndex() <= 0) {
            JOptionPane.showMessageDialog(panel1, "Seleccione un producto válido.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // ... (El resto de la lógica para agregar producto es correcta) ...
        String itemSeleccionado = (String) cmbProductos.getSelectedItem();
        int idProducto;
        double precio;
        int stockDisponible;
        String nombreProducto;

        try {
            idProducto = Integer.parseInt(itemSeleccionado.split(" - ")[0]);
            nombreProducto = itemSeleccionado.substring(itemSeleccionado.indexOf("-") + 2, itemSeleccionado.indexOf(" ("));

            String subCadenaPrecio = itemSeleccionado.substring(itemSeleccionado.indexOf("Precio: ") + 8, itemSeleccionado.indexOf(", Stock: "));
            precio = Double.parseDouble(subCadenaPrecio);

            String subCadenaStock = itemSeleccionado.substring(itemSeleccionado.indexOf("Stock: ") + 7, itemSeleccionado.length() - 1);
            stockDisponible = Integer.parseInt(subCadenaStock);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(panel1, "Error al parsear datos del producto. Recargue la lista.", "Error Interno", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int cantidad;
        try {
            cantidad = Integer.parseInt(txtCantidad.getText().trim());
            if (cantidad <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(panel1, "Ingrese una cantidad válida (> 0).", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (cantidad > stockDisponible) {
            JOptionPane.showMessageDialog(panel1, "Stock insuficiente. Solo quedan " + stockDisponible + " unidades.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double subtotal = precio * cantidad;

        // Agregar o actualizar en el carrito (HashMap)
        if (carrito.containsKey(idProducto)) {
            // Producto ya en el carrito: aumentar cantidad y subtotal
            int cantidadActual = (int) carrito.get(idProducto)[2];
            double subtotalActual = (double) carrito.get(idProducto)[3];

            int nuevaCantidad = cantidadActual + cantidad;
            double nuevoSubtotal = subtotalActual + subtotal;

            carrito.put(idProducto, new Object[]{nombreProducto, precio, nuevaCantidad, nuevoSubtotal});
        } else {
            // Nuevo producto
            carrito.put(idProducto, new Object[]{nombreProducto, precio, cantidad, subtotal});
        }

        actualizarTablaYTotal();
        txtCantidad.setText("1");
    }

    private void actualizarTablaYTotal() {
        DefaultTableModel modelo = (DefaultTableModel) tableDetalle.getModel();
        modelo.setRowCount(0); // Limpiar tabla
        totalVenta = 0.0;

        for (Map.Entry<Integer, Object[]> entry : carrito.entrySet()) {
            int idProd = entry.getKey();
            Object[] data = entry.getValue();

            String nombre = (String) data[0];
            double precio = (double) data[1];
            int cantidad = (int) data[2];
            double subtotal = (double) data[3];

            totalVenta += subtotal;

            modelo.addRow(new Object[]{
                    idProd, nombre, cantidad, String.format("%.2f", precio), String.format("%.2f", subtotal)
            });
        }

        //Uso de lblTotal
        if (lblTotal != null) {
            lblTotal.setText("TOTAL: " + String.format("%.2f", totalVenta) + " ARS");
        }
    }

    // ----------------------------------------------------------------------
    // --- LÓGICA DE TRANSACCIÓN (CONFIRMAR VENTA) ---
    // ----------------------------------------------------------------------
    private void confirmarVenta() {
        if (idClienteSeleccionado == -1) {
            JOptionPane.showMessageDialog(panel1, "Debe seleccionar un cliente antes de confirmar la venta.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (carrito.isEmpty()) {
            JOptionPane.showMessageDialog(panel1, "El carrito de ventas está vacío.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (JOptionPane.showConfirmDialog(panel1, "¿Confirmar la venta por un total de " + String.format("%.2f", totalVenta) + " ARS?", "Confirmar Venta", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
            return;
        }

        // Inicio de la transacción
        try {
            con.setAutoCommit(false); // Desactivar auto-commit

            // 1. Insertar en la tabla 'ventas'
            String sqlVenta = "INSERT INTO ventas (id_cliente, fecha, total) VALUES (?, ?, ?)";
            int idVentaGenerado = -1;

            try (PreparedStatement psVenta = con.prepareStatement(sqlVenta, PreparedStatement.RETURN_GENERATED_KEYS)) {
                psVenta.setInt(1, idClienteSeleccionado);
                psVenta.setObject(2, LocalDateTime.now());
                psVenta.setDouble(3, totalVenta);
                psVenta.executeUpdate();

                try (ResultSet rs = psVenta.getGeneratedKeys()) {
                    if (rs.next()) {
                        idVentaGenerado = rs.getInt(1);
                    }
                }
            }

            if (idVentaGenerado == -1) {
                throw new SQLException("Error al obtener el ID de la venta generada.");
            }

            // 2. Insertar en 'detalle_ventas' y actualizar el stock
            String sqlDetalle = "INSERT INTO detalle_ventas (id_venta, id_producto, cantidad, precio_unitario, subtotal) VALUES (?, ?, ?, ?, ?)";
            String sqlStock = "UPDATE productos SET stock = stock - ? WHERE id_producto = ?";

            for (Map.Entry<Integer, Object[]> entry : carrito.entrySet()) {
                int idProducto = entry.getKey();
                Object[] data = entry.getValue();

                double precioUnitario = (double) data[1];
                int cantidad = (int) data[2];
                double subtotal = (double) data[3];

                // 2a. Insertar detalle
                try (PreparedStatement psDetalle = con.prepareStatement(sqlDetalle)) {
                    psDetalle.setInt(1, idVentaGenerado);
                    psDetalle.setInt(2, idProducto);
                    psDetalle.setInt(3, cantidad);
                    psDetalle.setDouble(4, precioUnitario);
                    psDetalle.setDouble(5, subtotal);
                    psDetalle.executeUpdate();
                }

                // 2b. Actualizar stock
                try (PreparedStatement psStock = con.prepareStatement(sqlStock)) {
                    psStock.setInt(1, cantidad);
                    psStock.setInt(2, idProducto);
                    psStock.executeUpdate();
                }
            }

            // 3. Commit de la transacción
            con.commit();
            JOptionPane.showMessageDialog(panel1, "Venta Nº " + idVentaGenerado + " registrada con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);

            // 4. Limpiar y recargar
            limpiarTransaccion();
            cargarProductosEnCombo(); // Recargar el combo para reflejar el nuevo stock

        } catch (SQLException e) {
            try {
                con.rollback(); // Rollback en caso de error
            } catch (SQLException rollbackEx) {
                // Manejar error de rollback
            }
            JOptionPane.showMessageDialog(panel1, "Error en la transacción de venta: " + e.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (con != null) con.setAutoCommit(true); // Restaurar auto-commit
            } catch (SQLException e) {
                // Manejar error
            }
        }
    }

    private void limpiarTransaccion() {
        txtDniCliente.setText("");
        if (lblNombreCliente != null) {
            lblNombreCliente.setText("---");
        }
        idClienteSeleccionado = -1;
        carrito.clear();
        actualizarTablaYTotal();
        txtCantidad.setText("");
        if (cmbProductos != null && cmbProductos.getItemCount() > 0) {
            cmbProductos.setSelectedIndex(0);
        }
    }

    // ----------------------------------------------------------------------
    // --- MÉTODOS DE INTEGRACIÓN ---
    // ----------------------------------------------------------------------
    public JPanel getPanel1() {
        //Si usas un diseñador de formularios (como IntelliJ o NetBeans),
        // este método está bien. Si no, DEBERÍAS CONSTRUIR panel1 manualmente
        // en el constructor o en createUIComponents.
        return panel1;
    }

    public void setVisible(boolean b) {
        // Este método se deja vacío si se usa getPanel1()
    }

    // Método requerido por algunos diseñadores de formularios para la inicialización
    private void createUIComponents() {
        // Aquí se crearían componentes personalizados si fueran necesarios
    }
}*/

package gimnasio;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class ventas extends JFrame {

    private JPanel panel1;
    private JTextField txtDniCliente;
    private JButton btnBuscarCliente;
    private JLabel lblNombreCliente;
    private JComboBox<String> cmbProductos;
    private JTextField txtCantidad;
    private JButton btnAgregarProducto;
    private JTable tableDetalle;
    private JLabel lblTotal;
    private JButton btnConfirmarVenta;
    private JTextField textField1;

    private Connection con = null;
    private int idClienteSeleccionado = -1;
    private double totalVenta = 0.0;
    private Map<Integer, Object[]> carrito = new HashMap<>();

    public ventas() {

        // Debe asegurarse de que todos los componentes (especialmente btnBuscarCliente)
        // son inicializados en createUIComponents() o por el constructor del JFrame

        if (btnBuscarCliente == null) {
            btnBuscarCliente = new JButton(); // Inicialización forzada si el form falla
        }

        con = conexion.conectar();

        configurarTablaDetalle();

        if (con != null) {
            cargarProductosEnCombo();
        }

        btnBuscarCliente.addActionListener(e -> buscarCliente());
        btnAgregarProducto.addActionListener(e -> agregarProductoAlCarrito());
        btnConfirmarVenta.addActionListener(e -> confirmarVenta());
    }

    private void configurarTablaDetalle() {
        DefaultTableModel modelo = new DefaultTableModel();
        modelo.setColumnIdentifiers(new Object[]{"ID", "Producto", "Cantidad", "Precio Unitario", "Subtotal"});

        if (tableDetalle != null) {
            tableDetalle.setModel(modelo);
        } else {
            tableDetalle = new JTable(modelo);
        }
    }

    private void cargarProductosEnCombo() {
        if (con == null || cmbProductos == null) return;
        String sql = "SELECT id_producto, nombre, precio, stock FROM productos WHERE stock > 0";
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            cmbProductos.removeAllItems();
            cmbProductos.addItem("Seleccionar Producto...");
            while (rs.next()) {
                String item = rs.getInt("id_producto") + " - " +
                        rs.getString("nombre") +
                        " (Precio: " + String.format("%.2f", rs.getDouble("precio")) +
                        ", Stock: " + rs.getInt("stock") + ")";
                cmbProductos.addItem(item);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(panel1, "Error al cargar productos: " + e.getMessage());
        }
    }

    private void buscarCliente() {
        String dni = txtDniCliente.getText().trim();
        if (dni.isEmpty()) {
            JOptionPane.showMessageDialog(panel1, "Ingrese el DNI del cliente.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String sql = "SELECT id_cliente, nombre, apellido FROM usuario_clientes WHERE dni = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, dni);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    idClienteSeleccionado = rs.getInt("id_cliente");
                    lblNombreCliente.setText(rs.getString("nombre") + " " + rs.getString("apellido"));
                    JOptionPane.showMessageDialog(panel1, "Cliente encontrado y seleccionado.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    lblNombreCliente.setText("Cliente no encontrado.");
                    idClienteSeleccionado = -1;
                    JOptionPane.showMessageDialog(panel1, "Cliente no encontrado.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(panel1, "Error al buscar cliente: " + e.getMessage());
        }
    }

    private void agregarProductoAlCarrito() {
        if (cmbProductos == null || cmbProductos.getSelectedIndex() <= 0) {
            JOptionPane.showMessageDialog(panel1, "Seleccione un producto válido.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String itemSeleccionado = (String) cmbProductos.getSelectedItem();
        int idProducto;
        double precio;
        int stockDisponible;
        String nombreProducto;

        try {
            idProducto = Integer.parseInt(itemSeleccionado.split(" - ")[0]);
            nombreProducto = itemSeleccionado.substring(itemSeleccionado.indexOf("-") + 2, itemSeleccionado.indexOf(" ("));

            String subCadenaPrecio = itemSeleccionado.substring(itemSeleccionado.indexOf("Precio: ") + 8, itemSeleccionado.indexOf(", Stock: "));
            precio = Double.parseDouble(subCadenaPrecio);

            String subCadenaStock = itemSeleccionado.substring(itemSeleccionado.indexOf("Stock: ") + 7, itemSeleccionado.length() - 1);
            stockDisponible = Integer.parseInt(subCadenaStock);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(panel1, "Error al parsear datos del producto. Recargue la lista.", "Error Interno", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int cantidad;
        try {
            cantidad = Integer.parseInt(txtCantidad.getText().trim());
            if (cantidad <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(panel1, "Ingrese una cantidad válida (> 0).", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (cantidad > stockDisponible) {
            JOptionPane.showMessageDialog(panel1, "Stock insuficiente. Solo quedan " + stockDisponible + " unidades.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double subtotal = precio * cantidad;

        if (carrito.containsKey(idProducto)) {
            int cantidadActual = (int) carrito.get(idProducto)[2];
            double subtotalActual = (double) carrito.get(idProducto)[3];

            int nuevaCantidad = cantidadActual + cantidad;
            double nuevoSubtotal = subtotalActual + subtotal;

            carrito.put(idProducto, new Object[]{nombreProducto, precio, nuevaCantidad, nuevoSubtotal});
        } else {
            carrito.put(idProducto, new Object[]{nombreProducto, precio, cantidad, subtotal});
        }

        actualizarTablaYTotal();
        txtCantidad.setText("1");
    }

    private void actualizarTablaYTotal() {
        DefaultTableModel modelo = (DefaultTableModel) tableDetalle.getModel();
        modelo.setRowCount(0);
        totalVenta = 0.0;

        for (Map.Entry<Integer, Object[]> entry : carrito.entrySet()) {
            int idProd = entry.getKey();
            Object[] data = entry.getValue();

            String nombre = (String) data[0];
            double precio = (double) data[1];
            int cantidad = (int) data[2];
            double subtotal = (double) data[3];

            totalVenta += subtotal;

            modelo.addRow(new Object[]{
                    idProd, nombre, cantidad, String.format("%.2f", precio), String.format("%.2f", subtotal)
            });
        }

        lblTotal.setText("TOTAL: " + String.format("%.2f", totalVenta) + " ARS");
    }

    private void confirmarVenta() {
        if (idClienteSeleccionado == -1) {
            JOptionPane.showMessageDialog(panel1, "Debe seleccionar un cliente antes de confirmar la venta.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (carrito.isEmpty()) {
            JOptionPane.showMessageDialog(panel1, "El carrito de ventas está vacío.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (JOptionPane.showConfirmDialog(panel1, "¿Confirmar la venta por un total de " + String.format("%.2f", totalVenta) + " ARS?", "Confirmar Venta", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            con.setAutoCommit(false);

            String sqlVenta = "INSERT INTO ventas (id_cliente, fecha, total) VALUES (?, ?, ?)";
            int idVentaGenerado = -1;

            try (PreparedStatement psVenta = con.prepareStatement(sqlVenta, PreparedStatement.RETURN_GENERATED_KEYS)) {
                psVenta.setInt(1, idClienteSeleccionado);
                psVenta.setObject(2, LocalDateTime.now());
                psVenta.setDouble(3, totalVenta);
                psVenta.executeUpdate();

                try (ResultSet rs = psVenta.getGeneratedKeys()) {
                    if (rs.next()) {
                        idVentaGenerado = rs.getInt(1);
                    }
                }
            }

            if (idVentaGenerado == -1) {
                throw new SQLException("Error al obtener el ID de la venta generada.");
            }

            String sqlDetalle = "INSERT INTO detalle_ventas (id_venta, id_producto, cantidad, precio_unitario, subtotal) VALUES (?, ?, ?, ?, ?)";
            String sqlStock = "UPDATE productos SET stock = stock - ? WHERE id_producto = ?";

            for (Map.Entry<Integer, Object[]> entry : carrito.entrySet()) {
                int idProducto = entry.getKey();
                Object[] data = entry.getValue();

                double precioUnitario = (double) data[1];
                int cantidad = (int) data[2];
                double subtotal = (double) data[3];

                try (PreparedStatement psDetalle = con.prepareStatement(sqlDetalle)) {
                    psDetalle.setInt(1, idVentaGenerado);
                    psDetalle.setInt(2, idProducto);
                    psDetalle.setInt(3, cantidad);
                    psDetalle.setDouble(4, precioUnitario);
                    psDetalle.setDouble(5, subtotal);
                    psDetalle.executeUpdate();
                }

                try (PreparedStatement psStock = con.prepareStatement(sqlStock)) {
                    psStock.setInt(1, cantidad);
                    psStock.setInt(2, idProducto);
                    psStock.executeUpdate();
                }
            }

            con.commit();
            JOptionPane.showMessageDialog(panel1, "Venta Nº " + idVentaGenerado + " registrada con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);

            limpiarTransaccion();
            cargarProductosEnCombo();

        } catch (SQLException e) {
            try {
                con.rollback();
            } catch (SQLException rollbackEx) {
            }
            JOptionPane.showMessageDialog(panel1, "Error en la transacción de venta: " + e.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (con != null) con.setAutoCommit(true);
            } catch (SQLException e) {
            }
        }
    }

    private void limpiarTransaccion() {
        txtDniCliente.setText("");
        lblNombreCliente.setText("---");
        idClienteSeleccionado = -1;
        carrito.clear();
        actualizarTablaYTotal();
        txtCantidad.setText("");
        if (cmbProductos != null && cmbProductos.getItemCount() > 0) {
            cmbProductos.setSelectedIndex(0);
        }
    }

    public JPanel getPanel1() {
        return panel1;
    }

    private void createUIComponents() {
        // Método que debe ser llamado por el diseñador de formularios para inicializar los componentes del panel1
    }
}