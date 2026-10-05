package dao;

import conexion.ConexionBD;
import modelo.Equipo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Acceso a la tabla EQUIPOS - modulo principal (RF006).
 * Listado y busqueda resueltos con JOIN sobre tipo_equipo, modelos,
 * marcas, paises, proveedores y ubicaciones.
 */
public class EquipoDAO extends DAOBase {

    private static final String SELECT_MAESTRO =
            "SELECT e.id_equipo, e.nombre, te.nombre AS tipo, ma.nombre AS marca, "
            + "mo.nombre AS modelo, e.numero_serie, e.garantia_meses, pa.nombre AS pais, "
            + "pr.nombre AS proveedor, e.fecha_compra, e.codigo_interno, ub.nombre AS ubicacion, "
            + "e.estado, e.id_tipo_equipo, e.id_modelo, e.id_pais, e.id_proveedor, "
            + "e.id_ubicacion "
            + "FROM equipos e "
            + "JOIN tipo_equipo te  ON te.id_tipo_equipo = e.id_tipo_equipo "
            + "JOIN modelos mo      ON mo.id_modelo = e.id_modelo "
            + "JOIN marcas ma       ON ma.id_marca = mo.id_marca "
            + "JOIN paises pa       ON pa.id_pais = e.id_pais "
            + "JOIN proveedores pr  ON pr.id_proveedor = e.id_proveedor "
            + "JOIN ubicaciones ub  ON ub.id_ubicacion = e.id_ubicacion ";

    /** Filas visibles en la grilla (13 columnas). */
    public List<Object[]> listarFilas() throws SQLException {
        return consultarFilaPorFila(SELECT_MAESTRO + "ORDER BY e.id_equipo", 0, null);
    }

    /** Equipos para el combo del modulo de intervenciones. */
    public List<Equipo> listarEquipos() throws SQLException {
        List<Equipo> equipos = new ArrayList<>();
        String sql = "SELECT id_equipo, nombre, codigo_interno, estado FROM equipos "
                + "WHERE estado = 'ACTIVO' ORDER BY codigo_interno";
        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                equipos.add(new Equipo(rs.getInt("id_equipo"), rs.getString("nombre"),
                        null, null, null, null, null, null, null, rs.getString("codigo_interno"),
                        null, rs.getString("estado")));
            }
        }
        return equipos;
    }

    /** Busqueda por nombre, codigo interno, numero de serie, marca o modelo. */
    public List<Object[]> buscar(String texto) throws SQLException {
        String sql = SELECT_MAESTRO
                + "WHERE e.nombre LIKE ? OR e.codigo_interno LIKE ? OR e.numero_serie LIKE ? "
                + "OR ma.nombre LIKE ? OR mo.nombre LIKE ? ORDER BY e.id_equipo";
        String patron = "%" + texto + "%";
        return consultarFilaPorFila(sql, 5, patron);
    }

    private List<Object[]> consultarFilaPorFila(String sql, int cantidadDeParanmetros,
                                                String patron) throws SQLException {
        List<Object[]> filas = new ArrayList<>();
        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            for (int i = 1; i <= cantidadDeParanmetros; i++) {
                ps.setString(i, patron);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    filas.add(new Object[]{rs.getInt(1), rs.getString(2), rs.getString(3),
                            rs.getString(4), rs.getString(5), rs.getString(6), rs.getInt(7),
                            rs.getString(8), rs.getString(9),
                            String.valueOf(rs.getDate(10).toLocalDate()), rs.getString(11),
                            rs.getString(12), rs.getString(13)});
                }
            }
        }
        return filas;
    }

    /** Recupera el equipo completo por id (para editar en el formulario). */
    public Equipo buscarPorId(int idEquipo) throws SQLException {
        String sql = SELECT_MAESTRO + "WHERE e.id_equipo = ?";
        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idEquipo);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return new Equipo(rs.getInt("id_equipo"), rs.getString("nombre"),
                        rs.getInt("id_tipo_equipo"), rs.getInt("id_modelo"),
                        rs.getString("numero_serie"), rs.getInt("garantia_meses"),
                        rs.getInt("id_pais"), rs.getInt("id_proveedor"),
                        rs.getDate("fecha_compra").toLocalDate(), rs.getString("codigo_interno"),
                        rs.getInt("id_ubicacion"), rs.getString("estado"));
            }
        }
    }

    public void insertar(Equipo equipo) throws SQLException {
        String sql = "INSERT INTO equipos (nombre, id_tipo_equipo, id_modelo, numero_serie, "
                + "garantia_meses, id_pais, id_proveedor, fecha_compra, codigo_interno, "
                + "id_ubicacion, estado) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, equipo.nombre());
            ps.setInt(2, equipo.idTipoEquipo());
            ps.setInt(3, equipo.idModelo());
            ps.setString(4, equipo.numeroSerie());
            ps.setInt(5, equipo.garantiaMeses());
            ps.setInt(6, equipo.idPais());
            ps.setInt(7, equipo.idProveedor());
            ps.setDate(8, java.sql.Date.valueOf(equipo.fechaCompra()));
            ps.setString(9, equipo.codigoInterno());
            ps.setInt(10, equipo.idUbicacion());
            ps.setString(11, equipo.estado());
            ps.executeUpdate();
        }
    }

    public void actualizar(Equipo equipo) throws SQLException {
        String sql = "UPDATE equipos SET nombre = ?, id_tipo_equipo = ?, id_modelo = ?, "
                + "numero_serie = ?, garantia_meses = ?, id_pais = ?, id_proveedor = ?, "
                + "fecha_compra = ?, codigo_interno = ?, id_ubicacion = ?, estado = ? "
                + "WHERE id_equipo = ?";
        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, equipo.nombre());
            ps.setInt(2, equipo.idTipoEquipo());
            ps.setInt(3, equipo.idModelo());
            ps.setString(4, equipo.numeroSerie());
            ps.setInt(5, equipo.garantiaMeses());
            ps.setInt(6, equipo.idPais());
            ps.setInt(7, equipo.idProveedor());
            ps.setDate(8, java.sql.Date.valueOf(equipo.fechaCompra()));
            ps.setString(9, equipo.codigoInterno());
            ps.setInt(10, equipo.idUbicacion());
            ps.setString(11, equipo.estado());
            ps.setInt(12, equipo.idEquipo());
            ps.executeUpdate();
        }
    }

    /** Baja logica del equipo (RF006): no se borra el registro. */
    public void bajaLogica(int idEquipo) throws SQLException {
        String sql = "UPDATE equipos SET estado = 'INACTIVO' WHERE id_equipo = ?";
        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idEquipo);
            ps.executeUpdate();
        }
    }

    public boolean existeNumeroSerie(String numeroSerie, Integer idExcluido) throws SQLException {
        return existe("SELECT COUNT(*) FROM equipos WHERE numero_serie = ? AND id_equipo <> ?",
                numeroSerie, idExcluido);
    }

    public boolean existeCodigoInterno(String codigo, Integer idExcluido) throws SQLException {
        return existe("SELECT COUNT(*) FROM equipos WHERE codigo_interno = ? AND id_equipo <> ?",
                codigo, idExcluido);
    }

    private boolean existe(String sql, String valor, Integer idExcluido) throws SQLException {
        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, valor);
            ps.setInt(2, idExcluido == null ? -1 : idExcluido);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }
}
