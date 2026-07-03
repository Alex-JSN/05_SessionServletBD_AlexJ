package control;

import modelo.Alumno;
import dao.DAOAlumno;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException; 
import java.util.logging.Level;
import java.util.logging.Logger;
 

@WebServlet(name = "SAlumno", urlPatterns = {"/SAlumno"}) public class SAlumno extends HttpServlet
{
    private Alumno alumno;
    private DAOAlumno dao;
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException
    {
        response.setContentType("text/html;charset=UTF-8");
        Alumno edit = null;
        
        try
        {
            String accion = request.getParameter("accion");
            if ("Agregar".equals(accion))
            {
                alumno = new Alumno();
                alumno.setMatricula(request.getParameter("tfMatricula"));
                alumno.setNombre(request.getParameter("tfNombre"));
                alumno.setPaterno(request.getParameter("tfPaterno"));
                alumno.setMaterno(request.getParameter("tfMaterno"));
                alumno.setP1(request.getParameter("tfP1"));
                alumno.setP2(request.getParameter("tfP2"));
                alumno.setP3(request.getParameter("tfP3"));
                
                dao = new DAOAlumno();
                boolean ok = dao.agregar(alumno);
                if (!ok)
                {
                    request.setAttribute("errorAlumno",
                        "No se pudo agregar la calificación. Verifica que la matrícula "
                        + alumno.getMatricula() + " ya esté registrada (login) y que exista "
                        + "al menos una materia en la tabla Materias (revisa la consola del servidor).");
                }
                
                request.setAttribute("edit", edit);
                
                RequestDispatcher rd = getServletContext().getRequestDispatcher("/alumnos.jsp");
                rd.forward(request, response);
            }
            else if ("Editar".equals(accion))
            {
                dao = new DAOAlumno();
                edit = dao.buscar(Long.parseLong(request.getParameter("tfMatricula")));
               
                request.setAttribute("edit", edit);
                
                RequestDispatcher rd = getServletContext().getRequestDispatcher("/alumnos.jsp");
                rd.forward(request, response);
            }
            else if ("Modificar".equals(accion))
            {
                alumno = new Alumno();
                alumno.setMatricula(request.getParameter("tfMatricula"));
                alumno.setNombre(request.getParameter("tfNombre"));
                alumno.setPaterno(request.getParameter("tfPaterno"));
                alumno.setMaterno(request.getParameter("tfMaterno"));
 
                alumno.setP1(request.getParameter("tfP1"));
                alumno.setP2(request.getParameter("tfP2"));
                alumno.setP3(request.getParameter("tfP3"));
                
                dao = new DAOAlumno();
                boolean ok = dao.editar(alumno, Long.parseLong(request.getParameter("tfMatriculaOld")));
                if (!ok)
                {
                    request.setAttribute("errorAlumno",
                        "No se pudo guardar la calificación. Verifica que exista al menos una "
                        + "materia en la tabla Materias (revisa la consola del servidor para el detalle).");
                }
                
                edit = null;
                request.setAttribute("edit", edit);
                
                RequestDispatcher rd = getServletContext().getRequestDispatcher("/alumnos.jsp");
                rd.forward(request, response);
            }
            else if ("Eliminar".equals(accion))
            {
                long n1 = Long.parseLong(request.getParameter("tfMatricula"));
                dao = new DAOAlumno();
                dao.eliminar(n1);
                
                request.setAttribute("edit", edit);
                
                RequestDispatcher rd = getServletContext().getRequestDispatcher("/alumnos.jsp");
                rd.forward(request, response);
            }
            else
            {
                request.setAttribute("edit", edit);
                
                RequestDispatcher rd = getServletContext().getRequestDispatcher("/alumnos.jsp");
                rd.forward(request, response);
            }
            
        } catch (IOException | ServletException ex)
        {
            Logger.getLogger(SAlumno.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        processRequest(request, response);
    }
 
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        processRequest(request, response);
    }
 
    @Override
    public String getServletInfo()
    {
        return "Short description";
    }
}

