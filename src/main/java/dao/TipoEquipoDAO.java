package dao;

import conexion.ConexionBD;
import modelo.TipoEquipo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Acceso a la tabla TIPO_EQUIPO (RF010 - CRUD completo).
 */
public class TipoEquipoDAO extends DAOBase {

    public List<TipoEquipo> listar() throws SQLException {
        List<TipoEquipo> tipos = new ArrayList<>();
        String sql = "SELECT id_tipo_equipo, nombre, descripcion FROM tipo_equipo ORDER BY nombre";
        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                tipos.add(new TipoEquipo(rs.getInt("id_tipo_equipo"), rs.getString("nombre"),
                        rs.getString("descripcion")));
            }
        }
        return tipos;
    }

    public List<TipoEquipo> buscar(String texto) throws SQLException {
        List<TipoEquipo> tipos = new ArrayList<>();
        String sql = "SELECT id_tipo_equipo, nombre, descripcion FROM tipo_equipo "
                + "WHERE nombre LIKE ? OR descripcion LIKE ? ORDER BY nombre";
        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, "%" + texto + "%");
            ps.setString(2, "%" + texto + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tipos.add(new TipoEquipo(rs.getInt("id_tipo_equipo"), rs.getString("nombre"),
                            rs.getString("descripcion")));
                }
            }
        }
        return tipos;
    }

    public void insertar(TipoEquipo tipo) throws SQLException {
        String sql = "INSERT INTO tipo_equipo (nombre, descripcion) VALUES (?, ?)";
        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, tipo.nombre());
            ps.setString(2, tipo.descripcion());
            ps.executeUpdate();
        }
    }

    public void actualizar(TipoEquipo tipo) throws SQLException {
        String sql = "UPDATE tipo_equipo SET nombre = ?, descripcion = ? WHERE id_tipo_equipo = ?";
        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, tipo.nombre());
            ps.setString(2, tipo.descripcion());
            ps.setInt(3, tipo.idTipoEquipo());
            ps.executeUpdate();
        }
    }

    public void eliminar(int idTipo) throws SQLException {
        String sql = "DELETE FROM tipo_equipo WHERE id_tipo_equipo = ?";
        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idTipo);
            ps.executeUpdate();
        }
    }

    public boolean existeNombre(String nombre, Integer idExcluido) throws SQLException {
        String sql = "SELECT COUNT(*) FROM tipo_equipo WHERE nombre = ? AND id_tipo_equipo <> ?";
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
