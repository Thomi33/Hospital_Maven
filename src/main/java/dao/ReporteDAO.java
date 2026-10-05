package dao;

import conexion.ConexionBD;
import modelo.Informe;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Ejecuta las consultas SQL complejas del proyecto (JOIN, GROUP BY y
 * funciones de agregacion). Las mismas consultas estan documentadas en
 * src/main/sql/consultas.sql.
 */
public class ReporteDAO extends DAOBase {

    private final Map<String, String> consultas = new LinkedHashMap<>();

    public ReporteDAO() {
        consultas.put("Equipos por ubicacion (GROUP BY + COUNT + HAVING)",
                "SELECT u.nombre AS ubicacion, i.nombre AS institucion, "
                + "COUNT(e.id_equipo) AS total_equipos "
                + "FROM ubicaciones u JOIN instituciones i ON i.id_institucion = u.id_institucion "
                + "LEFT JOIN equipos e ON e.id_ubicacion = u.id_ubicacion AND e.estado = 'ACTIVO' "
                + "GROUP BY u.nombre, i.nombre HAVING COUNT(e.id_equipo) > 0 "
                + "ORDER BY total_equipos DESC");

        consultas.put("Intervenciones por tipo (JOIN + GROUP BY + MIN/MAX)",
                "SELECT t.nombre AS tipo_intervencion, COUNT(i.id_intervencion) AS cantidad, "
                + "MIN(i.fecha) AS primera, MAX(i.fecha) AS ultima "
                + "FROM tipo_intervencion t LEFT JOIN intervenciones i "
                + "ON i.id_tipo_intervencion = t.id_tipo_intervencion "
                + "GROUP BY t.nombre ORDER BY cantidad DESC");

        consultas.put("Equipos por marca (JOIN de 3 tablas + COUNT DISTINCT)",
                "SELECT m.nombre AS marca, COUNT(e.id_equipo) AS total_equipos, "
                + "COUNT(DISTINCT mo.id_modelo) AS modelos_distintos "
                + "FROM marcas m JOIN modelos mo ON mo.id_marca = m.id_marca "
                + "JOIN equipos e ON e.id_modelo = mo.id_modelo AND e.estado = 'ACTIVO' "
                + "GROUP BY m.nombre ORDER BY total_equipos DESC");

        consultas.put("Proveedores con equipos suministrados (AVG + HAVING)",
                "SELECT p.nombre AS proveedor, p.email, COUNT(e.id_equipo) AS equipos_suministrados, "
                + "ROUND(AVG(e.garantia_meses), 1) AS garantia_promedio_meses "
                + "FROM proveedores p LEFT JOIN equipos e ON e.id_proveedor = p.id_proveedor "
                + "GROUP BY p.nombre, p.email HAVING COUNT(e.id_equipo) > 0 "
                + "ORDER BY equipos_suministrados DESC");

        consultas.put("Equipos por tipo y estado (GROUP BY + CASE)",
                "SELECT t.nombre AS tipo_equipo, "
                + "SUM(CASE WHEN e.estado = 'ACTIVO' THEN 1 ELSE 0 END) AS activos, "
                + "SUM(CASE WHEN e.estado = 'INACTIVO' THEN 1 ELSE 0 END) AS dados_de_baja, "
                + "COUNT(e.id_equipo) AS total FROM tipo_equipo t "
                + "LEFT JOIN equipos e ON e.id_tipo_equipo = t.id_tipo_equipo "
                + "GROUP BY t.nombre ORDER BY total DESC");

        consultas.put("Equipos por pais de origen y proveedor (JOIN de 4 tablas)",
                "SELECT pa.nombre AS pais, p.nombre AS proveedor, COUNT(e.id_equipo) AS total "
                + "FROM paises pa JOIN equipos e ON e.id_pais = pa.id_pais "
                + "JOIN proveedores p ON p.id_proveedor = e.id_proveedor "
                + "GROUP BY pa.nombre, p.nombre ORDER BY total DESC");

        consultas.put("Intervenciones por mes (DATE_FORMAT + COUNT)",
                "SELECT DATE_FORMAT(fecha, '%Y-%m') AS anio_mes, COUNT(*) AS intervenciones, "
                + "COUNT(DISTINCT id_equipo) AS equipos_atendidos FROM intervenciones "
                + "GROUP BY DATE_FORMAT(fecha, '%Y-%m') ORDER BY anio_mes");

        consultas.put("Tecnicos con 2 o mas trabajos (GROUP BY + HAVING)",
                "SELECT tecnico_responsable, COUNT(*) AS intervenciones, MAX(fecha) AS ultima "
                + "FROM intervenciones GROUP BY tecnico_responsable HAVING COUNT(*) >= 2 "
                + "ORDER BY intervenciones DESC");

        consultas.put("Equipos activos sin intervenciones (NOT EXISTS)",
                "SELECT e.codigo_interno, e.nombre, u.nombre AS ubicacion FROM equipos e "
                + "JOIN ubicaciones u ON u.id_ubicacion = e.id_ubicacion "
                + "WHERE e.estado = 'ACTIVO' AND NOT EXISTS "
                + "(SELECT 1 FROM intervenciones i WHERE i.id_equipo = e.id_equipo) "
                + "ORDER BY e.codigo_interno");
    }

    public List<String> nombresDeInformes() {
        return new ArrayList<>(consultas.keySet());
    }

    /** Ejecuta la consulta elegida y devuelve un informe listo para la grilla. */
    public Informe ejecutar(String nombreDeInforme) throws SQLException {
        String sql = consultas.get(nombreDeInforme);
        if (sql == null) {
            throw new SQLException("El informe \"" + nombreDeInforme + "\" no existe.");
        }
        List<String> columnas = new ArrayList<>();
        List<Object[]> filas = new ArrayList<>();
        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            ResultSetMetaData meta = rs.getMetaData();
            for (int i = 1; i <= meta.getColumnCount(); i++) {
                columnas.add(meta.getColumnLabel(i).replace('_', ' '));
            }
            while (rs.next()) {
                Object[] fila = new Object[meta.getColumnCount()];
                for (int i = 1; i <= fila.length; i++) {
                    fila[i - 1] = rs.getObject(i);
                }
                filas.add(fila);
            }
        }
        return new Informe(nombreDeInforme, columnas, filas);
    }
}
