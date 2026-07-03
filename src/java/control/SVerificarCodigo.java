package control;
 
import dao.DAOUsuario;
 
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import util.EmailUtil;


@WebServlet(name = "SVerificarCodigo", urlPatterns = {"/SVerificarCodigo"}) public class SVerificarCodigo extends HttpServlet
{
    private final DAOUsuario daoUsuario = new DAOUsuario();
 
    /** Muestra el formulario para ingresar el código. */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        request.getRequestDispatcher("verificarCodigo.jsp").forward(request, response);
    }
 
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        processRequest(request, response);
    }
 
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
 
        request.setCharacterEncoding("UTF-8");
 
        String correo = request.getParameter("correo");
        String codigo = request.getParameter("codigo");
        String accion = request.getParameter("accion"); // "verificar" o "reenviar"
 
        if ("reenviar".equals(accion))
        {
            reenviarCodigo(request, response, correo);
            return;
        }
 
        if (correo == null || correo.isBlank() || codigo == null || codigo.isBlank())
        {
            request.setAttribute("error", "Ingresa tu correo y el código.");
            request.setAttribute("correo", correo);
            request.getRequestDispatcher("verificarCodigo.jsp").forward(request, response);
            return;
        }
 
        String resultado = daoUsuario.activarCuenta(correo, codigo.trim());
 
        switch (resultado)
        {
            case "ok":
                request.setAttribute("mensaje", "Tu cuenta fue activada correctamente. Ya puedes iniciar sesión.");
                request.getRequestDispatcher("loginIniciar.jsp").forward(request, response);
                return;
            case "expirado":
                request.setAttribute("error", "El código expiró. Solicita uno nuevo.");
                break;
            default:
                request.setAttribute("error", "El código ingresado no es correcto.");
        }
 
        request.setAttribute("correo", correo);
        request.getRequestDispatcher("verificarCodigo.jsp").forward(request, response);
    }
 
    private void reenviarCodigo(HttpServletRequest request, HttpServletResponse response, String correo)
            throws ServletException, IOException
    {
 
        if (correo == null || correo.isBlank())
        {
            request.setAttribute("error", "Escribe tu correo para poder reenviarte el código.");
            request.getRequestDispatcher("verificarCodigo.jsp").forward(request, response);
            return;
        }
 
        String nuevoCodigo = daoUsuario.regenerarCodigo(correo);
 
        if (nuevoCodigo == null)
        {
            request.setAttribute("error", "Ese correo no está pendiente de verificación.");
            request.setAttribute("correo", correo);
            request.getRequestDispatcher("verificarCodigo.jsp").forward(request, response);
            return;
        }
 
        try
        {
            // No tenemos el nombre a la mano aquí; usamos el correo como saludo genérico.
            EmailUtil.enviarCorreoVerificacion(correo, "estudiante", nuevoCodigo);
        }
        catch (RuntimeException e)
        {
            request.setAttribute("error", "No pudimos reenviar el código. Intenta más tarde.");
            request.setAttribute("correo", correo);
            request.getRequestDispatcher("verificarCodigo.jsp").forward(request, response);
            return;
        }
 
        request.setAttribute("mensaje", "Te reenviamos un nuevo código a tu correo.");
        request.setAttribute("correo", correo);
        request.getRequestDispatcher("verificarCodigo.jsp").forward(request, response);
    }
 
    @Override
    public String getServletInfo()
    {
        return "Servlet de verificación de código de correo";
    }
}
