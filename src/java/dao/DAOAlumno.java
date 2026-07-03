package dao;

import modelo.Alumno;
import conexion.ConexionMySQL;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * DAO adaptado a la nueva base de datos (Usuarios + Calificaciones).
 *
 * DISEÑO ACTUAL (transición):
 * - Los alumnos que aparecen aquí YA deben existir en la tabla Usuarios
 *   (se registran mediante el flujo de login/registro con correo y contraseña).
 *   Este panel NO crea cuentas nuevas, solo administra sus calificaciones.
 * - Cada calificación pertenece a una materia (Calificaciones.id_materia), pero
 *   como todavía no hay un selector de materia en el formulario, se usa una
 *   materia por default (ID_MATERIA_DEFAULT).
 *
 * PARA ACTIVAR MULTI-MATERIA A FUTURO:
 * 1. Agrega un <select> de materia en alumnos.jsp (poblado desde la tabla Materias).
 * 2. Descomenta las líneas marcadas con "MATERIA:" en este archivo.
 * 3. Reemplaza el uso de ID_MATERIA_DEFAULT por el id_materia que venga del formulario.
 */
public class DAOAlumno
{
    // TODO MATERIA: mientras no haya selector de materia en el formulario, se usa esta por default.
    // Ajusta el valor al id_materia real que quieras usar para pruebas (revisa: SELECT * FROM Materias;)
    private static final int ID_MATERIA_DEFAULT = 1;

    public ArrayList<Alumno> listar()
    {
        ArrayList<Alumno> list = new ArrayList<>();

        String sql = "SELECT u.matricula, u.nombre, u.apellido_p, u.apellido_m, "
                + "c.parcial1, c.parcial2, c.parcial3 "
                // MATERIA: descomenta esta columna cuando quieras mostrar el nombre de la materia
                // + ", m.materia AS materia_nombre "
                + "FROM Usuarios u "
                + "LEFT JOIN Calificaciones c ON u.id_usuario = c.id_usuario "
                // MATERIA: descomenta este JOIN junto con la columna de arriba
                // + "LEFT JOIN Materias m ON c.id_materia = m.id_materia "
                + "WHERE u.tipo_usuario = 'alumno' "
                + "ORDER BY u.nombre";

        try (Connection con = ConexionMySQL.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery())
        {
            while (rs.next())
            {
                list.add(mapear(rs));
            }
        }
        catch (SQLException e)
        {
            System.out.println("Error en listar(): " + e.getMessage());
        }
        return list;
    }

    public String mostrar()
    {
        String r = """
            <br><br>
            <table border="0">
                <caption>Lista de Alumnos</caption>
                <thead>
                    <tr>
                        <th>Matricula</th>
                        <th>Nombre</th>
                        <th>Paterno</th>
                        <th>Materno</th>
                        <th>DDI</th>
                        <th>DWI</th>
                        <th>ECBD</th>
                        <th>Promedio</th>
                        <th colspan="2">Acciones</th>
                    </tr>
                </thead>
            <tbody>
                """;

        for (Alumno reg : listar())
        {
            String fila = """
                <tr>
                    <td>%s</td>
                    <td>%s</td>
                    <td>%s</td>
                    <td>%s</td>
                    <td>%s</td>
                    <td>%s</td>
                    <td>%s</td>
                    <td>%.1f</td>
                    <td>
                        <form method="post" action="SAlumno">
                            <input type="hidden" name="accion"      value="Editar"/>
                            <input type="hidden" name="tfMatricula" value="%s"/>
                            <button type="submit">Editar</button>
                        </form>
                    </td>
                    <td>
                        <form method="post" action="SAlumno">
                            <input type="hidden" name="accion"      value="Eliminar"/>
                            <input type="hidden" name="tfMatricula" value="%s"/>
                            <input type="submit" value="Eliminar"/>
                        </form>
                    </td>
                </tr>
                """;
            r = r + String.format(fila,
                reg.getMatricula(),
                reg.getNombre(),
                reg.getPaterno(),
                reg.getMaterno(),
                reg.getP1() != null ? reg.getP1() : "-",
                reg.getP2() != null ? reg.getP2() : "-",
                reg.getP3() != null ? reg.getP3() : "-",
                reg.getPromedio(),
                reg.getMatricula(),
                reg.getMatricula());
        }

        r = r + """
                </tbody>
             </table>
            """;
        return r;
    }

