-- =====================================================================
-- PROYECTO INTEGRADOR
-- Sistema de Gestion de Equipamiento Clinico Hospitalario
-- ---------------------------------------------------------------------
-- Archivo : datos_prueba.sql
-- Objetivo : Cargar datos de prueba en la Base de Datos "hospital"
--            (ejecutar creacion_base.sql antes que este archivo).
-- Contrasenas: hash SHA-256 generado con SHA2(texto, 256) de MySQL.
--   admin/admin123 - tecnico/tecnico123 - supervisor/supervisor123
-- =====================================================================

USE hospital;

-- PERFILES (RF002)
INSERT INTO perfiles (id_perfil, nombre, descripcion, estado) VALUES
    (1, 'Administrador', 'Acceso total al sistema', 'ACTIVO'),
    (2, 'Tecnico Biomedico', 'Registra equipos e intervenciones', 'ACTIVO'),
    (3, 'Supervisor', 'Consulta reportes y auditoria', 'ACTIVO'),
    (4, 'Invitado', 'Perfil dado de baja, sin acceso', 'INACTIVO');

-- FUNCIONALIDADES (RF003)
INSERT INTO funcionalidades (id_funcionalidad, nombre, descripcion, estado) VALUES
    (1, 'Gestionar Usuarios', 'Alta, baja y modificacion de usuarios', 'ACTIVO'),
    (2, 'Gestionar Equipos', 'ABM del inventario de equipos clinicos', 'ACTIVO'),
    (3, 'Registrar Intervenciones', 'Alta y consulta de intervenciones', 'ACTIVO'),
    (4, 'Ver Reportes', 'Consultas con JOIN y agregaciones', 'ACTIVO'),
    (5, 'Ver Auditoria', 'Consulta de la tabla de auditoria', 'ACTIVO'),
    (6, 'Gestionar Catalogos', 'ABM de marcas, modelos y proveedores', 'ACTIVO');

INSERT INTO perfil_funcionalidad (id_perfil, id_funcionalidad) VALUES
    (1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6),
    (2, 2), (2, 3), (2, 6),
    (3, 3), (3, 4), (3, 5);

-- USUARIOS (RF001)
INSERT INTO usuarios (id_usuario, nombre, apellido, cedula, fecha_nacimiento,
                      telefono, email, usuario, contrasena, id_perfil, estado, fecha_alta) VALUES
    (1, 'Ana',    'Souza',     '5.123.456-2', '1985-04-12', '099123456', 'ana.souza@hospital.uy',
     'admin', SHA2('admin123', 256), 1, 'ACTIVO', '2024-01-10 08:00:00'),
    (2, 'Carlos', 'Perez',     '4.987.654-1', '1990-09-30', '099654321', 'carlos.perez@hospital.uy',
     'tecnico', SHA2('tecnico123', 256), 2, 'ACTIVO', '2024-02-15 08:00:00'),
    (3, 'Maria',  'Rodriguez', '3.555.777-9', '1988-11-05', '099555777', 'maria.rodriguez@hospital.uy',
     'supervisor', SHA2('supervisor123', 256), 3, 'ACTIVO', '2024-03-20 08:00:00'),
    (4, 'Lucas',  'Gomez',     '2.222.333-4', '1995-06-21', '099222333', 'lucas.gomez@hospital.uy',
     'lgomez', SHA2('lgomez123', 256), 4, 'INACTIVO', '2024-04-01 08:00:00');

-- PAISES (RF009)
INSERT INTO paises (id_pais, nombre) VALUES
    (1, 'Uruguay'),
    (2, 'Argentina'),
    (3, 'Brasil'),
    (4, 'Alemania'),
    (5, 'Estados Unidos'),
    (6, 'China'),
    (7, 'Japon');

-- INSTITUCIONES (RF008)
INSERT INTO instituciones (id_institucion, nombre, direccion, telefono, ciudad, id_pais) VALUES
    (1, 'Hospital General de Rivera', 'Av. Rivera 1234', '046221234', 'Rivera', 1),
    (2, 'Hospital Departamental de Tacuarembo', 'Ruta 5 km 3', '046312345', 'Tacuarembo', 1),
    (3, 'Centro Medico de Salto', 'Calle 15 de Mayo 456', '047323456', 'Salto', 1);

