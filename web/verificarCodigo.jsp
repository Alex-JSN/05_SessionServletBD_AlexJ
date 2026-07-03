
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Verificar correo</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/estilosLogin.css">
</head>
<body>

    <div class="contenedor-registro">
        <h2>Verifica tu correo</h2>
        <p>Te enviamos un código de 6 dígitos. Ingrésalo para activar tu cuenta.</p>

        <% if (request.getAttribute("error") != null) { %>
            <p class="mensaje-error"><%= request.getAttribute("error") %></p>
        <% } %>

        <% if (request.getAttribute("mensaje") != null) { %>
            <p class="mensaje-exito"><%= request.getAttribute("mensaje") %></p>
        <% } %>

        <form action="${pageContext.request.contextPath}/SVerificarCodigo" method="post">
            <input  type="hidden"            name="accion" value="verificar">
            <label  for="correo">Correo electrónico</label>
            <input  type="email" id="correo" name="correo" value="<%= request.getAttribute("correo") != null ? request.getAttribute("correo") : "" %>" required>
            <label  for="codigo">Código de verificación</label>
            <input  type="text"  id="codigo" name="codigo" maxlength="6" inputmode="numeric" pattern="\d{6}" required>
            <button type="submit">Verificar</button>
        </form>

        <form action="${pageContext.request.contextPath}/SVerificarCodigo" method="post">
            <input  type="hidden" name="accion" value="reenviar">
            <input  type="hidden" name="correo" value="<%= request.getAttribute("correo") != null ? request.getAttribute("correo") : "" %>">
            <button type="submit" class="boton-secundario">Reenviar código</button>
        </form>

        <p>¿Ya tienes cuenta verificada? <a href="${pageContext.request.contextPath}/SLogin">Inicia sesión aquí</a></p>
    </div>

</body>
</html>

