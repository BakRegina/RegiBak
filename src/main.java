
import java.sql.Connection;
import java.sql.SQLException;

public class main {
    public static void main(String[] args) {
        //verificamos que este bien conectada la BD
        Connection c = conexion.conexion;
       /* try {
            if (c != null) {
                System.out.println("Test: conexión OK");
            } else {
                System.out.println("Test: conexión NULL o cerrada");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            conexion.cerrar();
        }*/
            Connection test = conexion.conectar();
            if (test != null) {
                System.out.println("Base de datos abierta correctamente");
            } else {
                System.out.println("Conexión fallida");
            }
        }
    }
