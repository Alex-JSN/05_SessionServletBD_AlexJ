package dao;

import conexion.ConexionMySQL;
import modelo.Usuario;
import util.PasswordUtil;
import util.TokenUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class DAOUsuario
{
    public boolean existeCorreo(String correo)
    {
        String sql = "SELECT id_usuario FROM Usuarios WHERE correo = ?";
        try (Connection con = ConexionMySQL.getConnection();
             PreparedStatement ps = con.prepareStatement(sql))
        {
            ps.setString(1, correo);
            try (ResultSet rs = ps.executeQuery())
            {
                return rs.next();
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Error al validar correo", e);
        }
    }

    public boolean existeMatricula(String matricula)
    {
        String sql = "SELECT id_usuario FROM Usuarios WHERE matricula = ?";
        try (Connection con = ConexionMySQL.getConnection();
             PreparedStatement ps = con.prepareStatement(sql))
        {
            ps.setString(1, matricula);
            try (ResultSet rs = ps.executeQuery())
            {
                return rs.next();
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Error al validar matrícula", e);
        }
    }

    /** Registra un usuario nuevo con estado 'inactivo' y un código de verificación de 6 dígitos. Regresa el código generado. */
    public String registrar(Usuario u)
    {
        String codigo = TokenUtil.generarCodigo();
        Timestamp expiracion = Timestamp.valueOf(LocalDateTime.now().plusMinutes(TokenUtil.MINUTOS_EXPIRACION));

        String sql = "INSERT INTO Usuarios "
                + "(matricula, nombre, apellido_p, apellido_m, correo, contrasena, tipo_usuario, estado, token_verificacion, token_expiracion) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, 'inactivo', ?, ?)";

        try (Connection con = ConexionMySQL.getConnection();
             PreparedStatement ps = con.prepareStatement(sql))
        {
            ps.setString(1, u.getMatricula());
            ps.setString(2, u.getNombre());
            ps.setString(3, u.getApellidoP());
            ps.setString(4, u.getApellidoM());
            ps.setString(5, u.getCorreo());
            ps.setString(6, PasswordUtil.hashear(u.getContrasena()));
            ps.setString(7, u.getTipoUsuario());
            ps.setString(8, codigo);
            ps.setTimestamp(9, expiracion);

            ps.executeUpdate();
            return codigo;
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Error al registrar usuario", e);
        }
    }

    /**
     * Genera un nuevo código de verificación para un correo ya registrado pero inactivo,
     * y lo regresa para que se pueda reenviar por correo. Regresa null si el correo no existe
     * o si ya está activo (no aplica reenvío).
     */
    public String regenerarCodigo(String correo)
    {
        Usuario u = obtenerPorCorreo(correo);
        if (u == null || !"inactivo".equals(u.getEstado()))
        {
            return null;
        }

        String codigo = TokenUtil.generarCodigo();
        Timestamp expiracion = Timestamp.valueOf(LocalDateTime.now().plusMinutes(TokenUtil.MINUTOS_EXPIRACION));

        String sql = "UPDATE Usuarios SET token_verificacion = ?, token_expiracion = ? WHERE correo = ?";
        try (Connection con = ConexionMySQL.getConnection();
             PreparedStatement ps = con.prepareStatement(sql))
        {
            ps.setString(1, codigo);
            ps.setTimestamp(2, expiracion);
            ps.setString(3, correo);
            ps.executeUpdate();
            return codigo;
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Error al regenerar código", e);
        }
    }

    /** Busca un usuario por correo, junto con su hash de contraseña y estado, para el login. */
    public Usuario obtenerPorCorreo(String correo)
    {
        String sql = "SELECT * FROM Usuarios WHERE correo = ?";
        try (Connection con = ConexionMySQL.getConnection();
             PreparedStatement ps = con.prepareStatement(sql))
        {
            ps.setString(1, correo);
            try (ResultSet rs = ps.executeQuery())
            {
                if (rs.next())
                {
                    return mapear(rs);
                }
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Error al buscar usuario por correo", e);
        }
        return null;
    }

    /**
     * Activa la cuenta si el código coincide con el correo dado y no ha expirado.
     * Regresa: "ok" | "expirado" | "invalido"
     */
    public String activarCuenta(String correo, String codigo)
    {
        String sqlBuscar = "SELECT token_verificacion, token_expiracion FROM Usuarios WHERE correo = ?";
        try (Connection con = ConexionMySQL.getConnection();
             PreparedStatement ps = con.prepareStatement(sqlBuscar))
        {
            ps.setString(1, correo);
            try (ResultSet rs = ps.executeQuery())
            {
                if (!rs.next())
                {
                    return "invalido";
                }
                String codigoGuardado = rs.getString("token_verificacion");
                if (codigoGuardado == null || !codigoGuardado.equals(codigo))
                {
                    return "invalido";
                }
                Timestamp expiracion = rs.getTimestamp("token_expiracion");
                if (expiracion != null && expiracion.before(new Timestamp(System.currentTimeMillis())))
                {
                    return "expirado";
                }
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Error al validar código", e);
        }

        String sqlActualizar = "UPDATE Usuarios SET estado = 'activo', token_verificacion = NULL, token_expiracion = NULL "
                + "WHERE correo = ?";
        try (Connection con = ConexionMySQL.getConnection();
             PreparedStatement ps = con.prepareStatement(sqlActualizar))
        {
            ps.setString(1, correo);
            ps.executeUpdate();
            return "ok";
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Error al activar cuenta", e);
        }
    }

    private Usuario mapear(ResultSet rs) throws SQLException
    {
        Usuario u = new Usuario();
        u.setIdUsuario(rs.getInt("id_usuario"));
        u.setMatricula(rs.getString("matricula"));
        u.setNombre(rs.getString("nombre"));
        u.setApellidoP(rs.getString("apellido_p"));
        u.setApellidoM(rs.getString("apellido_m"));
        u.setCorreo(rs.getString("correo"));
        u.setContrasena(rs.getString("contrasena"));
        u.setTipoUsuario(rs.getString("tipo_usuario"));
        u.setEstado(rs.getString("estado"));
        return u;
    }
}