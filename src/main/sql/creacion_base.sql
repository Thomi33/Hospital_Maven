-- =====================================================================
-- PROYECTO INTEGRADOR - PROGRAMACION AVANZADA Y BASE DE DATOS
-- Sistema de Gestion de Equipamiento Clinico Hospitalario
-- ---------------------------------------------------------------------
-- Archivo : creacion_base.sql
-- Objetivo : Crear la Base de Datos "hospital" normalizada hasta la
--            Tercera Forma Normal (3FN), con claves primarias,
--            claves foraneas, restricciones UNIQUE, NOT NULL,
--            AUTO_INCREMENT e integridad referencial.
-- Motor   : MySQL 8.x / MariaDB 10.4+ (InnoDB)
-- =====================================================================

DROP DATABASE IF EXISTS hospital;
CREATE DATABASE hospital
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE hospital;

-- =====================================================================
-- TABLA 9: PAISES - RF009 (CRUD completo)
-- =====================================================================
CREATE TABLE paises (
    id_pais   INT UNSIGNED NOT NULL AUTO_INCREMENT,
    nombre    VARCHAR(80)  NOT NULL,
    PRIMARY KEY (id_pais),
    CONSTRAINT uq_paises_nombre UNIQUE (nombre)
) ENGINE = InnoDB;

