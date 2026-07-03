<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="modelo.Alumno"%>
<%@page import="dao.DAOAlumno"%>
<%!
    DAOAlumno lista = new DAOAlumno();
    Alumno edit = null;
%>
<%
    edit = null;
    if (request.getAttribute("edit") != null)
    {
        edit = (Alumno) request.getAttribute("edit");
    }
%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>CRUD Servlet</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/alumnos.css">
    </head>
    <body>
        <div class="contenedor">
            <div class="columna_tabla">
                <%
                    if (lista != null)
                    {
                        out.print(lista.mostrar());
                    }
                    else
                    {
                        out.print("<p>No hay registros disponibles</p>");
                    }
                %>
            </div>
            <div class="columna_formulario">
                <div id="form_registro">
                    <h2><%= (edit != null) ? "Modificar calificaciones" : "Registro de calificaciones"%></h2>
                    <% if (request.getAttribute("errorAlumno") != null) { %>
                        <p style="color:red;"><%= request.getAttribute("errorAlumno") %></p>
                    <% } %>
                    <form method="post" action="SAlumno">
                        <input type="hidden" name="accion"         value="<%= (edit != null) ? "Modificar" : "Agregar"%>"/>
                        <input type="hidden" name="tfMatriculaOld" value="<%= (edit != null) ? edit.getMatricula() : ""%>"/>
                        <input type="text"   name="tfMatricula"    value="<%= (edit != null) ? edit.getMatricula() : ""%>" placeholder="Matricula"  required/>
                        <input type="text"   name="tfNombre"       value="<%= (edit != null) ? edit.getNombre() : ""%>"    placeholder="Nombre"     required/>
                        <input type="text"   name="tfPaterno"      value="<%= (edit != null) ? edit.getPaterno() : ""%>"   placeholder="Apellido P" required/>
                        <input type="text"   name="tfMaterno"      value="<%= (edit != null) ? edit.getMaterno() : ""%>"   placeholder="Apellido M" required/>
                        <input type="text"   name="tfP1"           value="<%= (edit != null) ? edit.getP1() : ""%>"        placeholder="P1"         required/>
                        <input type="text"   name="tfP2"           value="<%= (edit != null) ? edit.getP2() : ""%>"        placeholder="P2"         required/>
                        <input type="text"   name="tfP3"           value="<%= (edit != null) ? edit.getP3() : ""%>"        placeholder="P3"         required/>
                        <input type="submit"                       value="<%= (edit != null) ? "Modificar" : "Agregar"%>"/>
                    </form>
                </div>
            </div>
        </div>
    </body>
</html>