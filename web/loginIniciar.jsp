<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Iniciar sesión</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/estilosLogin.css">
</head>
</html>
<body>
    <div class="contenedor-login">
        <h2>Iniciar sesión</h2>

        <% if (request.getAttribute("error") != null) { %>
            <p class="mensaje-error"><%= request.getAttribute("error") %></p>
        <% } %>

        <% if (request.getAttribute("mensaje") != null) { %>
            <p class="mensaje-exito"><%= request.getAttribute("mensaje") %></p>
        <% } %>

        <form action="${pageContext.request.contextPath}/SLogin" method="post">
            <label  for="correo">Correo electrónico</label>
            <input  type="email"    id="correo"     name="correo"     placeholder="correo@gmail.com" required>
            <label  for="contrasena">Contraseña</label>
            <input  type="password" id="contrasena" name="contrasena" placeholder="Contraseña"       required>
            <button type="submit">Entrar</button>
        </form>
        <p>¿No tienes cuenta? <a href="${pageContext.request.contextPath}/SRegistro">Regístrate aquí</a></p>
    </div>

</body>
</html>
