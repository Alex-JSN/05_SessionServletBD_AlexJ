package control;


import dao.DAOUsuario;
import modelo.Usuario;
import util.EmailUtil;
 
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@WebServlet(name = "SRegistro", urlPatterns = {"/SRegistro"}) public class SRegistro extends HttpServlet
{

    private final DAOUsuario daoUsuario = new DAOUsuario();
 
    /**
     * Muestra el formulario de registro.
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        request.getRequestDispatcher("loginRegistrar.jsp").forward(request, response);
    }
 
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        processRequest(request, response);
    }
 
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
 
        request.setCharacterEncoding("UTF-8");
 
        String matricula = request.getParameter("matricula");
        String nombre = request.getParameter("nombre");
        String apellidoP = request.getParameter("apellidoP");
        String apellidoM = request.getParameter("apellidoM");
        String correo = request.getParameter("correo");
        String contrasena = request.getParameter("contrasena");
        String tipoUsuario = "alumno";
 
        if (matricula == null || matricula.isBlank()
                || nombre == null || nombre.isBlank()
                || correo == null || !correo.matches("^[\\w.+-]+@[\\w-]+\\.[a-zA-Z]{2,}$")
                || contrasena == null || contrasena.length() < 8)
        {
            request.setAttribute("error", "Revisa tus datos: correo inválido o contraseña muy corta (mínimo 8 caracteres).");
            request.getRequestDispatcher("loginRegistrar.jsp").forward(request, response);
            return;
        }
 
        if (daoUsuario.existeCorreo(correo))
        {
            request.setAttribute("error", "Ese correo ya está registrado.");
            request.getRequestDispatcher("loginRegistrar.jsp").forward(request, response);
            return;
        }
 
        if (daoUsuario.existeMatricula(matricula))
        {
            request.setAttribute("error", "Esa matrícula ya está registrada.");
            request.getRequestDispatcher("loginRegistrar.jsp").forward(request, response);
            return;
        }
 
        Usuario nuevo = new Usuario(matricula, nombre, apellidoP, apellidoM, correo, contrasena, tipoUsuario);
        String codigo = daoUsuario.registrar(nuevo);
 
        try
        {
            EmailUtil.enviarCorreoVerificacion(correo, nombre, codigo);
        }
        catch (RuntimeException e)
        {
            request.setAttribute("error", "Te registramos, pero no pudimos enviar el correo de verificación. Contacta al administrador.");
            request.getRequestDispatcher("loginRegistrar.jsp").forward(request, response);
            return;
        }
 
        request.setAttribute("correo", correo);
        request.setAttribute("mensaje", "Te enviamos un código de verificación a tu correo. Ingrésalo para activar tu cuenta.");
        request.getRequestDispatcher("verificarCodigo.jsp").forward(request, response);
    }
 
    @Override
    public String getServletInfo()
    {
        return "Servlet de registro de usuarios";
    }
}