-- UBICACIONES (RF007)
INSERT INTO ubicaciones (id_ubicacion, nombre, descripcion, id_institucion) VALUES
    (1, 'CTI', 'Cuidados intensivos - piso 2', 1),
    (2, 'Emergencia', 'Area de emergencia - planta baja', 1),
    (3, 'Policlinica', 'Consulta externa', 1),
    (4, 'Laboratorio', 'Laboratorio de analisis clinicos', 1),
    (5, 'Administracion', 'Oficinas administrativas', 1),
    (6, 'Quirófano', 'Quirófanos 1 y 2', 2),
    (7, 'Imagenologia', 'Diagnostico por imagen', 3);

-- MARCAS (RF013) y MODELOS (RF012)
INSERT INTO marcas (id_marca, nombre) VALUES
    (1, 'Philips'),
    (2, 'Siemens'),
    (3, 'GE'),
    (4, 'Samsung'),
    (5, 'Mindray');

INSERT INTO modelos (id_modelo, nombre, id_marca) VALUES
    (1, 'IntelliVue MX450', 1),
    (2, 'EPIQ 7', 1),
    (3, 'MAGNETOM Sola', 2),
    (4, 'Acuson Sequoia', 2),
    (5, 'Revolution CT', 3),
    (6, 'MAC 2000', 3),
    (7, 'RS8500', 4),
    (8, 'Venus 70', 4),
    (9, 'DC-70', 5),
    (10, 'SV300', 5);

-- TIPO DE EQUIPO (RF010)
INSERT INTO tipo_equipo (id_tipo_equipo, nombre, descripcion) VALUES
    (1, 'Monitor', 'Monitorizacion de signos vitales'),
    (2, 'Respirador', 'Ventilacion mecanica'),
    (3, 'Tomografo', 'Diagnostico por imagen'),
    (4, 'Electrocardiografo', 'Registro electrico cardiaco'),
    (5, 'Ecografo', 'Ultrasonido diagnostico'),
    (6, 'Bomba de infusion', 'Infusion parenteral');

-- PROVEEDORES (RF011)
INSERT INTO proveedores (id_proveedor, nombre, direccion, telefono, email) VALUES
    (1, 'MedTech Uruguay S.A.', 'Bulevar Artigas 1420', '097111222', 'ventas@medtech.uy'),
    (2, 'BioImport Distribuidora', 'Ruta 8 km 21', '098222333', 'contacto@bioimport.uy'),
    (3, 'Siemens Healthineers Uruguay', 'Rambla Republica del Peru 1350', '099333444', 'uruguay@siemens-healthineers.com');

-- TIPO DE INTERVENCION (RF014)
INSERT INTO tipo_intervencion (id_tipo_intervencion, nombre, descripcion) VALUES
    (1, 'Preventiva', 'Revision programada para evitar fallas'),
    (2, 'Correctiva', 'Reparacion de una falla detectada'),
    (3, 'Instalacion', 'Puesta en marcha del equipo'),
    (4, 'Mantenimiento', 'Mantenimiento general programado'),
    (5, 'Calibracion', 'Ajuste y verificacion de mediciones');

