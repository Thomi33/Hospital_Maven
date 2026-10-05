package dao;

import conexion.ConexionBD;
import modelo.Intervencion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Acceso a la tabla INTERVENCIONES (RF005 - alta y consulta).
 * Los registros historicos no se modifican.
 */
public class IntervencionDAO extends DAOBase {

    private static final String SELECT_BASE =
            "SELECT i.id_intervencion, i.fecha, e.nombre AS equipo, e.codigo_interno, "
            + "t.nombre AS tipo, i.tecnico_responsable, i.observaciones, "
            + "i.id_equipo, i.id_tipo_intervencion "
            + "FROM intervenciones i "
            + "JOIN equipos e ON e.id_equipo = i.id_equipo "
            + "JOIN tipo_intervencion t ON t.id_tipo_intervencion = i.id_tipo_intervencion ";

    /** Filas para la grilla (7 columnas visibles). */
    public List<Object[]> listarFilas() throws SQLException {
        return consultar(SELECT_BASE + "ORDER BY i.fecha DESC, i.id_intervencion DESC",
                new String[0]);
    }

    public List<Object[]> buscar(String texto) throws SQLException {
        String sql = SELECT_BASE
                + "WHERE e.nombre LIKE ? OR e.codigo_interno LIKE ? "
                + "OR i.tecnico_responsable LIKE ? OR t.nombre LIKE ? "
                + "ORDER BY i.fecha DESC, i.id_intervencion DESC";
        String patron = "%" + texto + "%";
        return consultar(sql, new String[]{patron, patron, patron, patron});
    }

    private List<Object[]> consultar(String sql, String[] parametros) throws SQLException {
        List<Object[]> filas = new ArrayList<>();
        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            for (int i = 0; i < parametros.length; i++) {
                ps.setString(i + 1, parametros[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    filas.add(new Object[]{rs.getInt(1),
                            String.valueOf(rs.getDate(2).toLocalDate()), rs.getString(3),
                            rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7)});
                }
            }
        }
        return filas;
    }

    /** Ids del equipo y del tipo de intervencion de una fila de la grilla. */
    public Intervencion buscarPorId(int idIntervencion) throws SQLException {
        String sql = "SELECT id_intervencion, id_equipo, id_tipo_intervencion, fecha, "
                + "tecnico_responsable, observaciones FROM intervenciones WHERE id_intervencion = ?";
        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idIntervencion);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return new Intervencion(rs.getInt(1), rs.getInt(2), rs.getInt(3),
                        rs.getDate(4).toLocalDate(), rs.getString(5), rs.getString(6));
            }
        }
    }

    public void insertar(Intervencion intervencion) throws SQLException {
        String sql = "INSERT INTO intervenciones (id_equipo, id_tipo_intervencion, fecha, "
                + "tecnico_responsable, observaciones) VALUES (?, ?, ?, ?, ?)";
        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, intervencion.idEquipo());
            ps.setInt(2, intervencion.idTipoIntervencion());
            ps.setDate(3, java.sql.Date.valueOf(intervencion.fecha()));
            ps.setString(4, intervencion.tecnicoResponsable());
            ps.setString(5, intervencion.observaciones());
            ps.executeUpdate();
        }
    }
}
