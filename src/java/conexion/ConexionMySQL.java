package conexion;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;

public class ConexionMySQL
{
    private static String servidor;
    private static String nombreBD;
    private static String usuario;
    private static String password;
    private static String puerto;
    
    
    public static Connection getConnection() throws SQLException
    {
        try
        {
            Class.forName("com.mysql.cj.jdbc.Driver");
        }
        catch (ClassNotFoundException e)
        {
            System.out.println("No se cargo el Driver");
        }
        
        servidor = "localhost";
        nombreBD = "dwi_27_ejemplo";
        usuario = "root";
        password = "Hydra2026!Dev";
        puerto = "3306";
        
        String urlConexion = "jdbc:mysql://" + servidor + ":" + puerto + "/" + nombreBD;
        return DriverManager.getConnection( urlConexion, usuario, password);
    }
    
}
