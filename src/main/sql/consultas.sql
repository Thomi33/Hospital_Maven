-- =====================================================================
-- PROYECTO INTEGRADOR
-- Sistema de Gestion de Equipamiento Clinico Hospitalario
-- ---------------------------------------------------------------------
-- Archivo : consultas.sql
-- Objetivo : Consultas SQL complejas con JOIN, GROUP BY y funciones de
--            agregacion (objetivos especificos del proyecto).
-- Estas mismas consultas se ejecutan desde el modulo "Reportes"
-- de la aplicacion (dao.ReporteDAO).
-- =====================================================================

USE hospital;

-- 1. CANTIDAD DE EQUIPOS POR UBICACION (JOIN + GROUP BY + COUNT + HAVING)
SELECT u.nombre                       AS ubicacion,
       i.nombre                       AS institucion,
       COUNT(e.id_equipo)             AS total_equipos
FROM ubicaciones u
JOIN instituciones i ON i.id_institucion = u.id_institucion
LEFT JOIN equipos e ON e.id_ubicacion = u.id_ubicacion AND e.estado = 'ACTIVO'
GROUP BY u.nombre, i.nombre
HAVING COUNT(e.id_equipo) > 0
ORDER BY total_equipos DESC;

-- 2. INTERVENCIONES POR TIPO (JOIN + GROUP BY + COUNT + MIN + MAX)
SELECT ti.nombre                          AS tipo_intervencion,
       COUNT(iv.id_intervencion)          AS cantidad,
       MIN(iv.fecha)                      AS primera,
       MAX(iv.fecha)                      AS ultima
FROM tipo_intervencion ti
LEFT JOIN intervenciones iv ON iv.id_tipo_intervencion = ti.id_tipo_intervencion
GROUP BY ti.nombre
ORDER BY cantidad DESC;

-- 3. EQUIPOS POR MARCA (JOIN DE 3 TABLAS + GROUP BY + COUNT DISTINCT)
SELECT m.nombre                        AS marca,
       COUNT(e.id_equipo)              AS total_equipos,
       COUNT(DISTINCT mo.id_modelo)    AS modelos_distintos
FROM marcas m
JOIN modelos mo ON mo.id_marca = m.id_marca
JOIN equipos e  ON e.id_modelo = mo.id_modelo
WHERE e.estado = 'ACTIVO'
GROUP BY m.nombre
ORDER BY total_equipos DESC;

-- 4. PROVEEDORES CON EQUIPOS SUMINISTRADOS (JOIN + GROUP BY + AVG + HAVING)
SELECT p.nombre                                AS proveedor,
       p.email                                 AS email,
       COUNT(e.id_equipo)                      AS equipos_suministrados,
       ROUND(AVG(e.garantia_meses), 1)         AS garantia_promedio_meses
FROM proveedores p
LEFT JOIN equipos e ON e.id_proveedor = p.id_proveedor
GROUP BY p.nombre, p.email
HAVING COUNT(e.id_equipo) > 0
ORDER BY equipos_suministrados DESC;

-- 5. EQUIPOS POR TIPO Y ESTADO (GROUP BY + CASE)
SELECT te.nombre                                   AS tipo_equipo,
       SUM(CASE WHEN e.estado = 'ACTIVO'   THEN 1 ELSE 0 END) AS activos,
       SUM(CASE WHEN e.estado = 'INACTIVO' THEN 1 ELSE 0 END) AS dados_de_baja,
       COUNT(e.id_equipo)                                      AS total
FROM tipo_equipo te
LEFT JOIN equipos e ON e.id_tipo_equipo = te.id_tipo_equipo
GROUP BY te.nombre
ORDER BY total DESC;

-- 6. EQUIPOS POR PAIS DE ORIGEN Y PROVEEDOR (JOIN DE 4 TABLAS + COUNT)
SELECT pa.nombre AS pais, p.nombre AS proveedor, COUNT(e.id_equipo) AS total
FROM paises pa
JOIN equipos e       ON e.id_pais = pa.id_pais
JOIN proveedores p   ON p.id_proveedor = e.id_proveedor
GROUP BY pa.nombre, p.nombre
ORDER BY total DESC;

-- 7. INTERVENCIONES POR MES (GROUP BY + DATE_FORMAT + AGREGACION)
SELECT DATE_FORMAT(fecha, '%Y-%m') AS anio_mes,
       COUNT(*)                    AS intervenciones,
       COUNT(DISTINCT id_equipo)   AS equipos_atendidos
FROM intervenciones
GROUP BY DATE_FORMAT(fecha, '%Y-%m')
ORDER BY anio_mes;

-- 8. TECNICOS CON LA CANTIDAD DE TRABAJOS (GROUP BY + HAVING)
SELECT tecnico_responsable,
       COUNT(*)      AS intervenciones,
       MAX(fecha)    AS ultima_intervencion
FROM intervenciones
GROUP BY tecnico_responsable
HAVING COUNT(*) >= 2
ORDER BY intervenciones DESC;

-- 9. EQUIPOS SIN INTERVENCIONES REGISTRADAS (SUBCONSULTA NOT EXISTS)
SELECT e.codigo_interno, e.nombre, u.nombre AS ubicacion
FROM equipos e
JOIN ubicaciones u ON u.id_ubicacion = e.id_ubicacion
WHERE e.estado = 'ACTIVO'
  AND NOT EXISTS (SELECT 1 FROM intervenciones iv WHERE iv.id_equipo = e.id_equipo)
ORDER BY e.codigo_interno;

-- 10. LISTADO MAESTRO DE EQUIPOS (JOIN COMPLETO - modulo Equipos)
SELECT e.id_equipo, e.nombre, te.nombre AS tipo, m.nombre AS marca,
       mo.nombre AS modelo, e.numero_serie, e.garantia_meses,
       pa.nombre AS pais, p.nombre AS proveedor, e.fecha_compra,
       e.codigo_interno, u.nombre AS ubicacion, e.estado
FROM equipos e
JOIN tipo_equipo te  ON te.id_tipo_equipo = e.id_tipo_equipo
JOIN modelos mo      ON mo.id_modelo = e.id_modelo
JOIN marcas m        ON m.id_marca = mo.id_marca
JOIN paises pa       ON pa.id_pais = e.id_pais
JOIN proveedores p   ON p.id_proveedor = e.id_proveedor
JOIN ubicaciones u   ON u.id_ubicacion = e.id_ubicacion
ORDER BY e.id_equipo;
-- === FIN DEL ARCHIVO ===
