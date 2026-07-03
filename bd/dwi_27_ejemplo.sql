CREATE TABLE `Calificaciones`
(
  `id_calificacion` int NOT NULL,
  `id_usuario`      int NOT NULL,
  `id_materia`      int NOT NULL,
  `parcial1`        decimal(4,1) NOT NULL,
  `parcial2`        decimal(4,1) NOT NULL,
  `parcial3`        decimal(4,1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;

INSERT INTO `Calificaciones` (`id_calificacion`, `id_usuario`, `id_materia`, `parcial1`, `parcial2`, `parcial3`) VALUES
(2, 1, 1, 9.0, 9.0, 10.0),
(4, 2, 1, 9.0, 9.0, 10.0);

CREATE TABLE `Materias`
(
  `id_materia` int NOT NULL,
  `materia`    varchar(100) COLLATE utf8mb4_spanish_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;

INSERT INTO `Materias` (`id_materia`, `materia`) VALUES
(1, 'Programming');

CREATE TABLE `Usuarios`
(
  `id_usuario` int NOT NULL,
  `matricula`  varchar(15)  COLLATE utf8mb4_spanish_ci NOT NULL,
  `nombre`     varchar(50)  COLLATE utf8mb4_spanish_ci NOT NULL,
  `apellido_p` varchar(50)  COLLATE utf8mb4_spanish_ci NOT NULL,
  `apellido_m` varchar(50)  COLLATE utf8mb4_spanish_ci NOT NULL,
  `correo`     varchar(255) COLLATE utf8mb4_spanish_ci NOT NULL,
  `contrasena` varchar(255) COLLATE utf8mb4_spanish_ci NOT NULL,
  `tipo_usuario` enum('administrador','alumno')         COLLATE utf8mb4_spanish_ci NOT NULL,
  `estado`       enum('activo','inactivo','suspendido') COLLATE utf8mb4_spanish_ci NOT NULL,
  `token_verificacion` varchar(255) COLLATE utf8mb4_spanish_ci DEFAULT NULL,
  `token_expiracion`   datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;

INSERT INTO `Usuarios` (`id_usuario`, `matricula`, `nombre`, `apellido_p`, `apellido_m`, `correo`, `contrasena`, `tipo_usuario`, `estado`, `token_verificacion`, `token_expiracion`) VALUES
(1, '57231900100', 'Alex Javier', 'Santos', 'Nava', 'coursejapandev@gmail.com', 'JCeMyX84W1f8dOtgBWVj5Q==:EzLYT+rKBifnA3W0UnTeow==', 'alumno', 'activo', NULL, NULL),
(2, '57231900101', 'Alison', 'Santos', 'Ramirez', 'coursecanadadev@gmail.com', 'oNOEQfMi3SZinJSRUkdVTw==:7w2ewzWjZN+M8WvAL901aA==', 'alumno', 'activo', NULL, NULL),
(5, '57231900102', 'AlexDev', 'Santos', 'Nava', 'courselatamdev@gmail.com', 'vc7pY7oE+FKLVUF1k2/Y/A==:66zVeyEKRf9pMww0RKDirw==', 'alumno', 'activo', NULL, NULL);

ALTER TABLE `Calificaciones`
  ADD PRIMARY KEY (`id_calificacion`),
  ADD KEY `fk_Calificaciones_Usuarios_idx` (`id_usuario`),
  ADD KEY `fk_Calificaciones_Materias_idx` (`id_materia`);

ALTER TABLE `Materias`
  ADD PRIMARY KEY (`id_materia`);

ALTER TABLE `Usuarios`
  ADD PRIMARY KEY (`id_usuario`),
  ADD UNIQUE KEY `matricula_correo_uk` (`matricula`,`correo`);

ALTER TABLE `Calificaciones`
  MODIFY `id_calificacion` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

ALTER TABLE `Materias`
  MODIFY `id_materia` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

ALTER TABLE `Usuarios`
  MODIFY `id_usuario` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

ALTER TABLE `Calificaciones`
  ADD CONSTRAINT `fk_Calificaciones_Materias` FOREIGN KEY (`id_materia`) REFERENCES `Materias` (`id_materia`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_Calificaciones_Usuarios` FOREIGN KEY (`id_usuario`) REFERENCES `Usuarios` (`id_usuario`) ON DELETE CASCADE ON UPDATE CASCADE;
COMMIT;
