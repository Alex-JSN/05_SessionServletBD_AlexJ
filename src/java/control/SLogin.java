package control;

import dao.DAOUsuario;
import modelo.Usuario;
import util.PasswordUtil;
 
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "SLogin", urlPatterns = {"/SLogin"}) public class SLogin extends HttpServlet
{
    
    private final DAOUsuario daoUsuario = new DAOUsuario();
 
    /**
     * Muestra el formulario de inicio de sesión.
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        request.getRequestDispatcher("loginIniciar.jsp").forward(request, response);
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
 
        String correo = request.getParameter("correo");
        String contrasena = request.getParameter("contrasena");
 
        Usuario usuario = daoUsuario.obtenerPorCorreo(correo);
 
        if (usuario == null || !PasswordUtil.verificar(contrasena, usuario.getContrasena()))
        {
            request.setAttribute("error", "Correo o contraseña incorrectos.");
            request.getRequestDispatcher("loginIniciar.jsp").forward(request, response);
            return;
        }
 
        switch (usuario.getEstado())
        {
            case "inactivo":
                request.setAttribute("error", "Tu cuenta aún no está verificada. Revisa tu correo.");
                request.getRequestDispatcher("loginIniciar.jsp").forward(request, response);
                return;
            case "suspendido":
                request.setAttribute("error", "Tu cuenta está suspendida. Contacta al administrador.");
                request.getRequestDispatcher("loginIniciar.jsp").forward(request, response);
                return;
        }
 
        // Login correcto: crear sesión
        HttpSession sesion = request.getSession(true);
        sesion.setAttribute("usuario", usuario);
        sesion.setMaxInactiveInterval(30 * 60); // 30 minutos
 
        if ("administrador".equals(usuario.getTipoUsuario()))
        {
            response.sendRedirect(request.getContextPath() + "/SAlumno");
        }
        else
        {
            response.sendRedirect(request.getContextPath() + "/alumnos.jsp");
        }
    }
 
    @Override
    public String getServletInfo()
    {
        return "Servlet de inicio de sesión";
    }
}
