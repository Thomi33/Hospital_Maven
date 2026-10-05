package dao;

import conexion.ConexionBD;
import modelo.Ubicacion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Acceso a la tabla UBICACIONES (RF007 - CRUD completo).
 */
public class UbicacionDAO extends DAOBase {

    public List<Ubicacion> listar() throws SQLException {
        List<Ubicacion> ubicaciones = new ArrayList<>();
        String sql = "SELECT id_ubicacion, nombre, descripcion, id_institucion "
                + "FROM ubicaciones ORDER BY nombre";
        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ubicaciones.add(new Ubicacion(rs.getInt("id_ubicacion"), rs.getString("nombre"),
                        rs.getString("descripcion"), rs.getInt("id_institucion")));
            }
        }
        return ubicaciones;
    }

    /** Listado para la grilla: incluye la institucion (JOIN). */
    public List<Object[]> listarFilas() throws SQLException {
        List<Object[]> filas = new ArrayList<>();
        String sql = "SELECT u.id_ubicacion, u.nombre, u.descripcion, i.nombre, u.id_institucion "
                + "FROM ubicaciones u JOIN instituciones i ON i.id_institucion = u.id_institucion "
                + "ORDER BY u.nombre";
        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                filas.add(new Object[]{rs.getInt(1), rs.getString(2), rs.getString(3),
                        rs.getString(4), rs.getInt(5)});
            }
        }
        return filas;
    }

    public List<Object[]> buscar(String texto) throws SQLException {
        List<Object[]> filas = new ArrayList<>();
        String sql = "SELECT u.id_ubicacion, u.nombre, u.descripcion, i.nombre, u.id_institucion "
                + "FROM ubicaciones u JOIN instituciones i ON i.id_institucion = u.id_institucion "
                + "WHERE u.nombre LIKE ? OR u.descripcion LIKE ? OR i.nombre LIKE ? "
                + "ORDER BY u.nombre";
        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, "%" + texto + "%");
            ps.setString(2, "%" + texto + "%");
            ps.setString(3, "%" + texto + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    filas.add(new Object[]{rs.getInt(1), rs.getString(2), rs.getString(3),
                            rs.getString(4), rs.getInt(5)});
                }
            }
        }
        return filas;
    }

    public void insertar(Ubicacion ubicacion) throws SQLException {
        String sql = "INSERT INTO ubicaciones (nombre, descripcion, id_institucion) VALUES (?, ?, ?)";
        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, ubicacion.nombre());
            ps.setString(2, ubicacion.descripcion());
            ps.setInt(3, ubicacion.idInstitucion());
            ps.executeUpdate();
        }
    }

    public void actualizar(Ubicacion ubicacion) throws SQLException {
        String sql = "UPDATE ubicaciones SET nombre = ?, descripcion = ?, id_institucion = ? "
                + "WHERE id_ubicacion = ?";
        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, ubicacion.nombre());
            ps.setString(2, ubicacion.descripcion());
            ps.setInt(3, ubicacion.idInstitucion());
            ps.setInt(4, ubicacion.idUbicacion());
            ps.executeUpdate();
        }
    }

    public void eliminar(int idUbicacion) throws SQLException {
        String sql = "DELETE FROM ubicaciones WHERE id_ubicacion = ?";
        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idUbicacion);
            ps.executeUpdate();
        }
    }

    public boolean existeNombre(String nombre, Integer idExcluido) throws SQLException {
        String sql = "SELECT COUNT(*) FROM ubicaciones WHERE nombre = ? AND id_ubicacion <> ?";
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