    public Alumno buscar(long matricula)
    {
        Alumno alumno = null;
        String sql = "SELECT u.matricula, u.nombre, u.apellido_p, u.apellido_m, "
                + "c.parcial1, c.parcial2, c.parcial3 "
                + "FROM Usuarios u "
                + "LEFT JOIN Calificaciones c ON u.id_usuario = c.id_usuario "
                // MATERIA: cuando haya varias materias por alumno, filtra aquí por la materia elegida
                // + "AND c.id_materia = ? "
                + "WHERE u.matricula = ?";

        try (Connection con = ConexionMySQL.getConnection();
             PreparedStatement ps = con.prepareStatement(sql))
        {
            ps.setString(1, String.valueOf(matricula));
            try (ResultSet rs = ps.executeQuery())
            {
                if (rs.next())
                {
                    alumno = mapear(rs);
                }
            }
        }
        catch (SQLException e)
        {
            System.out.println("Error en buscar(): " + e.getMessage());
        }
        return alumno;
    }

    /** Agrega (o actualiza si ya existe) la calificación del alumno para la materia por default. */
    public boolean agregar(Alumno alumno)
    {
        Integer idUsuario = obtenerIdUsuario(alumno.getMatricula());
        if (idUsuario == null)
        {
            System.out.println("Error en agregar(): la matrícula " + alumno.getMatricula()
                    + " no está registrada en Usuarios. El alumno debe registrarse primero (login/registro).");
            return false;
        }

        // Si ya existe una calificación para este alumno y esta materia, actualízala en vez de duplicar.
        if (existeCalificacion(idUsuario, ID_MATERIA_DEFAULT))
        {
            return actualizarCalificacion(idUsuario, ID_MATERIA_DEFAULT, alumno);
        }

        String sql = "INSERT INTO Calificaciones (id_usuario, id_materia, parcial1, parcial2, parcial3) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection con = ConexionMySQL.getConnection();
             PreparedStatement ps = con.prepareStatement(sql))
        {
            ps.setInt(1, idUsuario);
            ps.setInt(2, ID_MATERIA_DEFAULT); // MATERIA: reemplaza por el id_materia real cuando exista selector
            ps.setBigDecimal(3, new BigDecimal(alumno.getP1()));
            ps.setBigDecimal(4, new BigDecimal(alumno.getP2()));
            ps.setBigDecimal(5, new BigDecimal(alumno.getP3()));
            ps.executeUpdate();
            return true;
        }
        catch (SQLException | NumberFormatException e)
        {
            System.out.println("Error en agregar(): " + e.getMessage());
            return false;
        }
    }

    /** Actualiza los datos del alumno (nombre) y su calificación. matriculaold identifica al registro a modificar. */
    public boolean editar(Alumno alumno, long matriculaold)
    {
        Integer idUsuario = obtenerIdUsuario(String.valueOf(matriculaold));
        if (idUsuario == null)
        {
            System.out.println("Error en editar(): no se encontró la matrícula " + matriculaold);
            return false;
        }

        String sqlUsuario = "UPDATE Usuarios SET matricula = ?, nombre = ?, apellido_p = ?, apellido_m = ? "
                + "WHERE id_usuario = ?";

        try (Connection con = ConexionMySQL.getConnection();
             PreparedStatement ps = con.prepareStatement(sqlUsuario))
        {
            ps.setString(1, alumno.getMatricula());
            ps.setString(2, alumno.getNombre());
            ps.setString(3, alumno.getPaterno());
            ps.setString(4, alumno.getMaterno());
            ps.setInt(5, idUsuario);
            ps.executeUpdate();
        }
        catch (SQLException e)
        {
            System.out.println("Error en editar() [datos de usuario]: " + e.getMessage());
            return false;
        }

        if (existeCalificacion(idUsuario, ID_MATERIA_DEFAULT))
        {
            return actualizarCalificacion(idUsuario, ID_MATERIA_DEFAULT, alumno);
        }
        else
        {
            // No tenía calificación todavía para esta materia: la creamos.
            String sqlInsert = "INSERT INTO Calificaciones (id_usuario, id_materia, parcial1, parcial2, parcial3) "
                    + "VALUES (?, ?, ?, ?, ?)";
            try (Connection con = ConexionMySQL.getConnection();
                 PreparedStatement ps = con.prepareStatement(sqlInsert))
            {
                ps.setInt(1, idUsuario);
                ps.setInt(2, ID_MATERIA_DEFAULT);
                ps.setBigDecimal(3, new BigDecimal(alumno.getP1()));
                ps.setBigDecimal(4, new BigDecimal(alumno.getP2()));
                ps.setBigDecimal(5, new BigDecimal(alumno.getP3()));
                ps.executeUpdate();
                return true;
            }
            catch (SQLException | NumberFormatException e)
            {
                System.out.println("Error en editar() [insertar calificación]: " + e.getMessage());
                return false;
            }
        }
    }

    /**
     * Elimina la calificación del alumno para la materia por default.
     * NOTA: a propósito NO borra al usuario de la tabla Usuarios, para no eliminar
     * su cuenta de acceso (correo/contraseña) junto con su calificación.
     */
    public boolean eliminar(long matricula)
    {
        Integer idUsuario = obtenerIdUsuario(String.valueOf(matricula));
        if (idUsuario == null)
        {
            System.out.println("Error en eliminar(): no se encontró la matrícula " + matricula);
            return false;
        }

        String sql = "DELETE FROM Calificaciones WHERE id_usuario = ? AND id_materia = ?";

        try (Connection con = ConexionMySQL.getConnection();
             PreparedStatement ps = con.prepareStatement(sql))
        {
            ps.setInt(1, idUsuario);
            ps.setInt(2, ID_MATERIA_DEFAULT);
            ps.executeUpdate();
            return true;
        }
        catch (SQLException e)
        {
            System.out.println("Error en eliminar(): " + e.getMessage());
            return false;
        }
    }

    // ---------- Helpers internos ----------

    private Alumno mapear(ResultSet rs) throws SQLException
    {
        Alumno alumno = new Alumno();
        alumno.setMatricula(rs.getString("matricula"));
        alumno.setNombre(rs.getString("nombre"));
        alumno.setPaterno(rs.getString("apellido_p"));
        alumno.setMaterno(rs.getString("apellido_m"));
        alumno.setP1(rs.getString("parcial1"));
        alumno.setP2(rs.getString("parcial2"));
        alumno.setP3(rs.getString("parcial3"));
        // MATERIA: cuando actives el JOIN con Materias, aquí puedes hacer:
        // alumno.setMateria(rs.getString("materia_nombre"));
        return alumno;
    }

    private Integer obtenerIdUsuario(String matricula)
    {
        String sql = "SELECT id_usuario FROM Usuarios WHERE matricula = ?";
        try (Connection con = ConexionMySQL.getConnection();
             PreparedStatement ps = con.prepareStatement(sql))
        {
            ps.setString(1, matricula);
            try (ResultSet rs = ps.executeQuery())
            {
                if (rs.next())
                {
                    return rs.getInt("id_usuario");
                }
            }
        }
        catch (SQLException e)
        {
            System.out.println("Error en obtenerIdUsuario(): " + e.getMessage());
        }
        return null;
    }

    private boolean existeCalificacion(int idUsuario, int idMateria)
    {
        String sql = "SELECT id_calificacion FROM Calificaciones WHERE id_usuario = ? AND id_materia = ?";
        try (Connection con = ConexionMySQL.getConnection();
             PreparedStatement ps = con.prepareStatement(sql))
        {
            ps.setInt(1, idUsuario);
            ps.setInt(2, idMateria);
            try (ResultSet rs = ps.executeQuery())
            {
                return rs.next();
            }
        }
        catch (SQLException e)
        {
            System.out.println("Error en existeCalificacion(): " + e.getMessage());
            return false;
        }
    }

    private boolean actualizarCalificacion(int idUsuario, int idMateria, Alumno alumno)
    {
        String sql = "UPDATE Calificaciones SET parcial1 = ?, parcial2 = ?, parcial3 = ? "
                + "WHERE id_usuario = ? AND id_materia = ?";
        try (Connection con = ConexionMySQL.getConnection();
             PreparedStatement ps = con.prepareStatement(sql))
        {
            ps.setBigDecimal(1, new BigDecimal(alumno.getP1()));
            ps.setBigDecimal(2, new BigDecimal(alumno.getP2()));
            ps.setBigDecimal(3, new BigDecimal(alumno.getP3()));
            ps.setInt(4, idUsuario);
            ps.setInt(5, idMateria);
            ps.executeUpdate();
            return true;
        }
        catch (SQLException | NumberFormatException e)
        {
            System.out.println("Error en actualizarCalificacion(): " + e.getMessage());
            return false;
        }
    }
}