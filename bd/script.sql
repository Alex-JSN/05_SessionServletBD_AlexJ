CREATE TABLE `Usuarios`
(
  `id_usuario`  INT(12) AUTO_INCREMENT NOT NULL,
  `matricula`   VARCHAR(15)  NOT NULL,
  `nombre`      VARCHAR(50)  NOT NULL,
  `apellido_p`  VARCHAR(50)  NOT NULL,
  `apellido_m`  VARCHAR(50)  NOT NULL,
  `correo`      VARCHAR(255) NOT NULL,
  `contrasena`  VARCHAR(255) NOT NULL,
  `tipo_usuario` ENUM('administrador', 'alumno')          NOT NULL,
  `estado`       ENUM('activo', 'inactivo', 'suspendido') NOT NULL,
  `token_verificacion` VARCHAR(255) NULL,
  `token_expiracion`   DATETIME NULL,
  PRIMARY KEY (`id_usuario`),
  UNIQUE INDEX `matricula_correo_uk` (`matricula` ASC, `correo` ASC)
) ENGINE = InnoDB;


CREATE TABLE `Materias`
(
  `id_materia` INT(12) AUTO_INCREMENT NOT NULL,
  `materia`    VARCHAR(100) NOT NULL,
  PRIMARY KEY (`id_materia`)
) ENGINE = InnoDB;

CREATE TABLE `Calificaciones`
(
  `id_calificacion` INT(12) AUTO_INCREMENT NOT NULL,
  `id_usuario`      INT(12) NOT NULL,
  `id_materia`      INT(12) NOT NULL,
  `parcial1`        DECIMAL(4,1) NOT NULL,
  `parcial2`        DECIMAL(4,1) NOT NULL,
  `parcial3`        DECIMAL(4,1) NOT NULL,
  INDEX `fk_Calificaciones_Usuarios_idx` (`id_usuario` ASC),
  INDEX `fk_Calificaciones_Materias_idx` (`id_materia` ASC),
  PRIMARY KEY (`id_calificacion`),
  CONSTRAINT `fk_Calificaciones_Usuarios`
    FOREIGN KEY (`id_usuario`) REFERENCES `Usuarios` (`id_usuario`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_Calificaciones_Materias`
    FOREIGN KEY (`id_materia`) REFERENCES `Materias` (`id_materia`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB;