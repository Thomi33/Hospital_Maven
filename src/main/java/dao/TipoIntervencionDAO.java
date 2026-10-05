package dao;

import conexion.ConexionBD;
import modelo.TipoIntervencion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Acceso a la tabla TIPO_INTERVENCION (RF014 - CRUD opcional).
 */
public class TipoIntervencionDAO extends DAOBase {

    public List<TipoIntervencion> listar() throws SQLException {
        List<TipoIntervencion> tipos = new ArrayList<>();
        String sql = "SELECT id_tipo_intervencion, nombre, descripcion "
                + "FROM tipo_intervencion ORDER BY nombre";
        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                tipos.add(new TipoIntervencion(rs.getInt("id_tipo_intervencion"),
                        rs.getString("nombre"), rs.getString("descripcion")));
            }
        }
        return tipos;
    }

    public List<TipoIntervencion> buscar(String texto) throws SQLException {
        List<TipoIntervencion> tipos = new ArrayList<>();
        String sql = "SELECT id_tipo_intervencion, nombre, descripcion FROM tipo_intervencion "
                + "WHERE nombre LIKE ? OR descripcion LIKE ? ORDER BY nombre";
        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, "%" + texto + "%");
            ps.setString(2, "%" + texto + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tipos.add(new TipoIntervencion(rs.getInt("id_tipo_intervencion"),
                            rs.getString("nombre"), rs.getString("descripcion")));
                }
            }
        }
        return tipos;
    }

    public void insertar(TipoIntervencion tipo) throws SQLException {
        String sql = "INSERT INTO tipo_intervencion (nombre, descripcion) VALUES (?, ?)";
        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, tipo.nombre());
            ps.setString(2, tipo.descripcion());
            ps.executeUpdate();
        }
    }

    public void actualizar(TipoIntervencion tipo) throws SQLException {
        String sql = "UPDATE tipo_intervencion SET nombre = ?, descripcion = ? "
                + "WHERE id_tipo_intervencion = ?";
        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, tipo.nombre());
            ps.setString(2, tipo.descripcion());
            ps.setInt(3, tipo.idTipoIntervencion());
            ps.executeUpdate();
        }
    }

    public void eliminar(int idTipo) throws SQLException {
        String sql = "DELETE FROM tipo_intervencion WHERE id_tipo_intervencion = ?";
        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idTipo);
            ps.executeUpdate();
        }
    }

    public boolean existeNombre(String nombre, Integer idExcluido) throws SQLException {
        String sql = "SELECT COUNT(*) FROM tipo_intervencion WHERE nombre = ? "
                + "AND id_tipo_intervencion <> ?";
        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setInt(2, idExcluido == null ? -1 : idExcluido);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }
}
