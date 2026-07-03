<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Registrarse</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/estilosLogin.css">
</head>
<body>
    <div class="contenedor-registro">
        <h2>Crear cuenta</h2>
        <% if (request.getAttribute("error") != null) { %>
            <p class="mensaje-error"><%= request.getAttribute("error") %></p>
        <% } %>

        <% if (request.getAttribute("mensaje") != null) { %>
            <p class="mensaje-exito"><%= request.getAttribute("mensaje") %></p>
        <% } %>

        <form action="${pageContext.request.contextPath}/SRegistro" method="post">
            <label  for="matricula">Matrícula</label>
            <input  type="text"     id="matricula"  name="matricula"   placeholder="Matricula"        required>
            <label  for="nombre">Nombre</label>
            <input  type="text"     id="nombre"     name="nombre"      placeholder="Nombre"           required>
            <label  for="apellidoP">Apellido paterno</label>
            <input  type="text"     id="apellidoP"  name="apellidoP"   placeholder="Apellido P"       required>
            <label  for="apellidoM">Apellido materno</label>
            <input  type="text"     id="apellidoM"  name="apellidoM"   placeholder="Apellido M"       required>
            <label  for="correo">Correo electrónico</label>
            <input  type="email"    id="correo"     name="correo"      placeholder="correo@gmail.com" required>
            <label  for="contrasena">Contraseña</label>
            <input  type="password" id="contrasena" name="contrasena" minlength="8" placeholder="Contraseña" required>
            <button type="submit">Registrarme</button>
        </form>

        <p>¿Ya tienes cuenta? <a href="${pageContext.request.contextPath}/SLogin">Inicia sesión aquí</a></p>
    </div>
</body>
</html>