-- =====================================================================
-- TABLA 8: INSTITUCIONES - RF008 (CRUD opcional, tabla obligatoria)
-- =====================================================================
CREATE TABLE instituciones (
    id_institucion INT UNSIGNED NOT NULL AUTO_INCREMENT,
    nombre         VARCHAR(120) NOT NULL,
    direccion      VARCHAR(150) NOT NULL,
    telefono       VARCHAR(30)  NOT NULL,
    ciudad         VARCHAR(80)  NOT NULL,
    id_pais        INT UNSIGNED NOT NULL,
    PRIMARY KEY (id_institucion),
    CONSTRAINT uq_instituciones_nombre UNIQUE (nombre),
    CONSTRAINT fk_instituciones_paises
        FOREIGN KEY (id_pais) REFERENCES paises (id_pais)
        ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE = InnoDB;

-- =====================================================================
-- TABLA 7: UBICACIONES - RF007 (CRUD completo)
-- =====================================================================
CREATE TABLE ubicaciones (
    id_ubicacion   INT UNSIGNED NOT NULL AUTO_INCREMENT,
    nombre         VARCHAR(80)  NOT NULL,
    descripcion    VARCHAR(150) NULL,
    id_institucion INT UNSIGNED NOT NULL,
    PRIMARY KEY (id_ubicacion),
    CONSTRAINT uq_ubicaciones_nombre UNIQUE (nombre),
    CONSTRAINT fk_ubicaciones_instituciones
        FOREIGN KEY (id_institucion) REFERENCES instituciones (id_institucion)
        ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE = InnoDB;

-- =====================================================================
-- TABLA 2: PERFILES - RF002
-- =====================================================================
CREATE TABLE perfiles (
    id_perfil    INT UNSIGNED NOT NULL AUTO_INCREMENT,
    nombre       VARCHAR(60)  NOT NULL,
    descripcion  VARCHAR(150) NOT NULL,
    estado       ENUM('ACTIVO', 'INACTIVO') NOT NULL DEFAULT 'ACTIVO',
    PRIMARY KEY (id_perfil),
    CONSTRAINT uq_perfiles_nombre UNIQUE (nombre)
) ENGINE = InnoDB;

-- =====================================================================
-- TABLA 3: FUNCIONALIDADES - RF003
-- =====================================================================
CREATE TABLE funcionalidades (
    id_funcionalidad INT UNSIGNED NOT NULL AUTO_INCREMENT,
    nombre           VARCHAR(80)  NOT NULL,
    descripcion      VARCHAR(150) NOT NULL,
    estado           ENUM('ACTIVO', 'INACTIVO') NOT NULL DEFAULT 'ACTIVO',
    PRIMARY KEY (id_funcionalidad),
    CONSTRAINT uq_funcionalidades_nombre UNIQUE (nombre)
) ENGINE = InnoDB;

-- =====================================================================
-- TABLA EXTRA: PERFIL_FUNCIONALIDAD (enlace N:N)
-- Evita atributos repetidos dentro de perfiles (1FN / 3FN)
-- =====================================================================
CREATE TABLE perfil_funcionalidad (
    id_perfil         INT UNSIGNED NOT NULL,
    id_funcionalidad  INT UNSIGNED NOT NULL,
    PRIMARY KEY (id_perfil, id_funcionalidad),
    CONSTRAINT fk_pf_perfiles
        FOREIGN KEY (id_perfil) REFERENCES perfiles (id_perfil)
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_pf_funcionalidades
        FOREIGN KEY (id_funcionalidad) REFERENCES funcionalidades (id_funcionalidad)
        ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE = InnoDB;

-- =====================================================================
-- TABLA 1: USUARIOS - RF001 (nombre de usuario unico)
-- =====================================================================
CREATE TABLE usuarios (
    id_usuario        INT UNSIGNED NOT NULL AUTO_INCREMENT,
    nombre            VARCHAR(60)  NOT NULL,
    apellido          VARCHAR(60)  NOT NULL,
    cedula            VARCHAR(20)  NOT NULL,
    fecha_nacimiento  DATE         NOT NULL,
    telefono          VARCHAR(30)  NOT NULL,
    email             VARCHAR(100) NOT NULL,
    usuario           VARCHAR(40)  NOT NULL,
    contrasena        VARCHAR(64)  NOT NULL, -- hash SHA-256 (64 caracteres hex)
    id_perfil         INT UNSIGNED NOT NULL,
    estado            ENUM('ACTIVO', 'INACTIVO') NOT NULL DEFAULT 'ACTIVO',
    fecha_alta        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id_usuario),
    CONSTRAINT uq_usuarios_usuario UNIQUE (usuario),
    CONSTRAINT uq_usuarios_cedula  UNIQUE (cedula),
    CONSTRAINT uq_usuarios_email   UNIQUE (email),
    CONSTRAINT fk_usuarios_perfiles
        FOREIGN KEY (id_perfil) REFERENCES perfiles (id_perfil)
        ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE = InnoDB;

-- =====================================================================
-- TABLA 4: AUDITORIA - RF004
-- El usuario se almacena como clave foranea y se resuelve por JOIN (3FN)
-- =====================================================================
CREATE TABLE auditoria (
    id_auditoria  BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    id_usuario    INT UNSIGNED    NULL,
    fecha         DATE            NOT NULL,
    hora          TIME            NOT NULL,
    operacion     VARCHAR(20)     NOT NULL, -- LOGIN / ALTA / MODIFICACION / BAJA / BORRADO / CONSULTA
    tabla         VARCHAR(40)     NOT NULL,
    detalle       VARCHAR(255)    NOT NULL,
    PRIMARY KEY (id_auditoria),
    CONSTRAINT fk_auditoria_usuarios
        FOREIGN KEY (id_usuario) REFERENCES usuarios (id_usuario)
        ON UPDATE CASCADE ON DELETE SET NULL,
    INDEX idx_auditoria_fecha (fecha),
    INDEX idx_auditoria_operacion (operacion)
) ENGINE = InnoDB;

-- =====================================================================
-- TABLA 13: MARCAS - RF013 (CRUD completo)
-- =====================================================================
CREATE TABLE marcas (
    id_marca INT UNSIGNED NOT NULL AUTO_INCREMENT,
    nombre   VARCHAR(80)  NOT NULL,
    PRIMARY KEY (id_marca),
    CONSTRAINT uq_marcas_nombre UNIQUE (nombre)
) ENGINE = InnoDB;

-- =====================================================================
-- TABLA 12: MODELOS - RF012 (CRUD completo, siempre asociado a una marca)
-- =====================================================================
CREATE TABLE modelos (
    id_modelo INT UNSIGNED NOT NULL AUTO_INCREMENT,
    nombre    VARCHAR(80)  NOT NULL,
    id_marca  INT UNSIGNED NOT NULL,
    PRIMARY KEY (id_modelo),
    CONSTRAINT uq_modelos_nombre_marca UNIQUE (id_marca, nombre),
    CONSTRAINT fk_modelos_marcas
        FOREIGN KEY (id_marca) REFERENCES marcas (id_marca)
        ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE = InnoDB;

-- =====================================================================
-- TABLA 10: TIPO_EQUIPO - RF010 (CRUD completo)
-- =====================================================================
CREATE TABLE tipo_equipo (
    id_tipo_equipo INT UNSIGNED NOT NULL AUTO_INCREMENT,
    nombre         VARCHAR(80)  NOT NULL,
    descripcion    VARCHAR(150) NULL,
    PRIMARY KEY (id_tipo_equipo),
    CONSTRAINT uq_tipo_equipo_nombre UNIQUE (nombre)
) ENGINE = InnoDB;

-- =====================================================================
-- TABLA 11: PROVEEDORES - RF011 (CRUD completo)
-- =====================================================================
CREATE TABLE proveedores (
    id_proveedor INT UNSIGNED NOT NULL AUTO_INCREMENT,
    nombre       VARCHAR(120) NOT NULL,
    direccion    VARCHAR(150) NOT NULL,
    telefono     VARCHAR(30)  NOT NULL,
    email        VARCHAR(100) NOT NULL,
    PRIMARY KEY (id_proveedor),
    CONSTRAINT uq_proveedores_nombre UNIQUE (nombre),
    CONSTRAINT uq_proveedores_email  UNIQUE (email)
) ENGINE = InnoDB;

-- =====================================================================
-- TABLA 6: EQUIPOS - RF006 (modulo principal)
-- La marca NO se almacena aqui: se deriva del modelo (evita dependencia
-- transitiva y respeta la 3FN); la interfaz la muestra mediante JOIN.
-- =====================================================================
CREATE TABLE equipos (
    id_equipo        INT UNSIGNED NOT NULL AUTO_INCREMENT,
    nombre           VARCHAR(120) NOT NULL,
    id_tipo_equipo   INT UNSIGNED NOT NULL,
    id_modelo        INT UNSIGNED NOT NULL,
    numero_serie     VARCHAR(60)  NOT NULL,
    garantia_meses   INT UNSIGNED NOT NULL DEFAULT 12,
    id_pais          INT UNSIGNED NOT NULL,
    id_proveedor     INT UNSIGNED NOT NULL,
    fecha_compra     DATE         NOT NULL,
    codigo_interno   VARCHAR(40)  NOT NULL,
    id_ubicacion     INT UNSIGNED NOT NULL,
    estado           ENUM('ACTIVO', 'INACTIVO') NOT NULL DEFAULT 'ACTIVO',
    fecha_registro   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id_equipo),
    CONSTRAINT uq_equipos_numero_serie   UNIQUE (numero_serie),
    CONSTRAINT uq_equipos_codigo_interno UNIQUE (codigo_interno),
    CONSTRAINT chk_equipos_garantia      CHECK (garantia_meses BETWEEN 0 AND 120),
    -- La fecha de compra no puede ser futura: se valida en Java (seccion 13)
    CONSTRAINT fk_equipos_tipo_equipo
        FOREIGN KEY (id_tipo_equipo) REFERENCES tipo_equipo (id_tipo_equipo)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_equipos_modelos
        FOREIGN KEY (id_modelo) REFERENCES modelos (id_modelo)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_equipos_paises
        FOREIGN KEY (id_pais) REFERENCES paises (id_pais)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_equipos_proveedores
        FOREIGN KEY (id_proveedor) REFERENCES proveedores (id_proveedor)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_equipos_ubicaciones
        FOREIGN KEY (id_ubicacion) REFERENCES ubicaciones (id_ubicacion)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    INDEX idx_equipos_estado (estado),
    INDEX idx_equipos_nombre (nombre)
) ENGINE = InnoDB;

-- =====================================================================
-- TABLA 14: TIPO_INTERVENCION - RF014 (CRUD opcional)
-- =====================================================================
CREATE TABLE tipo_intervencion (
    id_tipo_intervencion INT UNSIGNED NOT NULL AUTO_INCREMENT,
    nombre               VARCHAR(60)  NOT NULL,
    descripcion          VARCHAR(150) NULL,
    PRIMARY KEY (id_tipo_intervencion),
    CONSTRAINT uq_tipo_intervencion_nombre UNIQUE (nombre)
) ENGINE = InnoDB;

-- =====================================================================
-- TABLA 5: INTERVENCIONES - RF005 (alta y consulta)
-- =====================================================================
CREATE TABLE intervenciones (
    id_intervencion      INT UNSIGNED NOT NULL AUTO_INCREMENT,
    id_equipo            INT UNSIGNED NOT NULL,
    id_tipo_intervencion INT UNSIGNED NOT NULL,
    fecha                DATE         NOT NULL,
    tecnico_responsable  VARCHAR(120) NOT NULL,
    observaciones        VARCHAR(500) NOT NULL,
    PRIMARY KEY (id_intervencion),
    -- La fecha de la intervencion no puede ser futura: se valida en Java
    CONSTRAINT fk_intervenciones_equipos
        FOREIGN KEY (id_equipo) REFERENCES equipos (id_equipo)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_intervenciones_tipo
        FOREIGN KEY (id_tipo_intervencion) REFERENCES tipo_intervencion (id_tipo_intervencion)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    INDEX idx_intervenciones_fecha (fecha)
) ENGINE = InnoDB;
-- === FIN DEL ARCHIVO ===