-- EQUIPOS (RF006) - modulo principal
INSERT INTO equipos (id_equipo, nombre, id_tipo_equipo, id_modelo, numero_serie,
                     garantia_meses, id_pais, id_proveedor, fecha_compra,
                     codigo_interno, id_ubicacion, estado, fecha_registro) VALUES
    (1,  'Monitor de signos vitales CTI-01',    1, 1,  'PH-MX450-0001', 24, 4, 1, '2024-03-15', 'EQ-0001', 1, 'ACTIVO',   '2024-03-16 09:00:00'),
    (2,  'Monitor de signos vitales CTI-02',    1, 1,  'PH-MX450-0002', 24, 4, 1, '2024-03-15', 'EQ-0002', 1, 'ACTIVO',   '2024-03-16 09:10:00'),
    (3,  'Respirador mecanico UTI-01',          2, 10, 'MY-SV300-1001', 12, 6, 2, '2023-11-02', 'EQ-0003', 1, 'ACTIVO',   '2023-11-05 10:00:00'),
    (4,  'Respirador mecanico Emergencia',      2, 10, 'MY-SV300-1002', 12, 6, 2, '2023-11-02', 'EQ-0004', 2, 'ACTIVO',   '2023-11-05 10:20:00'),
    (5,  'Tomografo axial computarizado',       3, 5,  'GE-RCT-7788',   36, 5, 3, '2022-07-20', 'EQ-0005', 7, 'ACTIVO',   '2022-07-25 08:00:00'),
    (6,  'Electrocardiografo Policlina 1',      4, 6,  'GE-MAC2000-55', 12, 5, 1, '2024-01-10', 'EQ-0006', 3, 'ACTIVO',   '2024-01-12 08:00:00'),
    (7,  'Ecografo de alta gama',               5, 2,  'PH-EPIQ7-3344', 24, 4, 1, '2023-05-30', 'EQ-0007', 7, 'ACTIVO',   '2023-06-01 08:00:00'),
    (8,  'Ecografo portatil policlinica',       5, 9,  'MY-DC70-9911',  12, 6, 2, '2023-09-12', 'EQ-0008', 3, 'ACTIVO',   '2023-09-14 08:00:00'),
    (9,  'Monitor de signos vitales laboratorio', 1, 7, 'SS-RS8500-21', 24, 6, 3, '2024-02-08', 'EQ-0009', 4, 'ACTIVO',  '2024-02-09 08:00:00'),
    (10, 'Monitor movil quirofano 1',           1, 1,  'PH-MX450-0099', 24, 4, 1, '2022-04-04', 'EQ-0010', 6, 'INACTIVO', '2022-04-06 08:00:00'),
    (11, 'Bomba de infusion policlinica 1',     6, 3,  'SM-MSOL-6060',  12, 4, 3, '2024-05-21', 'EQ-0011', 3, 'ACTIVO',   '2024-05-22 08:00:00'),
    (12, 'Bomba de infusion CTI-03',            6, 4,  'SM-SEQ-7070',   12, 4, 3, '2024-06-18', 'EQ-0012', 1, 'ACTIVO',   '2024-06-19 08:00:00');

-- INTERVENCIONES (RF005)
INSERT INTO intervenciones (id_intervencion, id_equipo, id_tipo_intervencion, fecha,
                            tecnico_responsable, observaciones) VALUES
    (1,  1, 1, '2024-06-10', 'Carlos Perez', 'Revision general sin observaciones'),
    (2,  1, 2, '2024-09-02', 'Carlos Perez', 'Se reemplazo el cable de red de la sonda'),
    (3,  3, 4, '2024-05-14', 'Carlos Perez', 'Limpieza y cambio de filtros'),
    (4,  3, 5, '2024-08-22', 'Maria Rodriguez', 'Calibracion de mezcla de gases'),
    (5,  5, 1, '2024-04-03', 'Tecnico externo Siemens', 'Mantenimiento preventivo del tubo de rayos X'),
    (6,  5, 2, '2024-10-11', 'Tecnico externo Siemens', 'Reemplazo del ventilador de refrigeracion'),
    (7,  6, 3, '2024-01-15', 'Carlos Perez', 'Instalacion y capacitacion de uso'),
    (8,  7, 1, '2024-03-27', 'Carlos Perez', 'Actualizacion de firmware'),
    (9,  8, 5, '2024-07-19', 'Maria Rodriguez', 'Calibracion de imagen'),
    (10, 9, 2, '2024-11-05', 'Carlos Perez', 'Se reparo el conector de la bateria'),
    (11, 2, 1, '2024-06-11', 'Carlos Perez', 'Revision preventiva semestral'),
    (12, 4, 4, '2024-09-30', 'Maria Rodriguez', 'Mantenimiento de turbina y circuitos'),
    (13, 11, 3, '2024-05-25', 'Carlos Perez', 'Instalacion y prueba de flujo'),
    (14, 12, 1, '2024-07-02', 'Carlos Perez', 'Revision preventiva sin novedades');
-- === FIN DEL ARCHIVO ===
